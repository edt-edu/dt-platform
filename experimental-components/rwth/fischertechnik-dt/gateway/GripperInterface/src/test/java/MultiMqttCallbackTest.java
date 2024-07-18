import org.eclipse.paho.client.mqttv3.*;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultiMqttCallbackTest {

  @Test
  public void testMultiMqttCallback() throws MqttException {

    // create gateway
    GripperGateway gateway = new GripperGateway("vacuum-gripper");

    // create MultiMqttCallback list
    MultiMqttCallback clientOneCallbacks = new MultiMqttCallback();

    // list for messages received by client
    List<String> clientMsgs = new ArrayList<>();

    // client
    MqttClient client = new MqttClient(
        "tcp://localhost:1883",
        "client1"); // TODO: move me up 1 level
    client.setCallback(clientOneCallbacks);

    client = gateway.createNewMqttClient(clientOneCallbacks ,client);

    clientOneCallbacks.addCallback(new MqttCallback() {
      @Override
      public void connectionLost(Throwable throwable) {

      }

      @Override
      public void messageArrived(String topic, MqttMessage message) throws Exception {
        clientMsgs.add(new String(message.getPayload(), StandardCharsets.UTF_8));
      }

      @Override
      public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

      }
    });

    client.connect();

    gateway.connectMqttClient("/vacuum-gripper/ref-switch-vertical", client);
    System.out.println("Client connected");

    // publisher
    MqttClient publisher = new MqttClient(
        "tcp://localhost:1883",
        "publisher");

    publisher.connect();
    System.out.println("Publisher connected");


    System.out.println("Publishing");
    MqttMessage msg1 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg1.setQos(2);
    publisher.publish("/vacuum-gripper/ref-switch-vertical", msg1);

    MqttMessage msg2 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/vacuum-gripper/ref-switch-vertical", msg2);



    // TODO: implement corret assertion
    assertTrue(false);
  }
}