package org.gemoc.mbdo.gateway.config;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.gemoc.mbdo.gateway.handlers.MqttMessageHandler;
import org.gemoc.mbdo.gateway.service.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.ClientManager;
import org.springframework.integration.mqtt.core.Mqttv3ClientManager;
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
    public ClientManager<IMqttAsyncClient, MqttConnectOptions> clientManager() {
    	MqttConnectOptions connectionOptions = new MqttConnectOptions();
        connectionOptions.setServerURIs(new String[]{ this.gatewayService.getGatewayServiceConfiguration().mqttSourceBrokerUrl() });
        connectionOptions.setConnectionTimeout(30000);
        connectionOptions.setMaxReconnectDelay(1000);
        connectionOptions.setAutomaticReconnect(true);
        Mqttv3ClientManager clientManager = new Mqttv3ClientManager(connectionOptions, "gatewayInboundClient-"+UUID.randomUUID().toString());
        clientManager.setPersistence(new MqttDefaultFilePersistence());
        return clientManager;
    }
    
    @Bean
    public MessageProducer inbound( ClientManager<IMqttAsyncClient, MqttConnectOptions> clientManager) {
    	String[] topics = this.gatewayService.getMonitoredMqttTopics().toArray(new String[0]);
    	log.debug("listenning mqqt topics:\n "+ String.join("\n ", topics));
        
    	
    	MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(clientManager,
    			topics); // list of statically subscribed topics
    			
    	/*MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                		this.gatewayService.getGatewayServiceConfiguration().mqttSourceBrokerUrl(), 
                		"gatewayClient",
                		topics); // list of statically subscribed topics  */
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
