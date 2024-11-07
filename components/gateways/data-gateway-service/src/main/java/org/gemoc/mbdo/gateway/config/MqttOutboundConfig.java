package org.gemoc.mbdo.gateway.config;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.gemoc.mbdo.gateway.service.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.annotation.Header;

@Configuration
public class MqttOutboundConfig {
	
	private final GatewayService gatewayService;
	
	@Autowired
	public MqttOutboundConfig(GatewayService gatewayService) {
		this.gatewayService = gatewayService;
	}
	
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] { this.gatewayService.getGatewayServiceConfiguration().mqttTargetBrokerUrl() });
        options.setUserName("username");
        options.setPassword("password".toCharArray());
        options.setAutomaticReconnect(true);
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler =
                       new MqttPahoMessageHandler("gatewayOutboundClient"+UUID.randomUUID().toString(), 
                    		   mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("gateway");
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MqttOutBoundGateway {

        void sendToMqtt(String data);
        void sendToMqtt(@Header("mqtt_topic") String topic, String data);

    }
}
