package org.gemoc.mbdo.gateway.config;

import org.gemoc.mbdo.gateway.handlers.MqttMessageHandler;
import org.gemoc.mbdo.gateway.service.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MqttInboundConfig {

	private final GatewayService gatewayService;
	
	@Autowired
	public MqttInboundConfig(GatewayService gatewayService) {
		this.gatewayService = gatewayService;
	}
	
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
    	String[] topics = this.gatewayService.getMonitoredMqttTopics().toArray(new String[0]);
    	log.debug("listenning mqqt topics:\n "+ String.join("\n ", topics));
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter("tcp://localhost:1883", "gatewayClient",
                		topics); // list of statically subscribed topics  
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler(MqttMessageHandler mqttMessageHandler) {
    	return mqttMessageHandler;
    }
}
