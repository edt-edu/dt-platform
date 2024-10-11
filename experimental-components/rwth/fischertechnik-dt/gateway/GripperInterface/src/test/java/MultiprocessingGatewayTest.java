import org.eclipse.paho.client.mqttv3.*;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MultiprocessingGatewayTest{

  @Test
  public void testMultiprocessingGatewayMultipleObserversOneTopic() throws MqttException, InterruptedException {

    Random r = new Random();

    //create gateway
    MultiprocessingGateway multiprocessingGateway = new MultiprocessingGateway("multiprocessing-station");

    // list for messages received by observers
    List<Boolean> observerMsgs1 = new ArrayList<>();
    List<Boolean> observerMsgs2 = new ArrayList<>();

    //create first observer
    MultiprocessingObserver observer1 = new MultiprocessingObserver() {
      @Override
      public void onLightBarrierConveyorEnd(boolean bool) {
        observerMsgs1.add(bool);
      }
    };

    //create second observer
    MultiprocessingObserver observer2 = new MultiprocessingObserver() {
      @Override
      public void onLightBarrierConveyorEnd(boolean bool) {
        observerMsgs2.add(bool);
      }
    };

    multiprocessingGateway.observers.add(observer1);
    multiprocessingGateway.observers.add(observer2);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(multiprocessingGateway);

    client.connect();

    multiprocessingGateway.connectMqttClient("/multiprocessing-station/light-barrier-conveyor-end", client);
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
    publisher.publish("/multiprocessing-station/light-barrier-conveyor-end", msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/multiprocessing-station/light-barrier-conveyor-end", msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/multiprocessing-station/light-barrier-conveyor-end", msg3);

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
  public void testMultiprocessingGatewayOneObserverMultipleTopics() throws MqttException, InterruptedException {

    Random r = new Random();

    //create gateway
    MultiprocessingGateway multiprocessingGateway = new MultiprocessingGateway("multiprocessing-station");

    // list for messages received by observers
    List<Boolean> observerMsgs1 = new ArrayList<>();

    //create first observer
    MultiprocessingObserver observer1 = new MultiprocessingObserver() {
      @Override
      public void onLightBarrierConveyorEnd(boolean bool) {
        observerMsgs1.add(bool);
      }

      @Override
      public void onRefSwitchVacuumAtTurnTable(boolean bool){
        observerMsgs1.add(bool);
      }
    };

    multiprocessingGateway.observers.add(observer1);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(multiprocessingGateway);

    client.connect();

    multiprocessingGateway.connectMqttClient("/multiprocessing-station/+", client);
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
    publisher.publish("/multiprocessing-station/light-barrier-conveyor-end", msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/multiprocessing-station/light-barrier-conveyor-end", msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/multiprocessing-station/ref-switch-vacuum-at-turn-table", msg3);

    MqttMessage msg4 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg4.setQos(2);
    publisher.publish("/multiprocessing-station/ref-switch-vacuum-at-turn-table", msg4);

    Thread.sleep(100L);

    // check if both callbacks received the same massages
    assertEquals(false, observerMsgs1.get(0));
    assertEquals(true, observerMsgs1.get(1));
    assertEquals(false, observerMsgs1.get(2));
    assertEquals(true, observerMsgs1.get(3));
  }

  // tests for all topics of vacuum multiprocessing-station ---------------

  public void doTestBooleanTopic(String topic, MultiprocessingObserver observer, List<Boolean> msgs) throws MqttException, InterruptedException {

    Random r = new Random();

    MultiprocessingGateway gateway = new MultiprocessingGateway("multiprocessing-station");

    gateway.observers.add(observer);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(gateway);

    client.connect();

    gateway.connectMqttClient("/multiprocessing-station/" + topic, client);
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
    publisher.publish("/multiprocessing-station/" + topic, msg1);

    MqttMessage msg2 = new MqttMessage("true".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/multiprocessing-station/" + topic, msg2);

    MqttMessage msg3 = new MqttMessage("false".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/multiprocessing-station/" + topic, msg3);

    Thread.sleep(100L);

    assertEquals(false, msgs.get(0));
    assertEquals(true, msgs.get(1));
    assertEquals(false, msgs.get(2));
  }

  public void doTestIntegerTopic(String topic, MultiprocessingObserver observer, List<Integer> msgs) throws MqttException, InterruptedException {

    Random r = new Random();

    MultiprocessingGateway gateway = new MultiprocessingGateway("multiprocessing-station");

    gateway.observers.add(observer);

    // client
    MqttClient client = new MqttClient(
            "tcp://localhost:1883",
            // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
            "client" + r.nextInt());
    client.setCallback(gateway);

    client.connect();

    gateway.connectMqttClient("/multiprocessing-station/" + topic, client);
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
    publisher.publish("/multiprocessing-station/" + topic, msg1);

    MqttMessage msg2 = new MqttMessage("43".getBytes(StandardCharsets.UTF_8));
    msg2.setQos(2);
    publisher.publish("/multiprocessing-station/" + topic, msg2);

    MqttMessage msg3 = new MqttMessage("44".getBytes(StandardCharsets.UTF_8));
    msg3.setQos(2);
    publisher.publish("/multiprocessing-station/" + topic, msg3);

    Thread.sleep(100L);

    assertEquals(42, msgs.get(0));
    assertEquals(43, msgs.get(1));
    assertEquals(44, msgs.get(2));
  }

  @Test
  public void testRefSwitchRotationAtVacuum() throws MqttException, InterruptedException {

    List<Integer> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onRefSwitchRotationAtVacuum(int refSwitchRotationAtVacuum){
        msgs.add(refSwitchRotationAtVacuum);
      }
    };

    doTestIntegerTopic("ref-switch-rotation-at-vacuum", observer, msgs);
  }

  @Test
  public void testRefSwitchRotationAtBelt() throws MqttException, InterruptedException {

    List<Integer> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onRefSwitchRotationAtBelt(int refSwitchRotationAtBelt){
        msgs.add(refSwitchRotationAtBelt);
      }
    };

    doTestIntegerTopic("ref-switch-rotation-at-belt", observer, msgs);
  }

  @Test
  public void testLightBarrierConveyorEnd() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onLightBarrierConveyorEnd(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("light-barrier-conveyor-end", observer, msgs);
  }

  @Test
  public void testRefSwitchTurnTableAtSaw() throws MqttException, InterruptedException {

    List<Integer> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onRefSwitchTurnTableAtSaw(int refSwitchTurnTableAtSaw){
        msgs.add(refSwitchTurnTableAtSaw);
      }
    };

    doTestIntegerTopic("ref-switch-turn-table-at-saw", observer, msgs);
  }

  @Test
  public void testRefSwitchVacuumAtTurnTable() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onRefSwitchVacuumAtTurnTable(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("ref-switch-vacuum-at-turn-table", observer, msgs);
  }

  @Test
  public void testRefSwitchFeederInside() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onRefSwitchFeederInside(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("ref-switch-feeder-inside", observer, msgs);
  }

  @Test
  public void testRefSwitchFeederOutside() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onRefSwitchFeederOutside(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("ref-switch-feeder-outside", observer, msgs);
  }

  @Test
  public void testRefSwitchVacuumAtOven() throws MqttException, InterruptedException {

    List<Integer> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onRefSwitchVacuumAtOven(int refSwitchVacuumAtOven){
        msgs.add(refSwitchVacuumAtOven);
      }
    };

    doTestIntegerTopic("ref-switch-vacuum-at-oven", observer, msgs);
  }

  @Test
  public void testLightBarrierOven() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onLightBarrierOven(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("light-barrier-oven", observer, msgs);
  }

  @Test
  public void testMoveTurnTableClockwise() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onMoveTurnTableClockwise(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-turn-table-clockwise", observer, msgs);
  }

  @Test
  public void testMoveTurnTableCounterclockwise() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onMoveTurnTableCounterclockwise(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-turn-table-counterclockwise", observer, msgs);
  }

  @Test
  public void testMoveConveyorForward() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onMoveConveyorForward(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-conveyor-forward", observer, msgs);
  }

  @Test
  public void testEnableSaw() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onEnableSaw(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("enable-saw", observer, msgs);
  }

  @Test
  public void testMoveOvenFeederRetract() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onMoveOvenFeederRetract(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-oven-feeder-retract", observer, msgs);
  }

  @Test
  public void testMoveOvenFeederExtend() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onMoveOvenFeederExtend(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-oven-feeder-extend", observer, msgs);
  }

  @Test
  public void testMoveVacuumToOven() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onMoveVacuumToOven(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-vacuum-to-oven", observer, msgs);
  }

  @Test
  public void testMoveVacuumToTurnTable() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onMoveVacuumToTurnTable(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("move-vacuum-to-turn-table", observer, msgs);
  }

  @Test
  public void testEnableOvenLight() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onEnableOvenLight(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("enable-oven-light", observer, msgs);
  }

  @Test
  public void testEnableCompressor() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onEnableCompressor(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("enable-compressor", observer, msgs);
  }

  @Test
  public void testEnableValveVacuum() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onEnableValveVacuum(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("enable-valve-vacuum", observer, msgs);
  }

  @Test
  public void testEnableValveMoveVacuum() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onEnableValveMoveVacuum(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("enable-valve-move-vacuum", observer, msgs);
  }

  @Test
  public void testEnableValveMoveOvenDoor() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onEnableValveMoveOvenDoor(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("enable-valve-move-oven-door", observer, msgs);
  }

  @Test
  public void testEnableValveMoveFeeder() throws MqttException, InterruptedException {

    List<Boolean> msgs = new ArrayList<>();

    MultiprocessingObserver observer = new MultiprocessingObserver(){
      @Override
      public void onEnableValveMoveFeeder(boolean b){
        msgs.add(b);
      }
    };

    doTestBooleanTopic("enable-valve-move-feeder", observer, msgs);
  }
}