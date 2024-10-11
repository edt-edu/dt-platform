import high_bay.HighBayGateway;
import high_bay.HighBayObserver;
import org.eclipse.paho.client.mqttv3.*;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class HighBayGatewayTest {

  @Test
  public void testHighBayGatewayMultipleObserversOneTopic() throws MqttException, InterruptedException {

    Random r = new Random();

    //create gateway
    HighBayGateway highBayGateway = new HighBayGateway("high-bay");

    // list for messages received by observers
    List<Boolean> observerMsgs1 = new ArrayList<>();
    List<Boolean> observerMsgs2 = new ArrayList<>();

    //create first observer
    HighBayObserver observer1 = new HighBayObserver() {
      @Override
      public void onRefSwitchHorizontal(boolean bool) {
        observerMsgs1.add(bool);
      }
    };

    //create second observer
    HighBayObserver observer2 = new HighBayObserver() {
      @Override
      public void onRefSwitchHorizontal(boolean bool) {
        observerMsgs2.add(bool);
      }
    };

    highBayGateway.addObserver(observer1);
    highBayGateway.addObserver(observer2);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(highBayGateway);

    client.connect();

    highBayGateway.connectMqttClient("/high-bay/ref-switch-horizontal", client);
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
    publisher.publish("/high-bay/ref-switch-horizontal", msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/high-bay/ref-switch-horizontal", msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/high-bay/ref-switch-horizontal", msg3);

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
  public void testHighBayGatewayOneObserverMultipleTopics() throws MqttException, InterruptedException {

    Random r = new Random();

    //create gateway
    HighBayGateway highBayGateway = new HighBayGateway("high-bay");

    // list for messages received by observers
    List<Boolean> observerMsgs1 = new ArrayList<>();

    //create first observer
    HighBayObserver observer1 = new HighBayObserver() {
      @Override
      public void onRefSwitchHorizontal(boolean bool) {
        observerMsgs1.add(bool);
      }

      @Override
      public void onLightBarrierInside(boolean bool){
        observerMsgs1.add(bool);
      }
    };

    highBayGateway.addObserver(observer1);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(highBayGateway);

    client.connect();

    highBayGateway.connectMqttClient("/high-bay/+", client);
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
    publisher.publish("/high-bay/ref-switch-horizontal", msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/high-bay/ref-switch-horizontal", msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/high-bay/light-barrier-inside", msg3);

    MqttMessage msg4 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg4.setQos(2);
    publisher.publish("/high-bay/light-barrier-inside", msg4);

    Thread.sleep(100L);

    // check if both callbacks received the same massages
    assertEquals(false, observerMsgs1.get(0));
    assertEquals(true, observerMsgs1.get(1));
    assertEquals(false, observerMsgs1.get(2));
    assertEquals(true, observerMsgs1.get(3));
  }

  // tests for all topics of high bay ---------------

  public void doTestBooleanTopic(String topic, HighBayObserver observer, List<Boolean> msgs) throws MqttException, InterruptedException {

    Random r = new Random();

    HighBayGateway gateway = new HighBayGateway("high-bay");

    gateway.addObserver(observer);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(gateway);

    client.connect();

    gateway.connectMqttClient("/high-bay/" + topic, client);
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
    publisher.publish("/high-bay/" + topic, msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/high-bay/" + topic, msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/high-bay/" + topic, msg3);

    Thread.sleep(100L);

    assertEquals(false, msgs.get(0));
    assertEquals(true, msgs.get(1));
    assertEquals(false, msgs.get(2));
  }

  public void doTestIntegerTopic(String topic, HighBayObserver observer, List<Integer> msgs) throws MqttException, InterruptedException {

    Random r = new Random();

    HighBayGateway gateway = new HighBayGateway("high-bay");

    gateway.addObserver(observer);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(gateway);

    client.connect();

    gateway.connectMqttClient("/high-bay/" + topic, client);
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
    publisher.publish("/high-bay/" + topic, msg1);

    MqttMessage msg2 = new MqttMessage("43".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/high-bay/" + topic, msg2);

    MqttMessage msg3 = new MqttMessage("44".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/high-bay/" + topic, msg3);

    Thread.sleep(100L);

    assertEquals(42, msgs.get(0));
    assertEquals(43, msgs.get(1));
    assertEquals(44, msgs.get(2));
  }

  @Test
  public void testTopicRefSwitchHorizontal() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onRefSwitchHorizontal(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("ref-switch-horizontal", observer, msgs);
  }

  @Test
  public void testLightBarrierInside() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onLightBarrierInside(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("light-barrier-inside", observer, msgs);
  }

  @Test
  public void testLightBarrierOutside() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onLightBarrierOutside(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("light-barrier-outside", observer, msgs);
  }

  @Test
  public void testTopicRefSwitchVertical() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onRefSwitchVertical(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("ref-switch-vertical", observer, msgs);
  }

  @Test
  public void testTrailSensorLower() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onTrailSensorLower(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("trail-sensor-lower", observer, msgs);
  }

  @Test
  public void testTrailSensorUpper() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onTrailSensorUpper(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("trail-sensor-upper", observer, msgs);
  }

  @Test
  public void testHorizontalEnc1() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onHorizontalEnc1(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("horizontal-enc-1", observer, msgs);
  }

  @Test
  public void testHorizontalEnc2() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onHorizontalEnc2(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("horizontal-enc-2", observer, msgs);
  }

  @Test
  public void testVerticalEnc1() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onVerticalEnc1(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("vertical-enc-1", observer, msgs);
  }

  @Test
  public void testVerticalEnc2() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onVerticalEnc2(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("vertical-enc-2", observer, msgs);
  }

  @Test
  public void testRefSwitchCantileverFront() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onRefSwitchCantileverFront(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("ref-switch-cantilever-front", observer, msgs);
  }

  @Test
  public void testRefSwitchCantileverBack() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onRefSwitchCantileverBack(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("ref-switch-cantilever-back", observer, msgs);
  }

  @Test
  public void testVerticalPos() throws MqttException, InterruptedException {

    List<Integer> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onVerticalPos(int verticalPos){
        msgs.add(verticalPos);
      }
    };

    doTestIntegerTopic("vertical-pos", observer, msgs);
  }

  @Test
  public void testHorizontalPos() throws MqttException, InterruptedException {

    List<Integer> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onHorizontalPos(int horizontalPos){
        msgs.add(horizontalPos);
      }
    };

    doTestIntegerTopic("horizontal-pos", observer, msgs);
  }

  @Test
  public void testMoveConveyorForward() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onMoveConveyorForward(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-conveyor-forward", observer, msgs);
  }

  @Test
  public void testMoveConveyorBackrward() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onMoveConveyorBackward(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-conveyor-backward", observer, msgs);
  }

  @Test
  public void testMoveArmToRack() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onMoveArmToRack(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-arm-to-rack", observer, msgs);
  }

  @Test
  public void testMoveArmToConveyor() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onMoveArmToConveyor(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-arm-to-conveyor", observer, msgs);
  }

  @Test
  public void testMoveVerticalDown() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onMoveVerticalDown(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-vertical-down", observer, msgs);
  }

  @Test
  public void testMoveVerticalUp() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onMoveVerticalUp(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-vertical-up", observer, msgs);
  }

  @Test
  public void testMoveCantileverForward() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onMoveCantileverForward(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-cantilever-forward", observer, msgs);
  }

  @Test
  public void testMoveCantileverBackward() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    HighBayObserver observer = new HighBayObserver(){
      @Override
      public void onMoveCantileverBackward(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-cantilever-backward", observer, msgs);
  }
}