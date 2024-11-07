package org.gemoc.mbdo.gateway;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class GatewayApplication {

	
	
	// look at https://harkesh3.medium.com/mqtt-integration-with-spring-boot-fd762f258536
	
    public static void main(String[] args) {
        new SpringApplicationBuilder(GatewayApplication.class)
        //.web(false)
        .run(args);
    }
    
}
