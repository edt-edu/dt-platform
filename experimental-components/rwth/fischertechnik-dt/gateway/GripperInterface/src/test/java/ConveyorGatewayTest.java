import org.eclipse.paho.client.mqttv3.*;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ConveyorGatewayTest {

  @Test
  public void testConveyorGatewayMultipleObserversOneTopic() throws MqttException, InterruptedException {

    Random r = new Random();

    //create gateway
    ConveyorGateway conveyorGateway = new ConveyorGateway("conveyor-belt");

    // list for messages received by observers
    List<Boolean> observerMsgs1 = new ArrayList<>();
    List<Boolean> observerMsgs2 = new ArrayList<>();

    //create first observer
    ConveyorObserver observer1 = new ConveyorObserver() {
      @Override
      public void onPhototransistorFeedStation(boolean bool) {
        observerMsgs1.add(bool);
      }
    };

    //create second observer
    ConveyorObserver observer2 = new ConveyorObserver() {
      @Override
      public void onPhototransistorFeedStation(boolean bool) {
        observerMsgs2.add(bool);
      }
    };

    conveyorGateway.observers.add(observer1);
    conveyorGateway.observers.add(observer2);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(conveyorGateway);

    client.connect();

    conveyorGateway.connectMqttClient("/conveyor-belt/phototransistor-feed-station", client);
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
    publisher.publish("/conveyor-belt/phototransistor-feed-station", msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/conveyor-belt/phototransistor-feed-station", msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/conveyor-belt/phototransistor-feed-station", msg3);

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
  public void testConveyorGatewayOneObserverMultipleTopics() throws MqttException, InterruptedException {

    Random r = new Random();

    //create gateway
    ConveyorGateway conveyorGateway = new ConveyorGateway("conveyor-belt");

    // list for messages received by observers
    List<Boolean> observerMsgs1 = new ArrayList<>();

    //create first observer
    ConveyorObserver observer1 = new ConveyorObserver() {
      @Override
      public void onPhototransistorFeedStation(boolean bool) {
        observerMsgs1.add(bool);
      }

      @Override
      public void onPhototransistorSwapStation(boolean bool){
        observerMsgs1.add(bool);
      }
    };

    conveyorGateway.observers.add(observer1);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(conveyorGateway);

    client.connect();

    conveyorGateway.connectMqttClient("/conveyor-belt/+", client);
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
    publisher.publish("/conveyor-belt/phototransistor-feed-station", msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/conveyor-belt/phototransistor-feed-station", msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/conveyor-belt/phototransistor-swap-station", msg3);

    MqttMessage msg4 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg4.setQos(2);
    publisher.publish("/conveyor-belt/phototransistor-swap-station", msg4);

    Thread.sleep(100L);

    // check if both callbacks received the same massages
    assertEquals(false, observerMsgs1.get(0));
    assertEquals(true, observerMsgs1.get(1));
    assertEquals(false, observerMsgs1.get(2));
    assertEquals(true, observerMsgs1.get(3));
  }

  // tests for all topics of vacuum gripper ---------------

  public void doTestBooleanTopic(String topic, ConveyorObserver observer, List<Boolean> msgs) throws MqttException, InterruptedException {

    Random r = new Random();

    ConveyorGateway gateway = new ConveyorGateway("conveyor-belt");

    gateway.observers.add(observer);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(gateway);

    client.connect();

    gateway.connectMqttClient("/conveyor-belt/" + topic, client);
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
    publisher.publish("/conveyor-belt/" + topic, msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/conveyor-belt/" + topic, msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/conveyor-belt/" + topic, msg3);

    Thread.sleep(100L);

    assertEquals(false, msgs.get(0));
    assertEquals(true, msgs.get(1));
    assertEquals(false, msgs.get(2));
  }

  public void doTestIntegerTopic(String topic, ConveyorObserver observer, List<Integer> msgs) throws MqttException, InterruptedException {

    Random r = new Random();

    ConveyorGateway gateway = new ConveyorGateway("conveyor-belt");

    gateway.observers.add(observer);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(gateway);

    client.connect();

    gateway.connectMqttClient("/conveyor-belt/" + topic, client);
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
    publisher.publish("/conveyor-belt/" + topic, msg1);

    MqttMessage msg2 = new MqttMessage("43".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/conveyor-belt/" + topic, msg2);

    MqttMessage msg3 = new MqttMessage("44".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/conveyor-belt/" + topic, msg3);

    Thread.sleep(100L);

    assertEquals(42, msgs.get(0));
    assertEquals(43, msgs.get(1));
    assertEquals(44, msgs.get(2));
  }

  // tests for all topics of conveyor belt ---------------

  @Test
  public void testPhototransistorFeedStation() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    ConveyorObserver observer = new ConveyorObserver(){
      @Override
      public void onPhototransistorFeedStation(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("phototransistor-feed-station", observer, msgs);
  }

  @Test
  public void testPhototransistorSwapStation() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    ConveyorObserver observer = new ConveyorObserver(){
      @Override
      public void onPhototransistorSwapStation(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("phototransistor-swap-station", observer, msgs);
  }

  @Test
  public void testPulseButton() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    ConveyorObserver observer = new ConveyorObserver(){
      @Override
      public void onPulseButton(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("pulse-button", observer, msgs);
  }

  @Test
  public void testMoveConveyorForward() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    ConveyorObserver observer = new ConveyorObserver(){
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

    ConveyorObserver observer = new ConveyorObserver(){
      @Override
      public void onMoveConveyorBackward(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-conveyor-backward", observer, msgs);
  }
}