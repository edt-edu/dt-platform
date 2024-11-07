package org.gemoc.mbdo.gateway.handlers;

import org.gemoc.mbdo.gateway.config.MqttOutboundConfig.MqttOutBoundGateway;
import org.gemoc.mbdo.gateway.dto.GatewayServiceConfiguration.DtEvent;
import org.gemoc.mbdo.gateway.dto.GatewayServiceConfiguration.DtEventGroup;
import org.gemoc.mbdo.gateway.service.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;


/**
 * Component in charge of handling MQTT message and run the appropriate rules from the configuration
 */
@Component
@Slf4j
public class MqttMessageHandler implements MessageHandler {

	private final GatewayService gatewayService;
	private final MqttOutBoundGateway mqttOutbound;
	
	@Autowired 
	public MqttMessageHandler(GatewayService gatewayService, MqttOutBoundGateway mqttOutbound) { 
		this.gatewayService = gatewayService;
		this.mqttOutbound = mqttOutbound;
	}
	
	@Override
	public void handleMessage(Message<?> message) throws MessagingException {
		String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
		String payload = message.getPayload().toString();
		log.debug("REceived MQTT msg, topic: "+ topic + "  message: " + message.getPayload());
		for (DtEventGroup dtEventGroup : this.gatewayService.getGatewayServiceConfiguration().dtEventGroups()) {
			log.info("dtEventGroups() loop "+dtEventGroup.name());
		}
		this.applyCloneRules(topic, payload);
	}
	
	
	/**
	 * Look for applicable MQTT clone rules and applies them
	 * A clone rule will copy the message from one topic to another topic
	 * @param topic
	 * @param payload
	 */
	protected void applyCloneRules(@NotNull String topic, String payload) {
		for (DtEventGroup dtEventGroup : this.gatewayService.getGatewayServiceConfiguration().dtEventGroups()) {
			if(dtEventGroup.dataTransformation().equals("clone")) {
				for (DtEvent dtEvent : dtEventGroup.dtEvents()) {
					String ruleSourceTopic = dtEventGroup.mqttSourcePrefix()+dtEvent.mqttSourceTopic();
					if(topic.equals(ruleSourceTopic)) {
						log.debug("applying clone rule for topic " + topic);						
						String targetTopic = dtEventGroup.mqttTargetPrefix()+dtEvent.mqttTargetTopic();
						this.mqttOutbound.sendToMqtt(targetTopic, payload);
					}
				}
			}
		}
	}

}
