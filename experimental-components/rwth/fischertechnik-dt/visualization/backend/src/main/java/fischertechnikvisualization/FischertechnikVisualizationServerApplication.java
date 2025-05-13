package fischertechnikvisualization;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import vacuum_gripper.GripperGateway;
import vacuum_gripper.MultiMqttCallback;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Random;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class}, scanBasePackages = {"umlp.backendrte.service.websocket",
        "umlp.backendrte.service.rest", "service"})
public class FischertechnikVisualizationServerApplication extends FischertechnikVisualizationServerApplicationTOP {

    public static void main(String[] args) {
        SpringApplication.run(FischertechnikVisualizationServerApplication.class, args);
    }

    @PostConstruct
    @Override
    public void init() throws IOException {
        super.init();
        try {
            connectToMqtt();
        }catch (MqttException e){
            throw new IOException(e);
        }
    }

    protected void connectToMqtt() throws MqttException {
        Random r = new Random();
        MultiMqttCallback multiMqttCallback = new MultiMqttCallback();

        MqttClient client = new MqttClient(
                "tcp://localhost:1883",
                "client" + r.nextInt());

        client.setCallback(multiMqttCallback);
        client.connect();

        String gripperMachineId = "VacuumGripper01"; // TODO: name is probably wrong
        GripperGateway gripperGateway = new GripperGateway(gripperMachineId);
        gripperGateway.connectMqttClient(gripperMachineId + "/#", client);
        multiMqttCallback.addCallback(gripperGateway);
    }
}
