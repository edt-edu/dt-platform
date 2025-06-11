package fischertechnikvisualization;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.se_rwth.commons.logging.Log;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import vacuum_gripper.GripperGateway;
import vacuum_gripper.MultiMqttCallback;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

        String gripperMachineId = "PLC/Island 1/VacuumGripper/VacuumGripper02"; // TODO: name is probably wrong
        GripperGateway gripperGateway = new GripperGateway(gripperMachineId);
        gripperGateway.connectMqttClient(gripperMachineId + "/#", client);
        multiMqttCallback.addCallback(gripperGateway);

        multiMqttCallback.addCallback(setupDataTraces());
        initStatechart();
    }

    protected MqttCallback setupDataTraces(){
        // TODO: this has prototype quality, error management should be improved
        Map<String, BooleanTopic> booleanTopicMap = new HashMap<>();
        Map<String, DoubleTopic> doubleTopicMap = new HashMap<>();

        MqttCallback updateDatatraceCallback = new MqttCallback() {
            final Gson gson = new Gson();
            public void connectionLost(Throwable throwable) {/* ignore */}
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) { /* ignore */}

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) {
                String msg = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);
                System.out.println("Processing mqtt msg on " + s + ": " + mqttMessage);
                try {
                    JsonObject obj = gson.fromJson(msg, JsonObject.class);
                    if (booleanTopicMap.containsKey(s)) {
                        BooleanTopic trace = booleanTopicMap.get(s);
                        trace.addValues(trace.sizeValues(), obj.get("value").getAsBoolean());
                    } else if(doubleTopicMap.containsKey(s)){
                        DoubleTopic trace = doubleTopicMap.get(s);
                        trace.addValues(trace.sizeValues(), obj.get("value").getAsDouble());
                    }
                } catch (Exception e){
                    Log.warn("Can not add msg on topic '" + s + "' to shadows: " + msg);
                }
            }
        };

        // TODO: we could add these automatically based on payload.value
       List<String> booleanTopics = List.of(
           "PLC/Island 1/VacuumGripper/VacuumGripper02/measurements/input/vacuumSensArmEndIn",
           "PLC/Island 1/VacuumGripper/VacuumGripper02/measurements/input/vacuumSensRotEnd",
           "PLC/Island 1/VacuumGripper/VacuumGripper02/measurements/input/vacuumSensVerticalEndUp",
           "PLC/Island 1/VacuumGripper/VacuumGripper02/measurements/internal/isExecuting",
           "PLC/Island 1/VacuumGripper/VacuumGripper02/measurements/output/vacuumActArmIn",
           "PLC/Island 1/VacuumGripper/VacuumGripper02/measurements/output/vacuumActArmOut",
           "PLC/Island 1/VacuumGripper/VacuumGripper02/measurements/output/vacuumActCompressorOn",
           "PLC/Island 1/VacuumGripper/VacuumGripper02/measurements/output/vacuumActRotLeft",
           "PLC/Island 1/VacuumGripper/VacuumGripper02/measurements/output/vacuumActRotRight",
           "PLC/Island 1/VacuumGripper/VacuumGripper02/measurements/output/vacuumActValve",
           "PLC/Island 1/VacuumGripper/VacuumGripper02/measurements/output/vacuumActVerticalDown",
           "PLC/Island 1/VacuumGripper/VacuumGripper02/measurements/output/vacuumActVerticalUp"
       );

       List<String> doubleTopics = List.of(
           "PLC/Island 1/VacuumGripper/VacuumGripper02/measurements/input/vacuumSensArmEncoderCounter",
           "PLC/Island 1/VacuumGripper/VacuumGripper02/measurements/input/vacuumSensRotEncoderCounter",
           "PLC/Island 1/VacuumGripper/VacuumGripper02/measurements/input/vacuumSensVerticalEncoderCounter"
       );

        for (String booleanTopic : booleanTopics) {
            booleanTopicMap.put(
                booleanTopic,
                FischertechnikVisualizationManager.booleanTopicBuilder()
                    .topicName(booleanTopic)
                    .build().get()
            );


        }

        for (String doubleTopic : doubleTopics) {
            doubleTopicMap.put(
                doubleTopic,
                FischertechnikVisualizationManager.doubleTopicBuilder()
                    .topicName(doubleTopic)
                    .build().get()
            );
        }

        return updateDatatraceCallback;
    }

    protected void initStatechart(){
        Statechart sc = FischertechnikVisualizationManager.statechartBuilder()
            .name("SomeStatechart").build().get();

        State a = FischertechnikVisualizationManager.stateBuilder()
            .label("A").initialState(true).build().get();

        State b = FischertechnikVisualizationManager.stateBuilder()
            .label("B").color(Optional.of("red")).build().get();

        State c = FischertechnikVisualizationManager.stateBuilder()
            .label("C").finalState(true).build().get();

        sc.addStates(a);
        sc.addStates(b);
        sc.addStates(c);

        Transition aToB = FischertechnikVisualizationManager.transitionBuilder()
            .source(a).target(b).label(Optional.of("hello")).build().get();

        Transition bToC = FischertechnikVisualizationManager.transitionBuilder()
            .source(b).target(c).color(Optional.of("green")).build().get();

        sc.addTransitions(aToB);
        sc.addTransitions(bToC);
    }
}
