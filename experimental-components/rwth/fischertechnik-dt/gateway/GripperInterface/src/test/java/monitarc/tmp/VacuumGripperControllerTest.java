package monitarc.tmp;

import montiarc.rte.msg.Message;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tmp.VacuumGripperControllerComp;
import tmp.VacuumGripperControllerCompBuilder;
import vacuum_gripper.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

import static montiarc.rte.msg.MessageFactory.msg;
import static montiarc.rte.msg.MessageFactory.tk;
import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
public class VacuumGripperControllerTest {

  static class ObserverCollection {
    PortObserver<Boolean> verticalUp = new PortObserver<>();
    PortObserver<Boolean> verticalDown = new PortObserver<>();
    PortObserver<Boolean> horizontalForward = new PortObserver<>();
    PortObserver<Boolean> horizontalBack = new PortObserver<>();
    PortObserver<Boolean> rotationClockwise = new PortObserver<>();
    PortObserver<Boolean> rotationCounterclockwise = new PortObserver<>();
  }

  ObserverCollection doTest(List<Message<Number>> verticalPos,
                            List<Message<Number>> verticalPosGoal,
                            List<Message<Number>> horizontalPos,
                            List<Message<Number>> horizontalPosGoal,
                            List<Message<Number>> rotationPos,
                            List<Message<Number>> rotationPosGoal) {

    ObserverCollection oc = new ObserverCollection();

    VacuumGripperControllerComp sut = new VacuumGripperControllerCompBuilder().setName("sut").build();
    sut.port_verticalUp().connect(oc.verticalUp);
    sut.port_verticalDown().connect(oc.verticalDown);
    sut.port_horizontalForward().connect(oc.horizontalForward);
    sut.port_horizontalBack().connect(oc.horizontalBack);
    sut.port_rotationClockwise().connect(oc.rotationClockwise);
    sut.port_rotationCounterclockwise().connect(oc.rotationCounterclockwise);

    sut.init();

    for (int i = 0; i < verticalPos.size(); i++){
      sut.port_verticalPos().receive(verticalPos.get(i));
      sut.port_verticalPosGoal().receive(verticalPosGoal.get(i));

      sut.port_horizontalPos().receive(horizontalPos.get(i));
      sut.port_horizontalPosGoal().receive(horizontalPosGoal.get(i));

      sut.port_rotationPos().receive(rotationPos.get(i));
      sut.port_rotationPosGoal().receive(rotationPosGoal.get(i));
    }

    sut.run();

    return oc;

  }


  @Test
  void moveVerticalUp() {

    // Test data
    List<Message<Number>> verticalPos =
            List.of(msg(1), tk(), msg(2), tk(), msg(3), tk());
    List<Message<Number>> verticalPosGoal =
            List.of(msg(2), tk(), msg(3), tk(), msg(2), tk());
    List<Message<Boolean>> expectedVerticalUp =
            List.of(msg(true), tk(), msg(true), tk(), msg(false), tk());

    // dummy data for other ports
    List<Message<Number>> horizontalPos =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());
    List<Message<Number>> horizontalPosGoal =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());
    List<Message<Number>> rotationPos =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());
    List<Message<Number>> rotationPosGoal =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());

    ObserverCollection oc = doTest(verticalPos, verticalPosGoal, horizontalPos, horizontalPosGoal, rotationPos, rotationPosGoal);


    Assertions.assertAll(
            () -> assertThat(oc.verticalUp.getObservedMessages()).as("booleans").containsExactlyElementsOf(expectedVerticalUp)
    );
  }

  @Test
  void moveVerticalDown() {

    // Test data
    List<Message<Number>> verticalPos =
            List.of(msg(2), tk(), msg(3), tk(), msg(4), tk());
    List<Message<Number>> verticalPosGoal =
            List.of(msg(1), tk(), msg(2), tk(), msg(5), tk());
    List<Message<Boolean>> expectedVerticalDown =
            List.of(msg(true), tk(), msg(true), tk(), msg(false), tk());

    // dummy data for other ports
    List<Message<Number>> horizontalPos =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());
    List<Message<Number>> horizontalPosGoal =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());
    List<Message<Number>> rotationPos =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());
    List<Message<Number>> rotationPosGoal =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());

    ObserverCollection oc = doTest(verticalPos, verticalPosGoal, horizontalPos, horizontalPosGoal, rotationPos, rotationPosGoal);

    Assertions.assertAll(
            () -> assertThat(oc.verticalDown.getObservedMessages()).as("booleans").containsExactlyElementsOf(expectedVerticalDown)
    );
  }

  @Test
  void moveHorizontalForward() {

    // Test data
    List<Message<Number>> horizontalPos =
            List.of(msg(1), tk(), msg(2), tk(), msg(3), tk());
    List<Message<Number>> horizontalPosGoal =
            List.of(msg(2), tk(), msg(3), tk(), msg(2), tk());
    List<Message<Boolean>> expectedHorizontalForward =
            List.of(msg(true), tk(), msg(true), tk(), msg(false), tk());

    // dummy data for other ports
    List<Message<Number>> verticalPos =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());
    List<Message<Number>> verticalPosGoal =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());
    List<Message<Number>> rotationPos =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());
    List<Message<Number>> rotationPosGoal =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());

    ObserverCollection oc = doTest(verticalPos, verticalPosGoal, horizontalPos, horizontalPosGoal, rotationPos, rotationPosGoal);

    Assertions.assertAll(
            () -> assertThat(oc.horizontalForward.getObservedMessages()).as("booleans").containsExactlyElementsOf(expectedHorizontalForward)
    );
  }

  @Test
  void moveHorizontalBack() {

    // Test data
    List<Message<Number>> horizontalPos =
            List.of(msg(2), tk(), msg(3), tk(), msg(4), tk());
    List<Message<Number>> horizontalPosGoal =
            List.of(msg(1), tk(), msg(2), tk(), msg(5), tk());
    List<Message<Boolean>> expectedHorizontalBack =
            List.of(msg(true), tk(), msg(true), tk(), msg(false), tk());

    // dummy data for other ports
    List<Message<Number>> verticalPos =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());
    List<Message<Number>> verticalPosGoal =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());
    List<Message<Number>> rotationPos =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());
    List<Message<Number>> rotationPosGoal =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());

    ObserverCollection oc = doTest(verticalPos, verticalPosGoal, horizontalPos, horizontalPosGoal, rotationPos, rotationPosGoal);

    Assertions.assertAll(
            () -> assertThat(oc.horizontalBack.getObservedMessages()).as("booleans").containsExactlyElementsOf(expectedHorizontalBack)
    );
  }

  @Test
  void moveRotationClockwise() {

    // Test data
    List<Message<Number>> rotationPos =
            List.of(msg(1), tk(), msg(2), tk(), msg(3), tk());
    List<Message<Number>> rotationPosGoal =
            List.of(msg(2), tk(), msg(3), tk(), msg(2), tk());
    List<Message<Boolean>> expectedRotationClockwise =
            List.of(msg(true), tk(), msg(true), tk(), msg(false), tk());

    // dummy data for other ports
    List<Message<Number>> verticalPos =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());
    List<Message<Number>> verticalPosGoal =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());
    List<Message<Number>> horizontalPos =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());
    List<Message<Number>> horizontalPosGoal =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());

    ObserverCollection oc = doTest(verticalPos, verticalPosGoal, horizontalPos, horizontalPosGoal, rotationPos, rotationPosGoal);

    Assertions.assertAll(
            () -> assertThat(oc.rotationClockwise.getObservedMessages()).as("booleans").containsExactlyElementsOf(expectedRotationClockwise)
    );
  }

  @Test
  void moveRotationCounterclockwise() {

    // Test data
    List<Message<Number>> rotationPos =
            List.of(msg(2), tk(), msg(3), tk(), msg(4), tk());
    List<Message<Number>> rotationPosGoal =
            List.of(msg(1), tk(), msg(2), tk(), msg(5), tk());
    List<Message<Boolean>> expectedRotationCounterclockwise =
            List.of(msg(true), tk(), msg(true), tk(), msg(false), tk());

    // dummy data for other ports
    List<Message<Number>> verticalPos =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());
    List<Message<Number>> verticalPosGoal =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());
    List<Message<Number>> horizontalPos =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());
    List<Message<Number>> horizontalPosGoal =
            List.of(msg(0), tk(), msg(0), tk(), msg(0), tk());

    ObserverCollection oc = doTest(verticalPos, verticalPosGoal, horizontalPos, horizontalPosGoal, rotationPos, rotationPosGoal);

    Assertions.assertAll(
            () -> assertThat(oc.rotationCounterclockwise.getObservedMessages()).as("booleans").containsExactlyElementsOf(expectedRotationCounterclockwise)
    );
  }

  @Test
  void complexMovement() {

    // Test data
    List<Message<Number>> verticalPos =
            List.of(msg(1), tk(), msg(0), tk(), msg(0), tk());
    List<Message<Number>> verticalPosGoal =
            List.of(msg(2), tk(), msg(0), tk(), msg(0), tk());
    List<Message<Boolean>> expectedVerticalUp =
            List.of(msg(true), tk(), tk(), tk());
    List<Message<Boolean>> expectedVerticalDown =
            List.of(msg(false), tk(), tk(), tk());
    List<Message<Number>> horizontalPos =
            List.of(msg(0), tk(), msg(1), tk(), msg(0), tk());
    List<Message<Number>> horizontalPosGoal =
            List.of(msg(0), tk(), msg(2), tk(), msg(0), tk());
    List<Message<Boolean>> expectedHorizontalForward =
            List.of(tk(), msg(true), tk(), tk());
    List<Message<Boolean>> expectedHorizontalBack =
            List.of(tk(), msg(false), tk(), tk());
    List<Message<Number>> rotationPos =
            List.of(msg(0), tk(), msg(0), tk(), msg(1), tk());
    List<Message<Number>> rotationPosGoal =
            List.of(msg(0), tk(), msg(0), tk(), msg(2), tk());
    List<Message<Boolean>> expectedRotationClockwise =
            List.of(tk(), tk(), msg(true), tk());
    List<Message<Boolean>> expectedRotationCounterclockwise =
            List.of(tk(), tk(), msg(false), tk());

    ObserverCollection oc = doTest(verticalPos, verticalPosGoal, horizontalPos, horizontalPosGoal, rotationPos, rotationPosGoal);
    
    Assertions.assertAll(
            () -> assertThat(oc.verticalUp.getObservedMessages()).as("booleans").containsExactlyElementsOf(expectedVerticalUp),
            () -> assertThat(oc.verticalDown.getObservedMessages()).as("booleans").containsExactlyElementsOf(expectedVerticalDown),
            () -> assertThat(oc.horizontalForward.getObservedMessages()).as("booleans").containsExactlyElementsOf(expectedHorizontalForward),
            () -> assertThat(oc.horizontalBack.getObservedMessages()).as("booleans").containsExactlyElementsOf(expectedHorizontalBack),
            () -> assertThat(oc.rotationClockwise.getObservedMessages()).as("booleans").containsExactlyElementsOf(expectedRotationClockwise),
            () -> assertThat(oc.rotationCounterclockwise.getObservedMessages()).as("booleans").containsExactlyElementsOf(expectedRotationCounterclockwise)
    );
  }

  @Test
  void mqttConnection() throws MqttException, InterruptedException {

    // setting up system under test
    ObserverCollection oc = new ObserverCollection();

    VacuumGripperControllerComp sut = new VacuumGripperControllerCompBuilder().setName("sut").build();
    sut.port_verticalUp().connect(oc.verticalUp);
    sut.port_verticalDown().connect(oc.verticalDown);
    sut.port_horizontalForward().connect(oc.horizontalForward);
    sut.port_horizontalBack().connect(oc.horizontalBack);
    sut.port_rotationClockwise().connect(oc.rotationClockwise);
    sut.port_rotationCounterclockwise().connect(oc.rotationCounterclockwise);

    sut.init();

    // dummy data for other ports
    List<Message<Number>> verticalPos = List.of(msg(0), tk());
    List<Message<Number>> verticalPosGoal = List.of(msg(0), tk());
    List<Message<Number>> rotationPos = List.of(msg(0), tk());
    List<Message<Number>> rotationPosGoal = List.of(msg(0), tk());

    List<Message<Number>> horizontalPosGoal = List.of(msg(2), tk());

    // expected output
    List<Message<Boolean>> expectedHorizontalForward = List.of(msg(true), tk());


    // setting up gateway + observers
    GripperGateway gateway = new GripperGateway("vacuum-gripper");

    GripperObserver observer1 = new GripperObserver() {
      @Override
      public void onMotorHorizontalPos(int motorHorizontalPos) {
        List<Message<Number>> horizontalPos = List.of(msg(motorHorizontalPos), tk());
        for (int i = 0; i < horizontalPos.size(); i++) {
          sut.port_horizontalPos().receive(horizontalPos.get(i));
        }
      }
    };

    gateway.observers.add(observer1);

    // client
    Random r = new Random();

    MqttClient client = new MqttClient(
      "tcp://localhost:1883",
      // Client id is randomized, such that existing qos 2 messages are not delivered from a previous run
      "client" + r.nextInt());
    client.setCallback(gateway);

    client.connect();

    gateway.connectMqttClient("vacuum-gripper/motor-horizontal-pos", client);
    System.out.println("Client connected");

    // publisher
    MqttClient publisher = new MqttClient(
            "tcp://localhost:1883",
            "publisher1");

    publisher.connect();
    System.out.println("Publisher connected");

    // send current horizontal position
    System.out.println("Publishing");
    MqttMessage msg1 = new MqttMessage("1".getBytes(StandardCharsets.UTF_8));
    msg1.setQos(2);
    publisher.publish("/vacuum-gripper/motor-horizontal-pos", msg1);

    Thread.sleep(100);

    Assertions.assertAll(
            () -> assertThat(oc.horizontalForward.getObservedMessages()).as("booleans").containsExactlyElementsOf(expectedHorizontalForward)
    );
  }
}
