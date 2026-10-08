package org.gemoc.mbdo.gateway.service;

import org.gemoc.mbdo.gateway.dto.GatewayServiceConfiguration.InfluxDBConfiguration;
import org.gemoc.mbdo.gateway.utils.TypeDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteOptions;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.client.write.events.BackpressureEvent;
import com.influxdb.client.write.events.WriteErrorEvent;
import com.influxdb.client.write.events.WriteRetriableErrorEvent;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

/**
 * Records MQTT messages in InfluxDB. Points are queued and written in batches
 * by the InfluxDB client's own background
 * thread, so the MQTT callback thread never waits for InfluxDB: a batch is
 * written once it holds {@code batchSize}
 * points or its oldest point has waited {@code flushIntervalMs}. Each point
 * keeps the time its message was received.
 */
@Slf4j
@Service
public class MqttToInfluxdbService {

	static final int DEFAULT_BATCH_SIZE = 1000;
	static final int DEFAULT_FLUSH_INTERVAL_MS = 1000;
	static final int DEFAULT_BUFFER_LIMIT = 100_000;

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private final InfluxDBClient influxDBClient;
	private final WriteApi writeApi;

	@Autowired
	public MqttToInfluxdbService(GatewayService gatewayService) {
		InfluxDBConfiguration configuration = gatewayService.getGatewayServiceConfiguration().influxDBConfiguration();
		this.influxDBClient = InfluxDBClientFactory.create(configuration.url(),
				configuration.token().toCharArray(),
				configuration.org(),
				configuration.bucket());
		this.writeApi = influxDBClient.makeWriteApi(writeOptions(configuration));
		writeApi.listenEvents(WriteErrorEvent.class,
				event -> log.error("InfluxDB batch write failed, its points are lost", event.getThrowable()));
		writeApi.listenEvents(WriteRetriableErrorEvent.class,
				event -> log.warn("InfluxDB batch write failed, retrying in {} ms: {}",
						event.getRetryInterval(), event.getThrowable().getMessage()));
		writeApi.listenEvents(BackpressureEvent.class,
				event -> log.warn("InfluxDB write buffer full ({}), the oldest points are dropped", event.getReason()));
	}

	static WriteOptions writeOptions(InfluxDBConfiguration configuration) {
		return WriteOptions.builder()
				.batchSize(orDefault(configuration.batchSize(), DEFAULT_BATCH_SIZE))
				.flushInterval(orDefault(configuration.flushIntervalMs(), DEFAULT_FLUSH_INTERVAL_MS))
				.bufferLimit(orDefault(configuration.bufferLimit(), DEFAULT_BUFFER_LIMIT))
				.build();
	}

	private static int orDefault(Integer value, int defaultValue) {
		return value == null || value <= 0 ? defaultValue : value;
	}

	/** Writes the points still queued, then closes the connection. */
	@PreDestroy
	void close() {
		writeApi.close();
		influxDBClient.close();
	}

	/**
	 * Queues an MQTT message for InfluxDB and returns immediately: the point is written with the next batch, by the
	 * InfluxDB client's background thread. The point is timestamped now, when the message is received, and holds the
	 * raw message, its JSON {@code timestamp} field as text, and its JSON {@code value} field typed as
	 * {@code booleanValue}, {@code integerValue}, {@code floatValue} or {@code stringValue}. A message that is not JSON
	 * is logged and not recorded.
	 *
	 * @param measurement the InfluxDB measurement to record into, the {@code name} of the matching recording rule
	 *                    (e.g. "Physical_Twin_VGR1")
	 * @param topic the MQTT topic the message was received on, recorded as the {@code topic} tag
	 * @param mqttMessage the MQTT payload, a JSON object with optional {@code value} and {@code timestamp} fields
	 * @param extractTimestampFromJsonField not used yet ({@code timeProcess: extract-json-timestamp}): the point time
	 *                                      is always the reception time, the JSON timestamp is stored as a field
	 * @param extractValueFromJsonField not used yet ({@code dataProcess: extract-json-value}): the JSON value is
	 *                                  always extracted when present
	 */
	public void processAndStoreMessage(String measurement, String topic, String mqttMessage,
			boolean extractTimestampFromJsonField, boolean extractValueFromJsonField) {
		Point point = toPoint(measurement, topic, mqttMessage, System.currentTimeMillis());
		if (point != null) {
			// non-blocking: the point is written with the next batch
			writeApi.writePoint(point);
			log.debug("message queued for influxDB");
		}
	}

	/**
	 * The point recording an MQTT message received at {@code receivedAtMillis},
	 * null if the message is not JSON.
	 */
	static Point toPoint(String measurement, String topic, String mqttMessage, long receivedAtMillis) {
		try {
			// value and time extraction from mqtt
			Point point = Point.measurement(measurement)
					.addTag("topic", topic)
					.addField("message", mqttMessage)
					.time(receivedAtMillis, WritePrecision.MS);
			JsonNode jsonNode = MAPPER.readTree(mqttMessage);
			if (jsonNode.get("timestamp") != null) {
				point.addField("timestamp", jsonNode.get("timestamp").asText());
			}
			if (jsonNode.get("value") != null) {
				String value = jsonNode.get("value").asText();
				// detect value type for precise storage
				if (TypeDetector.isBoolean(value)) {
					point.addField("booleanValue", Boolean.parseBoolean(value));
				} else if (TypeDetector.isInteger(value)) {
					point.addField("integerValue", Integer.parseInt(value));
				} else if (TypeDetector.isFloat(value)) {
					point.addField("floatValue", Float.parseFloat(value));
				} else {
					point.addField("stringValue", value);
				}
			}
			return point;
		} catch (JsonProcessingException e) {
			log.error("error parsing TimestampedValueMessage json in mqtt message", e);
			return null;
		}
	}

}
