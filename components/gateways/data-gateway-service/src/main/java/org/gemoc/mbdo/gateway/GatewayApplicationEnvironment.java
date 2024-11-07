package org.gemoc.mbdo.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Getter
@Slf4j
public class GatewayApplicationEnvironment {

	/**
     * The file path for the main configuration file.
     * This is set to "classpath:configuration.yml" by default.
     */
    private String configurationFilePath = "classpath:configuration.yml";
    
    @Autowired
    public GatewayApplicationEnvironment(ApplicationArguments args) {
        String argumentName = "configuration.path";
        if (args.containsOption(argumentName)) {
            configurationFilePath = "file:" + args.getOptionValues(argumentName).getFirst();
            System.out.println("--> " + configurationFilePath);
        } else {
            log.warn("No configuration file provided. The default configuration file is being used. (ie. \"classpath:configuration.yml\")");
            log.info("you can specify the configuration file using the option --configuration.path=/app/config/configuration.yml");
            configurationFilePath = "classpath:configuration.yml";
        }
    }
}
