package org.gemoc.mbdo.gateway.dto;

import java.io.Serializable;
import java.util.List;

public record GatewayServiceConfiguration(
		String name,
		String mqttSourceBrokerUrl,
		String mqttTargetBrokerUrl,
		String kafkaBrokerUrl,
		InfluxDBConfiguration influxDBConfiguration,
		List<MqttInfluxdbRecording> mqttInfluxdbRecordings,
		List<MqttKafkaRecording> mqttKafkaRecordings,
		List<DtEventGroup> dtEventGroups) implements Serializable {
	public record InfluxDBConfiguration(
			String url,
			String token,
			String org,
			String bucket,
			Integer batchSize, // points per write request, optional (default 1000)
			Integer flushIntervalMs, // max time a point waits before its batch is written, optional (default 1000)
			Integer bufferLimit // points kept while InfluxDB is unreachable, oldest dropped beyond, optional (default 100000)
			) implements Serializable {}
	public record MqttInfluxdbRecording(
			String name,
			String mqttSourceTopic,
			String dataProcess, // possible values: extract-json-value, extract-json-value-json-timestamp, raw TODO use an enum ? 
			String timeProcess // possible values: system, extract-json-timestamp TODO use an enum ?
			) implements Serializable{}
	public record MqttKafkaRecording(
			String name,
			String mqttSourceTopic,
			String kafkaTargetTopic) implements Serializable {}
	public record DtEventGroup(
			String name,
			String dataTransformation, // possible values: clone TODO use an enum ?
			String mqttSourcePrefix,
			String mqttTargetPrefix,
			List<DtEvent> dtEvents
			) implements Serializable {}
	public record DtEvent(
			String mqttSourceTopic,
			String mqttTargetTopic
			) implements Serializable {}
}
