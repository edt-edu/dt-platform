import org.eclipse.paho.client.mqttv3.*;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MultiMqttCallbackTest {

  @Test
  public void testMultiMqttCallback() throws MqttException, InterruptedException {

    Random r = new Random();

    // create gateway
    GripperGateway gateway = new GripperGateway("vacuum-gripper");

    // create MultiMqttCallback list
    MultiMqttCallback clientOneCallbacks = new MultiMqttCallback();

    // list for messages received by client
    List<String> clientMsgs1 = new ArrayList<>();
    List<String> clientMsgs2 = new ArrayList<>();

    // client
    MqttClient client = new MqttClient(
        "tcp://localhost:1883",
        // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
        "client" + r.nextInt());
    client.setCallback(clientOneCallbacks);

    // first callback
    clientOneCallbacks.addCallback(new MqttCallback() {
      @Override
      public void connectionLost(Throwable throwable) {

      }

      @Override
      public void messageArrived(String topic, MqttMessage message) throws Exception {
        clientMsgs1.add(new String(message.getPayload(), StandardCharsets.UTF_8));
      }

      @Override
      public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

      }
    });

    // second callback
    clientOneCallbacks.addCallback(new MqttCallback() {
      @Override
      public void connectionLost(Throwable throwable) {

      }

      @Override
      public void messageArrived(String topic, MqttMessage message) throws Exception {
        clientMsgs2.add(new String(message.getPayload(), StandardCharsets.UTF_8));
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

    // sending messages
    System.out.println("Publishing");
    MqttMessage msg1 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg1.setQos(2);
    publisher.publish("/vacuum-gripper/ref-switch-vertical", msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/vacuum-gripper/ref-switch-vertical", msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/vacuum-gripper/ref-switch-vertical", msg3);

    Thread.sleep(100L);

    // check if both callbacks received the same massages
    assertEquals(clientMsgs1.get(0), clientMsgs2.get(0));
    assertEquals(clientMsgs1.get(1), clientMsgs2.get(1));
    assertEquals(clientMsgs1.get(2), clientMsgs2.get(2));
  }
}