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
import java.time.LocalDateTime;
import java.util.*;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class}, scanBasePackages = {"umlp.backendrte.service.websocket",
        "umlp.backendrte.service.rest", "service", "fischertechnikvisualization"})
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
        gripperGateway.connectMqttClient("#", client);
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
                        trace.addValues(
                            trace.sizeValuess(),
                            FischertechnikVisualizationManager.booleanValueBuilder()
                                .content(obj.get("value").getAsBoolean())
                                .timestamp(LocalDateTime.now())
                                .build().get()
                        );
                    } else if(doubleTopicMap.containsKey(s)){
                        DoubleTopic trace = doubleTopicMap.get(s);
                        trace.addValues(
                            trace.sizeValuess(),
                            FischertechnikVisualizationManager.doubleValueBuilder()
                                .content(obj.get("value").getAsDouble())
                                .timestamp(LocalDateTime.now())
                                .build().get()
                        );
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
           "PLC/Island 1/VacuumGripper/VacuumGripper02/measurements/output/vacuumActVerticalUp",
           // Rennes topics
           "PLC/RevPi01/VacuumGripper/VacuumGripper01/measurements/input/vacuumActValve"
       );

       List<String> doubleTopics = List.of(
           "PLC/Island 1/VacuumGripper/VacuumGripper02/measurements/input/vacuumSensArmEncoderCounter",
           "PLC/Island 1/VacuumGripper/VacuumGripper02/measurements/input/vacuumSensRotEncoderCounter",
           "PLC/Island 1/VacuumGripper/VacuumGripper02/measurements/input/vacuumSensVerticalEncoderCounter",
           // Rennes topics
           "PLC/RevPi01/VacuumGripper/VacuumGripper01/measurements/input/vacuumSensRotEncoderCounter",
           "PLC/RevPi01/VacuumGripper/VacuumGripper01/measurements/input/vacuumSensArmEncoderCounter",
           "PLC/RevPi01/VacuumGripper/VacuumGripper01/measurements/input/vacuumSensVerticalEncoderCounter"
       );

        for (String booleanTopic : booleanTopics) {
            BooleanTopic bt = FischertechnikVisualizationManager.booleanTopicBuilder()
                .topicName(booleanTopic)
                .build().get();
            booleanTopicMap.put(
                booleanTopic,
                bt
            );

            if(booleanTopic.contains("RevPi01")){
                FischertechnikVisualizationManager.getApp().addBooleanTopic(bt);
            }
        }

        for (String doubleTopic : doubleTopics) {
            DoubleTopic dt = FischertechnikVisualizationManager.doubleTopicBuilder()
                .topicName(doubleTopic)
                .build().get();
            doubleTopicMap.put(
                doubleTopic,
                dt
            );

            if(doubleTopic.contains("RevPi01")){
                FischertechnikVisualizationManager.getApp().addDoubleTopic(dt);
            }
        }

        return updateDatatraceCallback;
    }

    protected void initStatechart(){
        Statechart sc = FischertechnikVisualizationManager.statechartBuilder()
            .name("VGRSystemStates").build().get();

        // Top-level states (flattened, includes move's substates)
        State idle = FischertechnikVisualizationManager.stateBuilder()
            .label("idle").initialState(true).build().get();

        State setup = FischertechnikVisualizationManager.stateBuilder()
            .label("setup").build().get();

        State move = FischertechnikVisualizationManager.stateBuilder()
            .label("move").build().get();

        State stop = FischertechnikVisualizationManager.stateBuilder()
            .label("stop").build().get();

        // move substates (flattened)
        State prepare_move = FischertechnikVisualizationManager.stateBuilder()
            .label("prepare_move").build().get();

        State setup_before_move = FischertechnikVisualizationManager.stateBuilder()
            .label("setup_before_move").build().get();

        State moveStep01_gotoPickPosition = FischertechnikVisualizationManager.stateBuilder()
            .label("moveStep01_gotoPickPosition").build().get();

        State moveStep02_pickToken = FischertechnikVisualizationManager.stateBuilder()
            .label("moveStep02_pickToken").build().get();

        State moveStep03_gotoDropPos = FischertechnikVisualizationManager.stateBuilder()
            .label("moveStep03_gotoDropPos").build().get();

        State moveStep04_dropToken = FischertechnikVisualizationManager.stateBuilder()
            .label("moveStep04_dropToken").build().get();

        State move_done = FischertechnikVisualizationManager.stateBuilder()
            .label("move_done").build().get();

        // Add all states to the statechart
        sc.addStates(idle);
        sc.addStates(setup);
        sc.addStates(move);
        sc.addStates(stop);

        sc.addStates(prepare_move);
        sc.addStates(setup_before_move);
        sc.addStates(moveStep01_gotoPickPosition);
        sc.addStates(moveStep02_pickToken);
        sc.addStates(moveStep03_gotoDropPos);
        sc.addStates(moveStep04_dropToken);
        sc.addStates(move_done);

        // Transitions (label contains name + key guard/accept info from SysML for traceability)
        Transition prep_move_to_setup = FischertechnikVisualizationManager.transitionBuilder()
            .source(prepare_move)
            .target(setup_before_move) // SysML: then setup_before_move (prep_move -> setup_before_move)
            .label(Optional.of("prep_move_to_setup")).build().get();

        Transition setup_before_move_to_step_1 = FischertechnikVisualizationManager.transitionBuilder()
            .source(setup_before_move)
            .target(moveStep01_gotoPickPosition)
            .label(Optional.of("setup_before_move_to_step_1")).build().get();

        Transition prep_move_to_step1 = FischertechnikVisualizationManager.transitionBuilder()
            .source(prepare_move)
            .target(moveStep01_gotoPickPosition)
            .label(Optional.of("prep_move_to_step1")).build().get();

        Transition move_step_1_to_move_step_2 = FischertechnikVisualizationManager.transitionBuilder()
            .source(moveStep01_gotoPickPosition)
            .target(moveStep02_pickToken)
            .label(Optional.of("move_step_1_to_move_step_2")).build().get();

        Transition move_step_2_to_move_step_3 = FischertechnikVisualizationManager.transitionBuilder()
            .source(moveStep02_pickToken)
            .target(moveStep03_gotoDropPos)
            .label(Optional.of("move_step_2_to_move_step_3")).build().get();

        Transition move_step_3_to_move_step_4 = FischertechnikVisualizationManager.transitionBuilder()
            .source(moveStep03_gotoDropPos)
            .target(moveStep04_dropToken)
            .label(Optional.of("move_step_3_to_move_step_4")).build().get();

        Transition move_step_4_to_move_done = FischertechnikVisualizationManager.transitionBuilder()
            .source(moveStep04_dropToken)
            .target(move_done)
            .label(Optional.of("move_step_4_to_move_done")).build().get();

        // Top-level transitions between major states (with accept/guards/actions summarized in labels)
        Transition idle_to_setup = FischertechnikVisualizationManager.transitionBuilder()
            .source(idle)
            .target(setup)
            .label(Optional.of("idle_to_setup")).build().get();

        Transition move_to_setup = FischertechnikVisualizationManager.transitionBuilder()
            .source(move)
            .target(setup)
            .label(Optional.of("move_to_setup")).build().get();

        Transition setup_to_idle = FischertechnikVisualizationManager.transitionBuilder()
            .source(setup)
            .target(idle)
            .label(Optional.of("setup_to_idle")).build().get();

        Transition idle_to_move = FischertechnikVisualizationManager.transitionBuilder()
            .source(idle)
            .target(move)
            .label(Optional.of("idle_to_move")).build().get();

        Transition setup_to_move = FischertechnikVisualizationManager.transitionBuilder()
            .source(setup)
            .target(move)
            .label(Optional.of("setup_to_move")).build().get();

        Transition move_to_idle = FischertechnikVisualizationManager.transitionBuilder()
            .source(move)
            .target(idle)
            .label(Optional.of("move_to_idle")).build().get();

        Transition idle_to_stop = FischertechnikVisualizationManager.transitionBuilder()
            .source(idle)
            .target(stop)
            .label(Optional.of("idle_to_stop")).build().get();

        Transition setup_to_stop = FischertechnikVisualizationManager.transitionBuilder()
            .source(setup)
            .target(stop)
            .label(Optional.of("setup_to_stop")).build().get();

        Transition move_to_stop = FischertechnikVisualizationManager.transitionBuilder()
            .source(move)
            .target(stop)
            .label(Optional.of("move_to_stop")).build().get();

        Transition stop_to_idle = FischertechnikVisualizationManager.transitionBuilder()
            .source(stop)
            .target(idle)
            .label(Optional.of("stop_to_idle")).build().get();

        // Add transitions to the statechart
        sc.addTransitions(prep_move_to_setup);
        sc.addTransitions(setup_before_move_to_step_1);
        sc.addTransitions(prep_move_to_step1);
        sc.addTransitions(move_step_1_to_move_step_2);
        sc.addTransitions(move_step_2_to_move_step_3);
        sc.addTransitions(move_step_3_to_move_step_4);
        sc.addTransitions(move_step_4_to_move_done);

        sc.addTransitions(idle_to_setup);
        sc.addTransitions(move_to_setup);
        sc.addTransitions(setup_to_idle);
        sc.addTransitions(idle_to_move);
        sc.addTransitions(setup_to_move);
        sc.addTransitions(move_to_idle);

        sc.addTransitions(idle_to_stop);
        sc.addTransitions(setup_to_stop);
        sc.addTransitions(move_to_stop);
        sc.addTransitions(stop_to_idle);
    }
}
