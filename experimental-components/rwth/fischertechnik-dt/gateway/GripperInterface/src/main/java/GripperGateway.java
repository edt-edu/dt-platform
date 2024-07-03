import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class GripperGateway {
  
  protected final String machineId;
  protected List<GripperObserver> observers = new ArrayList<>();

  public GripperGateway(String machineId) {
    this.machineId = machineId;
  }

  public MqttClient createNewMqttClient(MultiMqttCallback callbacks) throws MqttException {
    MqttClient client = new MqttClient(
        "tcp://localhost:1883",
        clientId); // TODO: move me up 1 level

    callbacks.addCallback(new MqttCallback() {
      @Override
      public void connectionLost(Throwable throwable) {
        //Called when the client lost the connection to the broker
      }

      @Override
      public void messageArrived(String topic, MqttMessage message) throws Exception {
        if(topic != null) {

          // Outputs of the machine
          String msgStr = new String(message.getPayload(), StandardCharsets.UTF_8);

          if(topic.equals("/" + machineId + "/ref-switch-vertical")){
            observers.forEach(o -> o.onRefSwitchVertical(msgStr.equals("true")));
          }else if(topic.equals("/" + machineId + "/ref-switch-horizontal")){
            observers.forEach(o -> o.onRefSwitchHorizontal(message.toString().equals("true")));

          }else if(topic.equals("/" + machineId + "/ref-switch-rotation")){
            observers.forEach(o -> o.onRefSwitchRotation(message.toString().equals("true")));

          }else if(topic.equals("/" + machineId + "/motor-vertical-enc-1")){
            observers.forEach(o -> o.onMotorVerticalEnc1(message.toString().equals("true")));

          }else if(topic.equals("/" + machineId + "/motor-vertical-enc-2")){
            observers.forEach(o -> o.onMotorVerticalEnc2(message.toString().equals("true")));

          }else if(topic.equals("/" + machineId + "/motor-horizontal-enc-1")){
            observers.forEach(o -> o.onMotorHorizontalEnc1(message.toString().equals("true")));

          }else if(topic.equals("/" + machineId + "/motor-horizontal-enc-2")){
            observers.forEach(o -> o.onMotorHorizontalEnc2(message.toString().equals("true")));

          }else if(topic.equals("/" + machineId + "/motor-rotation-enc-1")){
            observers.forEach(o -> o.onMotorRotationEnc1(message.toString().equals("true")));

          }else if(topic.equals("/" + machineId + "/motor-rotation-enc-2")){
            observers.forEach(o -> o.onMotorRotationEnc2(message.toString().equals("true")));

            // todo: Wie wandeln wir byte[] in int um?
          }else if(topic.equals("/" + machineId + "/motor-vertical-pos")){
            observers.forEach(o -> o.onMotorVerticalPos(Integer.parseInt(msgStr)));

          }else if(topic.equals("/" + machineId + "/motor-horizontal-pos")){
            observers.forEach(o -> o.onMotorHorizontalPos(message ...);

          }else if(topic.equals("/" + machineId + "/motor-rotation-pos")){
            observers.forEach(o -> o.onMotorRotationPos(message ...);

            // Inputs of the machine

          }else if(topic.equals("/" + machineId + "/move-vertical-up")){
            observers.forEach(o -> o.onMoveVerticalUp(message.toString().equals("true")));

          }else if(topic.equals("/" + machineId + "/move-vertical-down")){
            observers.forEach(o -> o.onMoveVerticalDown(message.toString().equals("true")));

          }else if(topic.equals("/" + machineId + "/move-horizontal-up")){
            observers.forEach(o -> o.onMoveHorizontalUp(message.toString().equals("true")));

          }else if(topic.equals("/" + machineId + "/move-horizontal-down")){
            observers.forEach(o -> o.onMoveHorizontalDown(message.toString().equals("true")));

          }else if(topic.equals("/" + machineId + "/move-rotation-clockwise")){
            observers.forEach(o -> o.onMoveRotationClockwise(message.toString().equals("true")));

          }else if(topic.equals("/" + machineId + "/move-rotation-counterclockwise")){
            observers.forEach(o -> o.onMoveRotationCounterclockwise(message.toString().equals("true")));

          }else if(topic.equals("/" + machineId + "/enable-compressor")){
            observers.forEach(o -> o.onEnableCompressor(message.toString().equals("true")));

          }else if(topic.equals("/" + machineId + "/enable-valve")){
            observers.forEach(o -> o.onEnableValve(message.toString().equals("true")));

          }else{
            System.out.println("Message arrived at client " + client.getClientId() + ": No match for topic");

          }
        }else{
          System.out.println("Message arrived at client " + client.getClientId() + ": Invalid topic");
        }
      }

      @Override
      public void deliveryComplete(IMqttDeliveryToken deliveryToken) {
        //Called when an outgoing publish is complete
      }
    });

    return client;
  }

  public void connectMqttClient(String topic, MqttClient client) throws MqttException {
    client.connect();
    // TODO: Quality of service ggfs ändern
    client.subscribe(topic, 2);

    // todo: subscribe topics
  }
}
