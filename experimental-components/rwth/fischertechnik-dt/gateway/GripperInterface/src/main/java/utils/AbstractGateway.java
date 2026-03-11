package utils;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class AbstractGateway<T> implements MqttCallback {
    protected final MachineId machineId;
    protected List<T> observers = new ArrayList<>();
    protected final Map<String, BiConsumer<T, JsonObject>> methodsMap = new HashMap<>();

    protected AbstractGateway(MachineIdBuilder machineIdBuilder, MachineIdBuilder.ComponentType type, Map<String, BiConsumer<T, JsonObject>> methodsMap) {
        this.machineId = machineIdBuilder.withComponentType(type).build();
        this.methodsMap.putAll(methodsMap);
    }

    protected void addMethods(Map<String, BiConsumer<T, JsonObject>> methods) {
        this.methodsMap.putAll(methods);
    }

    @Override
    public void connectionLost(Throwable throwable) {
        //Called when the client lost the connection to the broker
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        if (topic != null) {
            if (!topic.startsWith(machineId.getBaseId() + "/")) {
                Log.debug("Received message for topic: " + topic + " but it does not match the machine ID: " + this.getBuiltMachineId(), "AbstractGateway.messageArrived");
                return;
            }

            if (methodsMap.containsKey(topic)) {
                String msgStr = new String(message.getPayload(), StandardCharsets.UTF_8);
                JsonElement json = JsonParser.parse(msgStr);
                JsonObject jsonObject = json.getAsJsonObject();

                BiConsumer<T, JsonObject> method = methodsMap.get(topic);
                observers.forEach(observer -> method.accept(observer, jsonObject));
            } else {
                Log.warn("No method found for topic: " + topic + " gateway: " + this.getBuiltMachineId());
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken deliveryToken) {
        //Called when an outgoing publish is complete
    }

    public void connectMqttClient(MqttClient client) throws MqttException {
        client.subscribe(machineId.getBaseId() + "/#", 2);
    }

    public String getBuiltMachineId() {
        return machineId.getBaseId();
    }


    public void addObserver(T observer) {
        observers.add(observer);
    }

    public void removeObserver(T observer) {
        observers.remove(observer);
    }
}
