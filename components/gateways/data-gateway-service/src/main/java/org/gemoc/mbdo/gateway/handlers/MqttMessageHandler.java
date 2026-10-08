package org.gemoc.mbdo.gateway.handlers;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.gemoc.mbdo.gateway.config.MqttOutboundConfig.MqttOutBoundGateway;
import org.gemoc.mbdo.gateway.dto.GatewayServiceConfiguration.DtEvent;
import org.gemoc.mbdo.gateway.dto.GatewayServiceConfiguration.DtEventGroup;
import org.gemoc.mbdo.gateway.dto.GatewayServiceConfiguration.MqttInfluxdbRecording;
import org.gemoc.mbdo.gateway.dto.GatewayServiceConfiguration.MqttKafkaRecording;
import org.gemoc.mbdo.gateway.service.GatewayService;
import org.gemoc.mbdo.gateway.service.MqttToInfluxdbService;
import org.gemoc.mbdo.gateway.service.MqttToKafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

/**
 * Component in charge of handling MQTT message and run the appropriate rules
 * from the configuration
 */
@Component
@Slf4j
public class MqttMessageHandler implements MessageHandler {

    private final GatewayService gatewayService;
    private final MqttOutBoundGateway mqttOutbound;
    private final MqttToInfluxdbService mqttToInfluxdbService;
    private final MqttToKafkaService mqttToKafkaService;

    @Autowired
    public MqttMessageHandler(GatewayService gatewayService, MqttOutBoundGateway mqttOutbound,
            MqttToInfluxdbService mqttToInfluxdbService, MqttToKafkaService mqttToKafkaService) {
        this.gatewayService = gatewayService;
        this.mqttOutbound = mqttOutbound;
        this.mqttToInfluxdbService = mqttToInfluxdbService;
        this.mqttToKafkaService = mqttToKafkaService;
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
        String payload = message.getPayload().toString();
        log.debug("REceived MQTT msg, topic: " + topic + "  message: " + message.getPayload());
        this.applyCloneRules(topic, payload);
        this.applyMqttToInfluxDBRules(topic, payload);
        this.applyMqttToKafkaRules(topic, payload);
    }

    /**
     * Look for applicable MQTT clone rules and applies them
     * A clone rule will copy the message from one topic to another topic
     *
     * @param topic
     * @param payload
     */
    protected void applyCloneRules(@NotNull String topic, String payload) {
        for (DtEventGroup dtEventGroup : this.gatewayService.getGatewayServiceConfiguration().dtEventGroups()) {
            if (dtEventGroup.dataTransformation().equals("clone")) {
                for (DtEvent dtEvent : dtEventGroup.dtEvents()) {
                    String ruleSourceTopic = dtEventGroup.mqttSourcePrefix() + dtEvent.mqttSourceTopic();
                    if (MqttTopic.isMatched(ruleSourceTopic, topic)) {
                        log.debug("applying clone rule for topic " + topic);
                        String targetTopic = dtEventGroup.mqttTargetPrefix() + dtEvent.mqttTargetTopic();
                        this.mqttOutbound.sendToMqtt(targetTopic, payload);
                    }
                }
            }
        }
    }

    protected void applyMqttToInfluxDBRules(@NotNull String topic, String payload) {
        for (MqttInfluxdbRecording recording : this.gatewayService.getGatewayServiceConfiguration()
                .mqttInfluxdbRecordings()) {
            if (MqttTopic.isMatched(recording.mqttSourceTopic(), topic)) {
                log.debug("applying MqttToInfluxDB rule for topic " + topic);
                mqttToInfluxdbService.processAndStoreMessage(recording.name(),
                        topic,
                        payload,
                        "extract-json-timestamp".equals(recording.timeProcess()),
                        "extract-json-value".equals(recording.dataProcess()));
            }
        }
    }

    private void applyMqttToKafkaRules(@NotNull String topic, String payload) {
        for (MqttKafkaRecording recording : this.gatewayService.getGatewayServiceConfiguration()
                .mqttKafkaRecordings()) {
            if (MqttTopic.isMatched(recording.mqttSourceTopic(), topic)) {
                String kafkaTopic = recording.kafkaTargetTopic();
                mqttToKafkaService.processAndSendMessage(recording.name(), kafkaTopic, topic, payload);
            }
        }
    }
}
