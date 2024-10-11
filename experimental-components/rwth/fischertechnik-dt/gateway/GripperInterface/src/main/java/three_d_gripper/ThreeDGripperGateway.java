package three_d_gripper;

import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ThreeDGripperGateway implements MqttCallback {

  protected final String machineId;
  protected List<ThreeDGripperObserver> observers = new ArrayList<>();

  public ThreeDGripperGateway(String machineId) {
    this.machineId = machineId;
  }

  @Override
  public void connectionLost(Throwable throwable) {
    //Called when the client lost the connection to the broker
  }

  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {
    if (topic != null) {

      String msgStr = new String(message.getPayload(), StandardCharsets.UTF_8);

      // Outputs of the machine

      if(topic.equals("/" + machineId + "/ref-switch-claw")){
        observers.forEach(o -> o.onRefSwitchClaw(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/enc-counter-claw")) {
        observers.forEach(o -> o.onEncCounterClaw(Integer.parseInt(msgStr)));

      }else if(topic.equals("/" + machineId + "/ref-switch-gripper")){
        observers.forEach(o -> o.onRefSwitchGripper(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/enc-counter-gripper")) {
        observers.forEach(o -> o.onEncCounterGripper(Integer.parseInt(msgStr)));

      }else if(topic.equals("/" + machineId + "/ref-switch-vertical")){
        observers.forEach(o -> o.onRefSwitchVertical(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/ref-switch-rotation")){
        observers.forEach(o -> o.onRefSwitchRotation(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/motor-vertical-enc-1")){
        observers.forEach(o -> o.onMotorVerticalEnc1(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/motor-vertical-enc-2")){
        observers.forEach(o -> o.onMotorVerticalEnc2(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/motor-rotation-enc-1")){
        observers.forEach(o -> o.onMotorRotationEnc1(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/motor-rotation-enc-2")){
        observers.forEach(o -> o.onMotorRotationEnc2(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/motor-vertical-pos")) {
        observers.forEach(o -> o.onMotorVerticalPos(Integer.parseInt(msgStr)));

      }else if(topic.equals("/" + machineId + "/motor-rotation-pos")) {
        observers.forEach(o -> o.onMotorRotationPos(Integer.parseInt(msgStr)));
      }

      // Inputs of the machine

      else if(topic.equals("/" + machineId + "/open-gripper")) {
        observers.forEach(o -> o.onOpenGripper(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/close-gripper")){
        observers.forEach(o -> o.onCloseGripper(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-horizontal-forward")){
        observers.forEach(o -> o.onMoveHorizontalForward(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-horizontal-backward")){
        observers.forEach(o -> o.onMoveHorizontalBackward(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-vertical-up")){
        observers.forEach(o -> o.onMoveVerticalUp(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-vertical-down")){
        observers.forEach(o -> o.onMoveVerticalDown(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-rotation-clockwise")){
        observers.forEach(o -> o.onMoveRotationClockwise(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-rotation-counterclockwise")) {
        observers.forEach(o -> o.onMoveRotationCounterclockwise(msgStr.equals("true")));
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

  public void addObserver(ThreeDGripperObserver observer){
    observers.add(observer);
  }

  public void removeObserver(ThreeDGripperObserver observer){
    observers.remove(observer);
  }
}