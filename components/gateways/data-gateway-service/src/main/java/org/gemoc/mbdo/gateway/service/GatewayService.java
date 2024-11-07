package org.gemoc.mbdo.gateway.service;

import org.gemoc.mbdo.gateway.GatewayApplicationEnvironment;
import org.gemoc.mbdo.gateway.dto.GatewayServiceConfiguration;
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
}
