import org.eclipse.paho.client.mqttv3.*;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SortingLineGatewayTest {

  @Test
  public void testSortingLineGatewayMultipleObserversOneTopic() throws MqttException, InterruptedException {

    Random r = new Random();

    //create gateway
    SortingLineGateway sortingLineGateway = new SortingLineGateway("sorting-line");

    // list for messages received by observers
    List<Boolean> observerMsgs1 = new ArrayList<>();
    List<Boolean> observerMsgs2 = new ArrayList<>();

    //create first observer
    SortingLineObserver observer1 = new SortingLineObserver() {
      @Override
      public void onLightBarrierInlet(boolean bool) {
        observerMsgs1.add(bool);
      }
    };

    //create second observer
    SortingLineObserver observer2 = new SortingLineObserver() {
      @Override
      public void onLightBarrierInlet(boolean bool) {
        observerMsgs2.add(bool);
      }
    };

    sortingLineGateway.observers.add(observer1);
    sortingLineGateway.observers.add(observer2);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(sortingLineGateway);

    client.connect();

    sortingLineGateway.connectMqttClient("/sorting-line/light-barrier-inlet", client);
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
    publisher.publish("/sorting-line/light-barrier-inlet", msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/sorting-line/light-barrier-inlet", msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/sorting-line/light-barrier-inlet", msg3);

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
  public void testSortingLineGatewayOneObserverMultipleTopics() throws MqttException, InterruptedException {

    Random r = new Random();

    //create gateway
    SortingLineGateway sortingLineGateway = new SortingLineGateway("sorting-line");

    // list for messages received by observers
    List<Boolean> observerMsgs1 = new ArrayList<>();

    //create first observer
    SortingLineObserver observer1 = new SortingLineObserver() {
      @Override
      public void onLightBarrierInlet(boolean bool) {
        observerMsgs1.add(bool);
      }

      @Override
      public void onLightBarrierBehindColorSensor(boolean bool){
        observerMsgs1.add(bool);
      }
    };

    sortingLineGateway.observers.add(observer1);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(sortingLineGateway);

    client.connect();

    sortingLineGateway.connectMqttClient("/sorting-line/+", client);
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
    publisher.publish("/sorting-line/light-barrier-inlet", msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/sorting-line/light-barrier-inlet", msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/sorting-line/light-barrier-behind-color-sensor", msg3);

    MqttMessage msg4 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg4.setQos(2);
    publisher.publish("/sorting-line/light-barrier-behind-color-sensor", msg4);

    Thread.sleep(100L);

    // check if both callbacks received the same massages
    assertEquals(false, observerMsgs1.get(0));
    assertEquals(true, observerMsgs1.get(1));
    assertEquals(false, observerMsgs1.get(2));
    assertEquals(true, observerMsgs1.get(3));
  }

  // tests for all topics of sorting line ---------------

  public void doTestBooleanTopic(String topic, SortingLineObserver observer, List<Boolean> msgs) throws MqttException, InterruptedException {

    Random r = new Random();

    SortingLineGateway gateway = new SortingLineGateway("sorting-line");

    gateway.observers.add(observer);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(gateway);

    client.connect();

    gateway.connectMqttClient("/sorting-line/" + topic, client);
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
    publisher.publish("/sorting-line/" + topic, msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/sorting-line/" + topic, msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/sorting-line/" + topic, msg3);

    Thread.sleep(100L);

    assertEquals(false, msgs.get(0));
    assertEquals(true, msgs.get(1));
    assertEquals(false, msgs.get(2));
  }

  public void doTestIntegerTopic(String topic, SortingLineObserver observer, List<Integer> msgs) throws MqttException, InterruptedException {

    Random r = new Random();

    SortingLineGateway gateway = new SortingLineGateway("sorting-line");

    gateway.observers.add(observer);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(gateway);

    client.connect();

    gateway.connectMqttClient("/sorting-line/" + topic, client);
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
    publisher.publish("/sorting-line/" + topic, msg1);

    MqttMessage msg2 = new MqttMessage("43".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/sorting-line/" + topic, msg2);

    MqttMessage msg3 = new MqttMessage("44".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/sorting-line/" + topic, msg3);

    Thread.sleep(100L);

    assertEquals(42, msgs.get(0));
    assertEquals(43, msgs.get(1));
    assertEquals(44, msgs.get(2));
  }

  @Test
  public void testPulseCounter() throws MqttException, InterruptedException {

    List<Integer> msgs = new ArrayList<>();

    SortingLineObserver observer = new SortingLineObserver(){
      @Override
      public void onPulseCounter(int pulseCounter){
        msgs.add(pulseCounter);
      }
    };

    doTestIntegerTopic("pulse-counter", observer, msgs);
  }

  @Test
  public void testLightBarrierInlet() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    SortingLineObserver observer = new SortingLineObserver(){
      @Override
      public void onLightBarrierInlet(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("light-barrier-inlet", observer, msgs);
  }

  @Test
  public void testLightBarrierBehindColorSensor() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    SortingLineObserver observer = new SortingLineObserver(){
      @Override
      public void onLightBarrierBehindColorSensor(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("light-barrier-behind-color-sensor", observer, msgs);
  }

  @Test
  public void testColorSensor() throws MqttException, InterruptedException {

    List<Integer> msgs = new ArrayList<>();

    SortingLineObserver observer = new SortingLineObserver(){
      @Override
      public void onColorSensor(int colorSensor){
        msgs.add(colorSensor);
      }
    };

    doTestIntegerTopic("color-sensor", observer, msgs);
  }

  @Test
  public void testLightBarrierWhite() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    SortingLineObserver observer = new SortingLineObserver(){
      @Override
      public void onLightBarrierWhite(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("light-barrier-white", observer, msgs);
  }

  @Test
  public void testLightBarrierRed() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    SortingLineObserver observer = new SortingLineObserver(){
      @Override
      public void onLightBarrierRed(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("light-barrier-red", observer, msgs);
  }

  @Test
  public void testLightBarrierBlue() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    SortingLineObserver observer = new SortingLineObserver(){
      @Override
      public void onLightBarrierBlue(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("light-barrier-blue", observer, msgs);
  }

  @Test
  public void testMoveConveyor() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    SortingLineObserver observer = new SortingLineObserver(){
      @Override
      public void onMoveConveyor(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-conveyor", observer, msgs);
  }

  @Test
  public void testEnableCompressor() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    SortingLineObserver observer = new SortingLineObserver(){
      @Override
      public void onEnableCompressor(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("enable-compressor", observer, msgs);
  }

  @Test
  public void testEnableValveFirstEjector() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    SortingLineObserver observer = new SortingLineObserver(){
      @Override
      public void onEnableValveFirstEjector(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("enable-valve-first-ejector", observer, msgs);
  }

  @Test
  public void testEnableValveSecondEjector() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    SortingLineObserver observer = new SortingLineObserver(){
      @Override
      public void onEnableValveSecondEjector(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("enable-valve-second-ejector", observer, msgs);
  }

  @Test
  public void testEnableValveThirdEjector() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    SortingLineObserver observer = new SortingLineObserver(){
      @Override
      public void onEnableValveThirdEjector(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("enable-valve-third-ejector", observer, msgs);
  }
}