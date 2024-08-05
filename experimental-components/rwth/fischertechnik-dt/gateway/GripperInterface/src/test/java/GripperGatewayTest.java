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

  // tests for all topics of vacuum gripper ---------------

  public void doTestBooleanTopic(String topic, GripperObserver observer, List<Boolean> msgs) throws MqttException, InterruptedException {

    Random r = new Random();

    GripperGateway gateway = new GripperGateway("vacuum-gripper");

    gateway.observers.add(observer);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(gateway);

    client.connect();

    gateway.connectMqttClient("/vacuum-gripper/" + topic, client);
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
    publisher.publish("/vacuum-gripper/" + topic, msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/vacuum-gripper/" + topic, msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/vacuum-gripper/" + topic, msg3);

    Thread.sleep(100L);

    assertEquals(false, msgs.get(0));
    assertEquals(true, msgs.get(1));
    assertEquals(false, msgs.get(2));
  }

  public void doTestIntegerTopic(String topic, GripperObserver observer, List<Integer> msgs) throws MqttException, InterruptedException {

    Random r = new Random();

    GripperGateway gateway = new GripperGateway("vacuum-gripper");

    gateway.observers.add(observer);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(gateway);

    client.connect();

    gateway.connectMqttClient("/vacuum-gripper/" + topic, client);
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
    publisher.publish("/vacuum-gripper/" + topic, msg1);

    MqttMessage msg2 = new MqttMessage("43".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/vacuum-gripper/" + topic, msg2);

    MqttMessage msg3 = new MqttMessage("44".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/vacuum-gripper/" + topic, msg3);

    Thread.sleep(100L);

    assertEquals(42, msgs.get(0));
    assertEquals(43, msgs.get(1));
    assertEquals(44, msgs.get(2));
  }

  @Test
  public void testTopicRefSwitchVertical() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    GripperObserver observer = new GripperObserver(){
      @Override
      public void onRefSwitchVertical(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("ref-switch-vertical", observer, msgs);
  }

  @Test
  public void testTopicRefSwitchHorizontal() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    GripperObserver observer = new GripperObserver(){
      @Override
      public void onRefSwitchHorizontal(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("ref-switch-horizontal", observer, msgs);
  }

  @Test
  public void testTopicRefSwitchRotation() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    GripperObserver observer = new GripperObserver(){
      @Override
      public void onRefSwitchRotation(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("ref-switch-rotation", observer, msgs);
  }

  @Test
  public void testTopicMotorVerticalEnc1() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    GripperObserver observer = new GripperObserver(){
      @Override
      public void onMotorVerticalEnc1(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("motor-vertical-enc-1", observer, msgs);
  }

  @Test
  public void testTopicMotorVerticalEnc2() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    GripperObserver observer = new GripperObserver(){
      @Override
      public void onMotorVerticalEnc2(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("motor-vertical-enc-2", observer, msgs);
  }

  @Test
  public void testTopicMotorHorizontalEnc1() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    GripperObserver observer = new GripperObserver(){
      @Override
      public void onMotorHorizontalEnc1(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("motor-horizontal-enc-1", observer, msgs);
  }

  @Test
  public void testTopicMotorHorizontalEnc2() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    GripperObserver observer = new GripperObserver(){
      @Override
      public void onMotorHorizontalEnc2(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("motor-horizontal-enc-2", observer, msgs);
  }

  @Test
  public void testTopicMotorRotationEnc1() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    GripperObserver observer = new GripperObserver(){
      @Override
      public void onMotorRotationEnc1(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("motor-rotation-enc-1", observer, msgs);
  }

  @Test
  public void testTopicMotorRotationEnc2() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    GripperObserver observer = new GripperObserver(){
      @Override
      public void onMotorRotationEnc2(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("motor-rotation-enc-2", observer, msgs);
  }
  
  @Test
  public void testTopicMotorVerticalPos() throws MqttException, InterruptedException {

    List<Integer> msgs = new ArrayList<>();

    GripperObserver observer = new GripperObserver(){
      @Override
      public void onMotorVerticalPos(int motorVerticalPos){
        msgs.add(motorVerticalPos);
      }
    };

    doTestIntegerTopic("motor-vertical-pos", observer, msgs);
  }

    @Test
  public void testTopicMotorHorizontalPos() throws MqttException, InterruptedException {

    List<Integer> msgs = new ArrayList<>();

    GripperObserver observer = new GripperObserver(){
      @Override
      public void onMotorHorizontalPos(int motorHorizontalPos){
        msgs.add(motorHorizontalPos);
      }
    };

    doTestIntegerTopic("motor-horizontal-pos", observer, msgs);
  }

    @Test
  public void testTopicMotorRotationPos() throws MqttException, InterruptedException {

    List<Integer> msgs = new ArrayList<>();

    GripperObserver observer = new GripperObserver(){
      @Override
      public void onMotorRotationPos(int motorRotationPos){
        msgs.add(motorRotationPos);
      }
    };

    doTestIntegerTopic("motor-rotation-pos", observer, msgs);
  }

  @Test
  public void testTopicMoveVerticalUp() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    GripperObserver observer = new GripperObserver(){
      @Override
      public void onMoveVerticalUp(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-vertical-up", observer, msgs);
  }

  @Test
  public void testTopicMoveVerticalDown() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    GripperObserver observer = new GripperObserver(){
      @Override
      public void onMoveVerticalDown(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-vertical-down", observer, msgs);
  }

  @Test
  public void testTopicMoveHorizontalUp() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    GripperObserver observer = new GripperObserver(){
      @Override
      public void onMoveHorizontalUp(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-horizontal-up", observer, msgs);
  }

  @Test
  public void testTopicMoveHorizontalDown() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    GripperObserver observer = new GripperObserver(){
      @Override
      public void onMoveHorizontalDown(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-horizontal-down", observer, msgs);
  }

  @Test
  public void testTopicMoveRotationClockwise() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    GripperObserver observer = new GripperObserver(){
      @Override
      public void onMoveRotationClockwise(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-rotation-clockwise", observer, msgs);
  }

  @Test
  public void testTopicMoveRotationCounterclockwise() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    GripperObserver observer = new GripperObserver(){
      @Override
      public void onMoveRotationCounterclockwise(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-rotation-counterclockwise", observer, msgs);
  }

  @Test
  public void testTopicEnableCompressor() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    GripperObserver observer = new GripperObserver(){
      @Override
      public void onEnableCompressor(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("enable-compressor", observer, msgs);
  }

  @Test
  public void testTopicEnableValve() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    GripperObserver observer = new GripperObserver(){
      @Override
      public void onEnableValve(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("enable-valve", observer, msgs);
  }
}