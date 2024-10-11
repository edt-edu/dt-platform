import org.eclipse.paho.client.mqttv3.*;
import org.junit.jupiter.api.Test;
import punching_machine.PunchingMachineGateway;
import punching_machine.PunchingMachineObserver;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PunchingMachineGatewayTest {

  @Test
  public void testPunchingMachineGatewayMultipleObserversOneTopic() throws MqttException, InterruptedException {

    Random r = new Random();

    //create gateway
    PunchingMachineGateway punchingMachineGateway = new PunchingMachineGateway("punching-machine");

    // list for messages received by observers
    List<Boolean> observerMsgs1 = new ArrayList<>();
    List<Boolean> observerMsgs2 = new ArrayList<>();

    //create first observer
    PunchingMachineObserver observer1 = new PunchingMachineObserver() {
      @Override
      public void onPhototransistorGoodsInOut(boolean bool) {
        observerMsgs1.add(bool);
      }
    };

    //create second observer
    PunchingMachineObserver observer2 = new PunchingMachineObserver() {
      @Override
      public void onPhototransistorGoodsInOut(boolean bool) {
        observerMsgs2.add(bool);
      }
    };

    punchingMachineGateway.addObserver(observer1);
    punchingMachineGateway.addObserver(observer2);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(punchingMachineGateway);

    client.connect();

    punchingMachineGateway.connectMqttClient("/punching-machine/phototransistor-goods-in-out", client);
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
    publisher.publish("/punching-machine/phototransistor-goods-in-out", msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/punching-machine/phototransistor-goods-in-out", msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/punching-machine/phototransistor-goods-in-out", msg3);

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
  public void testPunchingMachineGatewayOneObserverMultipleTopics() throws MqttException, InterruptedException {

    Random r = new Random();

    //create gateway
    PunchingMachineGateway punchingMachineGateway = new PunchingMachineGateway("punching-machine");

    // list for messages received by observers
    List<Boolean> observerMsgs1 = new ArrayList<>();

    //create first observer
    PunchingMachineObserver observer1 = new PunchingMachineObserver() {
      @Override
      public void onPhototransistorGoodsInOut(boolean bool) {
        observerMsgs1.add(bool);
      }

      @Override
      public void onPhototransistorPunchingMachine(boolean bool){
        observerMsgs1.add(bool);
      }
    };

    punchingMachineGateway.addObserver(observer1);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(punchingMachineGateway);

    client.connect();

    punchingMachineGateway.connectMqttClient("/punching-machine/+", client);
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
    publisher.publish("/punching-machine/phototransistor-goods-in-out", msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/punching-machine/phototransistor-goods-in-out", msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/punching-machine/phototransistor-punching-machine", msg3);

    MqttMessage msg4 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg4.setQos(2);
    publisher.publish("/punching-machine/phototransistor-punching-machine", msg4);

    Thread.sleep(100L);

    // check if both callbacks received the same massages
    assertEquals(false, observerMsgs1.get(0));
    assertEquals(true, observerMsgs1.get(1));
    assertEquals(false, observerMsgs1.get(2));
    assertEquals(true, observerMsgs1.get(3));
  }

  // tests for all topics of punching machine ---------------

  public void doTestBooleanTopic(String topic, PunchingMachineObserver observer, List<Boolean> msgs) throws MqttException, InterruptedException {

    Random r = new Random();

    PunchingMachineGateway gateway = new PunchingMachineGateway("punching-machine");

    gateway.addObserver(observer);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(gateway);

    client.connect();

    gateway.connectMqttClient("/punching-machine/" + topic, client);
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
    publisher.publish("/punching-machine/" + topic, msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/punching-machine/" + topic, msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/punching-machine/" + topic, msg3);

    Thread.sleep(100L);

    assertEquals(false, msgs.get(0));
    assertEquals(true, msgs.get(1));
    assertEquals(false, msgs.get(2));
  }

  public void doTestIntegerTopic(String topic, PunchingMachineObserver observer, List<Integer> msgs) throws MqttException, InterruptedException {

    Random r = new Random();

    PunchingMachineGateway gateway = new PunchingMachineGateway("punching-machine");

    gateway.addObserver(observer);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(gateway);

    client.connect();

    gateway.connectMqttClient("/punching-machine/" + topic, client);
    System.out.println("Client connected");

    // publisher
    MqttClient publisher = new MqttClient(
            "tcp://localhost:1883",
            "publisher");

    publisher.connect();
    System.out.println("Publisher connected");

    // sending messages
    System.out.println("Publishing");

    MqttMessage msg1 = new MqttMessage("42".getBytes(StandardCharsets.UTF_8));
    msg1.setQos(2);
    publisher.publish("/punching-machine/" + topic, msg1);

    MqttMessage msg2 = new MqttMessage("43".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/punching-machine/" + topic, msg2);

    MqttMessage msg3 = new MqttMessage("44".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/punching-machine/" + topic, msg3);

    Thread.sleep(100L);

    assertEquals(42, msgs.get(0));
    assertEquals(43, msgs.get(1));
    assertEquals(44, msgs.get(2));
  }

  @Test
  public void testPhototransistorGoodsInOut() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    PunchingMachineObserver observer = new PunchingMachineObserver(){
      @Override
      public void onPhototransistorGoodsInOut(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("phototransistor-goods-in-out", observer, msgs);
  }

  @Test
  public void testPhototransistorPunchingMachine() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    PunchingMachineObserver observer = new PunchingMachineObserver(){
      @Override
      public void onPhototransistorPunchingMachine(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("phototransistor-punching-machine", observer, msgs);
  }

  @Test
  public void testSwitchPunchingMachineUp() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    PunchingMachineObserver observer = new PunchingMachineObserver(){
      @Override
      public void onSwitchPunchingMachineUp(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("switch-punching-machine-up", observer, msgs);
  }

  @Test
  public void testSwitchPunchingMachineDown() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    PunchingMachineObserver observer = new PunchingMachineObserver(){
      @Override
      public void onSwitchPunchingMachineDown(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("switch-punching-machine-down", observer, msgs);
  }

  @Test
  public void testMoveConveyorForward() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    PunchingMachineObserver observer = new PunchingMachineObserver(){
      @Override
      public void onMoveConveyorForward(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-conveyor-forward", observer, msgs);
  }

  @Test
  public void testMoveConveyorBackward() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    PunchingMachineObserver observer = new PunchingMachineObserver(){
      @Override
      public void onMoveConveyorBackward(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-conveyor-backward", observer, msgs);
  }

  @Test
  public void testMovePunchingMachineUp() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    PunchingMachineObserver observer = new PunchingMachineObserver(){
      @Override
      public void onMovePunchingMachineUp(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-punching-machine-up", observer, msgs);
  }

  @Test
  public void testMovePunchingMachineDown() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    PunchingMachineObserver observer = new PunchingMachineObserver(){
      @Override
      public void onMovePunchingMachineDown(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-punching-machine-down", observer, msgs);
  }
}