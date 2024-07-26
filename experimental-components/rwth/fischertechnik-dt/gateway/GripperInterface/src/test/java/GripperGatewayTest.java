import org.eclipse.paho.client.mqttv3.*;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GripperGatewayTest{

  @Test
  public void testGripperGatewayMultipleObserversOneTopic() throws MqttException, InterruptedException {

    Random r = new Random();

    //create gateway
    GripperGateway gripperGateway = new GripperGateway("vacuum-gripper");

    // list for messages received by observers
    List<Boolean> observerMsgs1 = new ArrayList<>();
    List<Boolean> observerMsgs2 = new ArrayList<>();

    //create first observer
    GripperObserver observer1 = new GripperObserver() {
      @Override
      public void onRefSwitchVertical(boolean bool) {
        observerMsgs1.add(bool);
      }
    };

    //create second observer
    GripperObserver observer2 = new GripperObserver() {
      @Override
      public void onRefSwitchVertical(boolean bool) {
        observerMsgs2.add(bool);
      }
    };

    gripperGateway.observers.add(observer1);
    gripperGateway.observers.add(observer2);

    // client
    MqttClient client = new MqttClient(
        "tcp://localhost:1883",
        // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
        "client" + r.nextInt());
    client.setCallback(gripperGateway);

    client.connect();

    gripperGateway.connectMqttClient("/vacuum-gripper/ref-switch-vertical", client);
    System.out.println("Client connected");

    // publisher
    MqttClient publisher = new MqttClient(
        "tcp://localhost:1883",
        "publisher1");

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
    assertEquals(false, observerMsgs1.get(0));
    assertEquals(true, observerMsgs1.get(1));
    assertEquals(false, observerMsgs1.get(2));

    assertEquals(false, observerMsgs2.get(0));
    assertEquals(true, observerMsgs2.get(1));
    assertEquals(false, observerMsgs2.get(2));
  }

  @Test
  public void testGripperGatewayOneObserverMultipleTopics() throws MqttException, InterruptedException {

    Random r = new Random();

    //create gateway
    GripperGateway gripperGateway = new GripperGateway("vacuum-gripper");

    // list for messages received by observers
    List<Boolean> observerMsgs1 = new ArrayList<>();

    //create first observer
    GripperObserver observer1 = new GripperObserver() {
      @Override
      public void onRefSwitchVertical(boolean bool) {
        observerMsgs1.add(bool);
      }

      @Override
      public void onRefSwitchHorizontal(boolean bool){
        observerMsgs1.add(bool);
      }
    };

    gripperGateway.observers.add(observer1);

    // client
    MqttClient client = new MqttClient(
        "tcp://localhost:1883",
        // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
        "client" + r.nextInt());
    client.setCallback(gripperGateway);

    client.connect();

    gripperGateway.connectMqttClient("/vacuum-gripper/+", client);
    System.out.println("Client connected");

    // publisher
    MqttClient publisher = new MqttClient(
        "tcp://localhost:1883",
        "publisher2");

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
    publisher.publish("/vacuum-gripper/ref-switch-horizontal", msg3);

    MqttMessage msg4 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg4.setQos(2);
    publisher.publish("/vacuum-gripper/ref-switch-horizontal", msg4);

    Thread.sleep(100L);

    // check if both callbacks received the same massages
    assertEquals(false, observerMsgs1.get(0));
    assertEquals(true, observerMsgs1.get(1));
    assertEquals(false, observerMsgs1.get(2));
    assertEquals(true, observerMsgs1.get(3));
  }
}