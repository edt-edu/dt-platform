package org.gemoc.mbdo.gateway.service;

import org.gemoc.mbdo.gateway.utils.TypeDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MqttToInfluxdbService {

	private final InfluxDBClient influxDBClient;
	
	@Autowired 
	public MqttToInfluxdbService(GatewayService gatewayService) {
		this.influxDBClient = InfluxDBClientFactory.create(gatewayService.getGatewayServiceConfiguration().influxDBConfiguration().url(), 
				gatewayService.getGatewayServiceConfiguration().influxDBConfiguration().token().toCharArray(), 
				gatewayService.getGatewayServiceConfiguration().influxDBConfiguration().org(), 
				gatewayService.getGatewayServiceConfiguration().influxDBConfiguration().bucket());
	}
	
	// Method to process MQTT messages and store them in InfluxDB 
	public void processAndStoreMessage(String measurement, String topic, String mqttMessage, boolean extractTimestampFromJsonField, boolean extractValueFromJsonField) { 
		// Logic to parse MQTT message and convert it to InfluxDB point 
		
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			// value and time extraction from mqtt
			Point point = Point.measurement(measurement)
								.addTag("topic", topic)
								.addField("message", mqttMessage)
								.time(System.currentTimeMillis(), WritePrecision.MS);
			JsonNode jsonNode = mapper.readTree(mqttMessage);
			if(jsonNode.get("timestamp") != null) {
				point.addField("timestamp", jsonNode.get("timestamp").asText());
				if(extractTimestampFromJsonField) {
					
				}
			}
			if(jsonNode.get("value") != null) {
				String value = jsonNode.get("value").asText();
				// detect value type for precise storage
				if(TypeDetector.isBoolean(value)) {
					point.addField("booleanValue", Boolean.parseBoolean(value));
				} else if(TypeDetector.isInteger(value)) {
					point.addField("integerValue", Integer.parseInt(value));
				} else if(TypeDetector.isFloat(value)) {
					point.addField("floatValue", Float.parseFloat(value));
				} else {
					point.addField("stringValue", value);
				}
			}
			// Write the point to InfluxDB 
			influxDBClient.getWriteApiBlocking().writePoint(point);
		} catch (JsonProcessingException e) {
			log.error("error parsing TimestampedValueMessage json in mqtt message", e);
		}
		log.debug("message stored in influxDB");
	}
	
}
