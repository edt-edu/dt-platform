import org.eclipse.paho.client.mqttv3.*;
import org.junit.jupiter.api.Test;
import three_d_gripper.ThreeDGripperGateway;
import three_d_gripper.ThreeDGripperObserver;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ThreeDGripperGatewayTest {

  @Test
  public void testThreeDGripperGatewayMultipleObserversOneTopic() throws MqttException, InterruptedException {

    Random r = new Random();

    //create gateway
    ThreeDGripperGateway threeDGripperGateway = new ThreeDGripperGateway("3D-gripper");

    // list for messages received by observers
    List<Boolean> observerMsgs1 = new ArrayList<>();
    List<Boolean> observerMsgs2 = new ArrayList<>();

    //create first observer
    ThreeDGripperObserver observer1 = new ThreeDGripperObserver() {
      @Override
      public void onRefSwitchClaw(boolean bool) {
        observerMsgs1.add(bool);
      }
    };

    //create second observer
    ThreeDGripperObserver observer2 = new ThreeDGripperObserver() {
      @Override
      public void onRefSwitchClaw(boolean bool) {
        observerMsgs2.add(bool);
      }
    };

    threeDGripperGateway.addObserver(observer1);
    threeDGripperGateway.addObserver(observer2);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(threeDGripperGateway);

    client.connect();

    threeDGripperGateway.connectMqttClient("/3D-gripper/ref-switch-claw", client);
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
    publisher.publish("/3D-gripper/ref-switch-claw", msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/3D-gripper/ref-switch-claw", msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/3D-gripper/ref-switch-claw", msg3);

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
  public void testThreeDGripperGatewayOneObserverMultipleTopics() throws MqttException, InterruptedException {

    Random r = new Random();

    //create gateway
    ThreeDGripperGateway threeDGripperGateway = new ThreeDGripperGateway("3D-gripper");

    // list for messages received by observers
    List<Boolean> observerMsgs1 = new ArrayList<>();

    //create first observer
    ThreeDGripperObserver observer1 = new ThreeDGripperObserver() {
      @Override
      public void onRefSwitchClaw(boolean bool) {
        observerMsgs1.add(bool);
      }

      @Override
      public void onRefSwitchGripper(boolean bool) {
        observerMsgs1.add(bool);
      }
    };

    threeDGripperGateway.addObserver(observer1);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(threeDGripperGateway);

    client.connect();

    threeDGripperGateway.connectMqttClient("/3D-gripper/+", client);
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
    publisher.publish("/3D-gripper/ref-switch-claw", msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/3D-gripper/ref-switch-claw", msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/3D-gripper/ref-switch-gripper", msg3);

    MqttMessage msg4 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg4.setQos(2);
    publisher.publish("/3D-gripper/ref-switch-gripper", msg4);

    Thread.sleep(100L);

    // check if both callbacks received the same massages
    assertEquals(false, observerMsgs1.get(0));
    assertEquals(true, observerMsgs1.get(1));
    assertEquals(false, observerMsgs1.get(2));
    assertEquals(true, observerMsgs1.get(3));
  }


// tests for all topics of 3D gripper ---------------

  public void doTestBooleanTopic(String topic, ThreeDGripperObserver observer, List<Boolean> msgs) throws MqttException, InterruptedException {

    Random r = new Random();

    ThreeDGripperGateway gateway = new ThreeDGripperGateway("3D-gripper");

    gateway.addObserver(observer);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(gateway);

    client.connect();

    gateway.connectMqttClient("/3D-gripper/" + topic, client);
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
    publisher.publish("/3D-gripper/" + topic, msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/3D-gripper/" + topic, msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/3D-gripper/" + topic, msg3);

    Thread.sleep(100L);

    assertEquals(false, msgs.get(0));
    assertEquals(true, msgs.get(1));
    assertEquals(false, msgs.get(2));
  }

  public void doTestIntegerTopic(String topic, ThreeDGripperObserver observer, List<Integer> msgs) throws MqttException, InterruptedException {

    Random r = new Random();

    ThreeDGripperGateway gateway = new ThreeDGripperGateway("3D-gripper");

    gateway.addObserver(observer);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(gateway);

    client.connect();

    gateway.connectMqttClient("/3D-gripper/" + topic, client);
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
    publisher.publish("/3D-gripper/" + topic, msg1);

    MqttMessage msg2 = new MqttMessage("43".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/3D-gripper/" + topic, msg2);

    MqttMessage msg3 = new MqttMessage("44".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/3D-gripper/" + topic, msg3);

    Thread.sleep(100L);

    assertEquals(42, msgs.get(0));
    assertEquals(43, msgs.get(1));
    assertEquals(44, msgs.get(2));
  }

  @Test
  public void testRefSwitchClaw() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    ThreeDGripperObserver observer = new ThreeDGripperObserver() {
      @Override
      public void onRefSwitchClaw(boolean b) {
        msgs.add(b);
      }
    };

    doTestBooleanTopic("ref-switch-claw", observer, msgs);
  }

  @Test
  public void testEncCounterClaw() throws MqttException, InterruptedException {

    List<Integer> msgs = new ArrayList<>();

    ThreeDGripperObserver observer = new ThreeDGripperObserver() {
      @Override
      public void onEncCounterClaw(int encCounterClaw) {
        msgs.add(encCounterClaw);
      }
    };

    doTestIntegerTopic("enc-counter-claw", observer, msgs);
  }

  @Test
  public void testRefSwitchGripper() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    ThreeDGripperObserver observer = new ThreeDGripperObserver() {
      @Override
      public void onRefSwitchGripper(boolean b) {
        msgs.add(b);
      }
    };

    doTestBooleanTopic("ref-switch-gripper", observer, msgs);
  }

  @Test
  public void testEncCounterGripper() throws MqttException, InterruptedException {

    List<Integer> msgs = new ArrayList<>();

    ThreeDGripperObserver observer = new ThreeDGripperObserver() {
      @Override
      public void onEncCounterGripper(int encCounterGripper) {
        msgs.add(encCounterGripper);
      }
    };

    doTestIntegerTopic("enc-counter-gripper", observer, msgs);
  }

  @Test
  public void testRefSwitchVertical() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    ThreeDGripperObserver observer = new ThreeDGripperObserver() {
      @Override
      public void onRefSwitchVertical(boolean b) {
        msgs.add(b);
      }
    };

    doTestBooleanTopic("ref-switch-vertical", observer, msgs);
  }

  @Test
  public void testRefSwitchRotation() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    ThreeDGripperObserver observer = new ThreeDGripperObserver() {
      @Override
      public void onRefSwitchRotation(boolean b) {
        msgs.add(b);
      }
    };

    doTestBooleanTopic("ref-switch-rotation", observer, msgs);
  }

  @Test
  public void testMotorVerticalEnc1() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    ThreeDGripperObserver observer = new ThreeDGripperObserver() {
      @Override
      public void onMotorVerticalEnc1(boolean b) {
        msgs.add(b);
      }
    };

    doTestBooleanTopic("motor-vertical-enc-1", observer, msgs);
  }

  @Test
  public void testMotorVerticalEnc2() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    ThreeDGripperObserver observer = new ThreeDGripperObserver() {
      @Override
      public void onMotorVerticalEnc2(boolean b) {
        msgs.add(b);
      }
    };

    doTestBooleanTopic("motor-vertical-enc-2", observer, msgs);
  }

  @Test
  public void testMotorRotationEnc1() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    ThreeDGripperObserver observer = new ThreeDGripperObserver() {
      @Override
      public void onMotorRotationEnc1(boolean b) {
        msgs.add(b);
      }
    };

    doTestBooleanTopic("motor-rotation-enc-1", observer, msgs);
  }

  @Test
  public void testMotorRotationEnc2() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    ThreeDGripperObserver observer = new ThreeDGripperObserver() {
      @Override
      public void onMotorRotationEnc2(boolean b) {
        msgs.add(b);
      }
    };

    doTestBooleanTopic("motor-rotation-enc-2", observer, msgs);
  }

  @Test
  public void testMotorVerticalPos() throws MqttException, InterruptedException {

    List<Integer> msgs = new ArrayList<>();

    ThreeDGripperObserver observer = new ThreeDGripperObserver() {
      @Override
      public void onMotorVerticalPos(int motorVerticalPos) {
        msgs.add(motorVerticalPos);
      }
    };

    doTestIntegerTopic("motor-vertical-pos", observer, msgs);
  }

  @Test
  public void testMotorRotationPos() throws MqttException, InterruptedException {

    List<Integer> msgs = new ArrayList<>();

    ThreeDGripperObserver observer = new ThreeDGripperObserver() {
      @Override
      public void onMotorRotationPos(int motorRotationPos) {
        msgs.add(motorRotationPos);
      }
    };

    doTestIntegerTopic("motor-rotation-pos", observer, msgs);
  }

  @Test
  public void testOpenGripper() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    ThreeDGripperObserver observer = new ThreeDGripperObserver() {
      @Override
      public void onOpenGripper(boolean b) {
        msgs.add(b);
      }
    };

    doTestBooleanTopic("open-gripper", observer, msgs);
  }

  @Test
  public void testCloseGripper() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    ThreeDGripperObserver observer = new ThreeDGripperObserver() {
      @Override
      public void onCloseGripper(boolean b) {
        msgs.add(b);
      }
    };

    doTestBooleanTopic("close-gripper", observer, msgs);
  }

  @Test
  public void testMoveHorizontalForward() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    ThreeDGripperObserver observer = new ThreeDGripperObserver() {
      @Override
      public void onMoveHorizontalForward(boolean b) {
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-horizontal-forward", observer, msgs);
  }

  @Test
  public void testMoveHorizontalBackward() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    ThreeDGripperObserver observer = new ThreeDGripperObserver() {
      @Override
      public void onMoveHorizontalBackward(boolean b) {
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-horizontal-backward", observer, msgs);
  }

  @Test
  public void testMoveVerticalUp() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    ThreeDGripperObserver observer = new ThreeDGripperObserver() {
      @Override
      public void onMoveVerticalUp(boolean b) {
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-vertical-up", observer, msgs);
  }

  @Test
  public void testMoveVerticalDown() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    ThreeDGripperObserver observer = new ThreeDGripperObserver() {
      @Override
      public void onMoveVerticalDown(boolean b) {
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-vertical-down", observer, msgs);
  }

  @Test
  public void testMoveRotationClockwise() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    ThreeDGripperObserver observer = new ThreeDGripperObserver() {
      @Override
      public void onMoveRotationClockwise(boolean b) {
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-rotation-clockwise", observer, msgs);
  }

  @Test
  public void testMoveRotationCounterclockwise() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    ThreeDGripperObserver observer = new ThreeDGripperObserver() {
      @Override
      public void onMoveRotationCounterclockwise(boolean b) {
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-rotation-counterclockwise", observer, msgs);
  }
}