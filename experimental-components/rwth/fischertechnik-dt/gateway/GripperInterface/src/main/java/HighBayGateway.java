import org.eclipse.paho.client.mqttv3.*;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HighBayGateway implements MqttCallback{

  protected final String machineId;
  protected List<HighBayObserver> observers = new ArrayList<>();

  public HighBayGateway(String machineId) {
    this.machineId = machineId;
  }


  @Override
  public void connectionLost(Throwable throwable) {
    //Called when the client lost the connection to the broker
  }

  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {
    if(topic != null) {

      String msgStr = new String(message.getPayload(), StandardCharsets.UTF_8);

      // Outputs of the machine

      if(topic.equals("/" + machineId + "/ref-switch-horizontal")){
        observers.forEach(o -> o.onRefSwitchHorizontal(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/light-barrier-inside")){
        observers.forEach(o -> o.onLightBarrierInside(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/light-barrier-outside")){
        observers.forEach(o -> o.onLightBarrierOutside(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/ref-switch-vertical")){
        observers.forEach(o -> o.onRefSwitchVertical(msgStr.equals("true")));

      }else  if(topic.equals("/" + machineId + "/trail-sensor-lower")){
        observers.forEach(o -> o.onTrailSensorLower(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/trail-sensor-upper")){
        observers.forEach(o -> o.onTrailSensorUpper(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/horizontal-enc-1")){
        observers.forEach(o -> o.onHorizontalEnc1(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/horizontal-enc-2")){
        observers.forEach(o -> o.onHorizontalEnc2(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/vertical-enc-1")){
        observers.forEach(o -> o.onVerticalEnc1(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/vertical-enc-2")){
        observers.forEach(o -> o.onVerticalEnc2(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/ref-switch-cantilever-front")) {
        observers.forEach(o -> o.onRefSwitchCantileverFront(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/ref-switch-cantilever-back")){
          observers.forEach(o -> o.onRefSwitchCantileverBack(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/vertical-pos")){
        observers.forEach(o -> o.onVerticalPos(Integer.parseInt(msgStr)));

      }else if(topic.equals("/" + machineId + "/horizontal-pos")){
        observers.forEach(o -> o.onHorizontalPos(Integer.parseInt(msgStr)));
      }

      // Inputs of the machine

      else if(topic.equals("/" + machineId + "/move-conveyor-forward")){
        observers.forEach(o -> o.onMoveConveyorForward(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-conveyor-backward")){
        observers.forEach(o -> o.onMoveConveyorBackward(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-arm-to-rack")){
        observers.forEach(o -> o.onMoveArmToRack(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-arm-to-conveyor")) {
        observers.forEach(o -> o.onMoveArmToConveyor(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-vertical-down")){
        observers.forEach(o -> o.onMoveVerticalDown(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-vertical-up")){
        observers.forEach(o -> o.onMoveVerticalUp(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-cantilever-forward")){
        observers.forEach(o -> o.onMoveCantileverForward(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-cantilever-backward")){
        observers.forEach(o -> o.onMoveCantileverBackward(msgStr.equals("true")));
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
