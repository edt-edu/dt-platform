import org.eclipse.paho.client.mqttv3.*;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class IndexedLineGatewayTest {

  @Test
  public void testIndexedLineGatewayMultipleObserversOneTopic() throws MqttException, InterruptedException {

    Random r = new Random();

    //create gateway
    IndexedLineGateway indexedLineGateway = new IndexedLineGateway("indexed-line");

    // list for messages received by observers
    List<Boolean> observerMsgs1 = new ArrayList<>();
    List<Boolean> observerMsgs2 = new ArrayList<>();

    //create first observer
    IndexedLineObserver observer1 = new IndexedLineObserver() {
      @Override
      public void onButtonSlider1Front(boolean bool) {
        observerMsgs1.add(bool);
      }
    };

    //create second observer
    IndexedLineObserver observer2 = new IndexedLineObserver() {
      @Override
      public void onButtonSlider1Front(boolean bool) {
        observerMsgs2.add(bool);
      }
    };

    indexedLineGateway.observers.add(observer1);
    indexedLineGateway.observers.add(observer2);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(indexedLineGateway);

    client.connect();

    indexedLineGateway.connectMqttClient("/indexed-line/button-slider-1-front", client);
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
    publisher.publish("/indexed-line/button-slider-1-front", msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/indexed-line/button-slider-1-front", msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/indexed-line/button-slider-1-front", msg3);

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
  public void testIndexedLineGatewayOneObserverMultipleTopics() throws MqttException, InterruptedException {

    Random r = new Random();

    //create gateway
    IndexedLineGateway indexedLineGateway = new IndexedLineGateway("indexed-line");

    // list for messages received by observers
    List<Boolean> observerMsgs1 = new ArrayList<>();

    //create first observer
    IndexedLineObserver observer1 = new IndexedLineObserver() {
      @Override
      public void onButtonSlider1Front(boolean bool) {
        observerMsgs1.add(bool);
      }

      @Override
      public void onButtonSlider1Rear(boolean bool){
        observerMsgs1.add(bool);
      }
    };

    indexedLineGateway.observers.add(observer1);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(indexedLineGateway);

    client.connect();

    indexedLineGateway.connectMqttClient("/indexed-line/+", client);
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
    publisher.publish("/indexed-line/button-slider-1-front", msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/indexed-line/button-slider-1-front", msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/indexed-line/button-slider-1-rear", msg3);

    MqttMessage msg4 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg4.setQos(2);
    publisher.publish("/indexed-line/button-slider-1-rear", msg4);

    Thread.sleep(100L);

    // check if both callbacks received the same massages
    assertEquals(false, observerMsgs1.get(0));
    assertEquals(true, observerMsgs1.get(1));
    assertEquals(false, observerMsgs1.get(2));
    assertEquals(true, observerMsgs1.get(3));
  }

  // tests for all topics of indexed line ---------------

  public void doTestBooleanTopic(String topic, IndexedLineObserver observer, List<Boolean> msgs) throws MqttException, InterruptedException {

    Random r = new Random();

    IndexedLineGateway gateway = new IndexedLineGateway("indexed-line");

    gateway.observers.add(observer);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(gateway);

    client.connect();

    gateway.connectMqttClient("/indexed-line/" + topic, client);
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
    publisher.publish("/indexed-line/" + topic, msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/indexed-line/" + topic, msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/indexed-line/" + topic, msg3);

    Thread.sleep(100L);

    assertEquals(false, msgs.get(0));
    assertEquals(true, msgs.get(1));
    assertEquals(false, msgs.get(2));
  }

  public void doTestIntegerTopic(String topic, IndexedLineObserver observer, List<Integer> msgs) throws MqttException, InterruptedException {

    Random r = new Random();

    IndexedLineGateway gateway = new IndexedLineGateway("indexed-line");

    gateway.observers.add(observer);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(gateway);

    client.connect();

    gateway.connectMqttClient("/indexed-line/" + topic, client);
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
    publisher.publish("/indexed-line/" + topic, msg1);

    MqttMessage msg2 = new MqttMessage("43".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/indexed-line/" + topic, msg2);

    MqttMessage msg3 = new MqttMessage("44".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/indexed-line/" + topic, msg3);

    Thread.sleep(100L);

    assertEquals(42, msgs.get(0));
    assertEquals(43, msgs.get(1));
    assertEquals(44, msgs.get(2));
  }

  @Test
  public void testTopicButtonSlider1Front() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    IndexedLineObserver observer = new IndexedLineObserver(){
      @Override
      public void onButtonSlider1Front(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("button-slider-1-front", observer, msgs);
  }

  @Test
  public void testTopicButtonSlider1Rear() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    IndexedLineObserver observer = new IndexedLineObserver(){
      @Override
      public void onButtonSlider1Rear(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("button-slider-1-rear", observer, msgs);
  }

  @Test
  public void testTopicButtonSlider2Front() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    IndexedLineObserver observer = new IndexedLineObserver(){
      @Override
      public void onButtonSlider2Front(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("button-slider-2-front", observer, msgs);
  }

  @Test
  public void testTopicButtonSlider2Rear() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    IndexedLineObserver observer = new IndexedLineObserver(){
      @Override
      public void onButtonSlider2Rear(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("button-slider-2-rear", observer, msgs);
  }

  @Test
  public void testTopicPhototransistorSlider1() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    IndexedLineObserver observer = new IndexedLineObserver(){
      @Override
      public void onPhototransistorSlider1(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("phototransistor-slider-1", observer, msgs);
  }

  @Test
  public void testTopicPhototransistorMillingMachine() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    IndexedLineObserver observer = new IndexedLineObserver(){
      @Override
      public void onPhototransistorMillingMachine(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("phototransistor-milling-machine", observer, msgs);
  }

  @Test
  public void testTopicPhototransistorLoadingStation() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    IndexedLineObserver observer = new IndexedLineObserver(){
      @Override
      public void onPhototransistorLoadingStation(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("phototransistor-loading-station", observer, msgs);
  }

  @Test
  public void testTopicPhototransistorDrillingMachine() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    IndexedLineObserver observer = new IndexedLineObserver(){
      @Override
      public void onPhototransistorDrillingMachine(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("phototransistor-drilling-machine", observer, msgs);
  }

  @Test
  public void testTopicPhototransistorConveyorSwap() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    IndexedLineObserver observer = new IndexedLineObserver(){
      @Override
      public void onPhototransistorConveyorSwap(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("phototransistor-conveyor-swap", observer, msgs);
  }

  @Test
  public void testTopicMoveSlider1Backward() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    IndexedLineObserver observer = new IndexedLineObserver(){
      @Override
      public void onMoveSlider1Backward(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-slider-1-backward", observer, msgs);
  }

  @Test
  public void testTopicMoveSlider1Forward() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    IndexedLineObserver observer = new IndexedLineObserver(){
      @Override
      public void onMoveSlider1Forward(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-slider-1-forward", observer, msgs);
  }

  @Test
  public void testTopicMoveSlider2Backward() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    IndexedLineObserver observer = new IndexedLineObserver(){
      @Override
      public void onMoveSlider2Backward(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-slider-2-backward", observer, msgs);
  }

  @Test
  public void testMoveSlider2Forward() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    IndexedLineObserver observer = new IndexedLineObserver(){
      @Override
      public void onMoveSlider2Forward(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-slider-2-forward", observer, msgs);
  }

  @Test
  public void testMoveConveyorFeed() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    IndexedLineObserver observer = new IndexedLineObserver(){
      @Override
      public void onMoveConveyorFeed(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-conveyor-feed", observer, msgs);
  }

  @Test
  public void testMoveConveyorMillingMachine() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    IndexedLineObserver observer = new IndexedLineObserver(){
      @Override
      public void onMoveConveyorMillingMachine(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-conveyor-milling-machine", observer, msgs);
  }

  @Test
  public void testMoveMillingMachine() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    IndexedLineObserver observer = new IndexedLineObserver(){
      @Override
      public void onMoveMillingMachine(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-milling-machine", observer, msgs);
  }

  @Test
  public void testMoveConveyorDrillingMachine() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    IndexedLineObserver observer = new IndexedLineObserver(){
      @Override
      public void onMoveConveyorDrillingMachine(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-conveyor-drilling-machine", observer, msgs);
  }

  @Test
  public void testMoveDrillingMachine() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    IndexedLineObserver observer = new IndexedLineObserver(){
      @Override
      public void onMoveDrillingMachine(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-drilling-machine", observer, msgs);
  }

  @Test
  public void testMoveConveyorSwap() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    IndexedLineObserver observer = new IndexedLineObserver(){
      @Override
      public void onMoveConveyorSwap(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-conveyor-swap", observer, msgs);
  }
}