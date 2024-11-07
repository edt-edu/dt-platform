package org.gemoc.mbdo.gateway.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.gemoc.mbdo.gateway.GatewayApplicationEnvironment;
import org.gemoc.mbdo.gateway.dto.GatewayServiceConfiguration;
import org.gemoc.mbdo.gateway.dto.GatewayServiceConfiguration.DtEvent;
import org.gemoc.mbdo.gateway.dto.GatewayServiceConfiguration.DtEventGroup;
import org.gemoc.mbdo.gateway.dto.GatewayServiceConfiguration.MqttInfluxdbRecording;
import org.gemoc.mbdo.gateway.utils.YamlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import lombok.Getter;


@Getter
@Service
public class GatewayService {

	
	private final ApplicationContext applicationContext;
	private final GatewayServiceConfiguration gatewayServiceConfiguration;
	private final GatewayApplicationEnvironment gatewayApplicationEnvironment;
	
	@Autowired
	public GatewayService(GatewayApplicationEnvironment gatewayApplicationEnvironment, ApplicationContext applicationContext) {
		this.gatewayApplicationEnvironment = gatewayApplicationEnvironment;
		this.applicationContext = applicationContext;
		this.gatewayServiceConfiguration = loadGatewayServiceConfiguration();
	}
	
    /**
     * Retrieves the gateway configuration by converting a YAML file located at the specified path
     * into an instance of {@link GatewayServiceConfiguration} using Jackson ObjectMapper.
     *
     * @return The GatewayServiceConfiguration instance parsed from the YAML file.
     * @throws RuntimeException If there is an error during YAML parsing or file reading.
     */
	protected GatewayServiceConfiguration loadGatewayServiceConfiguration() {
		return YamlUtils.convertYamlToObject(applicationContext, gatewayApplicationEnvironment.getConfigurationFilePath(), GatewayServiceConfiguration.class);
	}
	
	/**
	 * compute form configuration the list of Mqtt topics that the gateway must listen
	 * @return
	 */
	public List<String> getMonitoredMqttTopics() {
		HashSet<String> monitoredTopics = new HashSet<String>();
		for ( MqttInfluxdbRecording influxRecording : this.getGatewayServiceConfiguration().mqttInfluxdbRecordings()) {
			monitoredTopics.add(influxRecording.mqttSourceTopic());
		}
		for (DtEventGroup dtEventGroup : this.getGatewayServiceConfiguration().dtEventGroups()) {
			for (DtEvent dtEvent : dtEventGroup.dtEvents()) {
				monitoredTopics.add(dtEventGroup.mqttSourcePrefix()+dtEvent.mqttSourceTopic());
			}
		}
		// TODO remove useless sub rules (for example when using mqtt topic wildcard "." or "#"
		List<String> result = new ArrayList<>(monitoredTopics); 
		Collections.sort(result);
		return result;
	}
}
