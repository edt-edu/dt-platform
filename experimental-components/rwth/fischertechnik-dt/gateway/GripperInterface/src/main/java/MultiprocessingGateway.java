import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class MultiprocessingGateway implements MqttCallback {

  protected final String machineId;
  protected List<MultiprocessingObserver> observers = new ArrayList<>();

  public MultiprocessingGateway(String machineId) {
        this.machineId = machineId;
    }

  @Override
  public void connectionLost(Throwable throwable) {
    //Called when the client lost the connection to the broker
  }

  @Override
  public void messageArrived(String topic, MqttMessage message) {
    if(topic != null) {

      String msgStr = new String(message.getPayload(), StandardCharsets.UTF_8);

      // Outputs of the machine

      if (topic.equals("/" + machineId + "/ref-switch-rotation-at-vacuum")) {
        observers.forEach(o -> o.onRefSwitchRotationAtVacuum(Integer.parseInt(msgStr)));

      } else if (topic.equals("/" + machineId + "/ref-switch-rotation-at-belt")) {
        observers.forEach(o -> o.onRefSwitchRotationAtBelt(Integer.parseInt(msgStr)));

      } else if (topic.equals("/" + machineId + "/light-barrier-conveyor-end")) {
        observers.forEach(o -> o.onLightBarrierConveyorEnd(msgStr.equals("true")));

      } else if (topic.equals("/" + machineId + "/ref-switch-turn-table-at-saw")) {
        observers.forEach(o -> o.onRefSwitchTurnTableAtSaw(Integer.parseInt(msgStr)));

      } else if (topic.equals("/" + machineId + "/ref-switch-vacuum-at-turn-table")) {
        observers.forEach(o -> o.onRefSwitchVacuumAtTurnTable(msgStr.equals("true")));

      } else if (topic.equals("/" + machineId + "/ref-switch-feeder-inside")) {
        observers.forEach(o -> o.onRefSwitchFeederInside(msgStr.equals("true")));

      } else if (topic.equals("/" + machineId + "/ref-switch-feeder-outside")) {
        observers.forEach(o -> o.onRefSwitchFeederOutside(msgStr.equals("true")));

      } else if (topic.equals("/" + machineId + "/ref-switch-vacuum-at-oven")) {
        observers.forEach(o -> o.onRefSwitchVacuumAtOven(Integer.parseInt(msgStr)));

      } else if (topic.equals("/" + machineId + "/light-barrier-oven")) {
        observers.forEach(o -> o.onLightBarrierOven(msgStr.equals("true")));


        // Inputs of the machine

      } else if (topic.equals("/" + machineId + "/move-turn-table-clockwise")) {
        observers.forEach(o -> o.onMoveTurnTableClockwise(msgStr.equals("true")));

      } else if (topic.equals("/" + machineId + "/move-turn-table-counterclockwise")) {
        observers.forEach(o -> o.onMoveTurnTableCounterclockwise(msgStr.equals("true")));

      } else if (topic.equals("/" + machineId + "/move-conveyor-forward")) {
        observers.forEach(o -> o.onMoveConveyorForward(msgStr.equals("true")));

      } else if (topic.equals("/" + machineId + "/enable-saw")) {
        observers.forEach(o -> o.onEnableSaw(msgStr.equals("true")));

      } else if (topic.equals("/" + machineId + "/move-oven-feeder-retract")) {
        observers.forEach(o -> o.onMoveOvenFeederRetract(msgStr.equals("true")));

      } else if (topic.equals("/" + machineId + "/move-oven-feeder-extend")) {
        observers.forEach(o -> o.onMoveOvenFeederExtend(msgStr.equals("true")));

      } else if (topic.equals("/" + machineId + "/move-vacuum-to-oven")) {
        observers.forEach(o -> o.onMoveVacuumToOven(msgStr.equals("true")));

      } else if (topic.equals("/" + machineId + "/move-vacuum-to-turn-table")) {
        observers.forEach(o -> o.onMoveVacuumToTurnTable(msgStr.equals("true")));

      } else if (topic.equals("/" + machineId + "/enable-oven-light")) {
        observers.forEach(o -> o.onEnableOvenLight(msgStr.equals("true")));

      } else if (topic.equals("/" + machineId + "/enable-compressor")) {
        observers.forEach(o -> o.onEnableCompressor(msgStr.equals("true")));

      } else if (topic.equals("/" + machineId + "/enable-valve-vacuum")) {
        observers.forEach(o -> o.onEnableValveVacuum(msgStr.equals("true")));

      } else if (topic.equals("/" + machineId + "/enable-valve-move-vacuum")) {
        observers.forEach(o -> o.onEnableValveMoveVacuum(msgStr.equals("true")));

      } else if (topic.equals("/" + machineId + "/enable-valve-move-oven-door")) {
        observers.forEach(o -> o.onEnableValveMoveOvenDoor(msgStr.equals("true")));

      } else if (topic.equals("/" + machineId + "/enable-valve-move-feeder")) {
        observers.forEach(o -> o.onEnableValveMoveFeeder(msgStr.equals("true")));
      }
    }
  }

  @Override
  public void deliveryComplete(IMqttDeliveryToken deliveryToken) {
      //Called when an outgoing publish is complete
  }

  public void connectMqttClient(String topic, MqttClient client) throws MqttException {
    client.subscribe(topic, 2);
  }
}