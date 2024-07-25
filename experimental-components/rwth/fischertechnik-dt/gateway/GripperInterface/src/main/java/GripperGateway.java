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

  public MqttClient createNewMqttClient(MultiMqttCallback callbacks, MqttClient client) throws MqttException {

    callbacks.addCallback(new MqttCallback() {
      @Override
      public void connectionLost(Throwable throwable) {
        //Called when the client lost the connection to the broker
      }

      @Override
      public void messageArrived(String topic, MqttMessage message) throws Exception {
        if(topic != null) {

          String msgStr = new String(message.getPayload(), StandardCharsets.UTF_8);

          // Outputs of the machine

          if(topic.equals("/" + machineId + "/ref-switch-vertical")){
            observers.forEach(o -> o.onRefSwitchVertical(msgStr.equals("true")));

          }else if(topic.equals("/" + machineId + "/ref-switch-horizontal")){
            observers.forEach(o -> o.onRefSwitchHorizontal(msgStr.equals("true")));

          }else if(topic.equals("/" + machineId + "/ref-switch-rotation")){
            observers.forEach(o -> o.onRefSwitchRotation(msgStr.equals("true")));

          }else if(topic.equals("/" + machineId + "/motor-vertical-enc-1")){
            observers.forEach(o -> o.onMotorVerticalEnc1(msgStr.equals("true")));

          }else if(topic.equals("/" + machineId + "/motor-vertical-enc-2")){
            observers.forEach(o -> o.onMotorVerticalEnc2(msgStr.equals("true")));

          }else if(topic.equals("/" + machineId + "/motor-horizontal-enc-1")){
            observers.forEach(o -> o.onMotorHorizontalEnc1(msgStr.equals("true")));

          }else if(topic.equals("/" + machineId + "/motor-horizontal-enc-2")){
            observers.forEach(o -> o.onMotorHorizontalEnc2(msgStr.equals("true")));

          }else if(topic.equals("/" + machineId + "/motor-rotation-enc-1")){
            observers.forEach(o -> o.onMotorRotationEnc1(msgStr.equals("true")));

          }else if(topic.equals("/" + machineId + "/motor-rotation-enc-2")){
            observers.forEach(o -> o.onMotorRotationEnc2(msgStr.equals("true")));

          }else if(topic.equals("/" + machineId + "/motor-vertical-pos")){
            observers.forEach(o -> o.onMotorVerticalPos(Integer.parseInt(msgStr)));

          }else if(topic.equals("/" + machineId + "/motor-horizontal-pos")){
            observers.forEach(o -> o.onMotorHorizontalPos(Integer.parseInt(msgStr)));

          }else if(topic.equals("/" + machineId + "/motor-rotation-pos")){
            observers.forEach(o -> o.onMotorRotationPos(Integer.parseInt(msgStr)));

            // Inputs of the machine

          }else if(topic.equals("/" + machineId + "/move-vertical-up")){
            observers.forEach(o -> o.onMoveVerticalUp(msgStr.equals("true")));

          }else if(topic.equals("/" + machineId + "/move-vertical-down")){
            observers.forEach(o -> o.onMoveVerticalDown(msgStr.equals("true")));

          }else if(topic.equals("/" + machineId + "/move-horizontal-up")){
            observers.forEach(o -> o.onMoveHorizontalUp(msgStr.equals("true")));

          }else if(topic.equals("/" + machineId + "/move-horizontal-down")){
            observers.forEach(o -> o.onMoveHorizontalDown(msgStr.equals("true")));

          }else if(topic.equals("/" + machineId + "/move-rotation-clockwise")){
            observers.forEach(o -> o.onMoveRotationClockwise(msgStr.equals("true")));

          }else if(topic.equals("/" + machineId + "/move-rotation-counterclockwise")){
            observers.forEach(o -> o.onMoveRotationCounterclockwise(msgStr.equals("true")));

          }else if(topic.equals("/" + machineId + "/enable-compressor")){
            observers.forEach(o -> o.onEnableCompressor(msgStr.equals("true")));

          }else if(topic.equals("/" + machineId + "/enable-valve")){
            observers.forEach(o -> o.onEnableValve(msgStr.equals("true")));

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
    
    // TODO: Quality of service ggfs ändern
    client.subscribe(topic, 2);
  }

  /* public static void main(String[] args) throws MqttException, InterruptedException {

    // create gateway
    GripperGateway gateway = new GripperGateway("vacuum-gripper");

    // create MultiCallback list
    MultiMqttCallback clientOneCallbacks = new MultiMqttCallback();

    // create first client
    MqttClient client1 = new MqttClient(
        "tcp://localhost:1883",
        "client1"); // TODO: move me up 1 level
    client1.setCallback(clientOneCallbacks);

    client1 = gateway.createNewMqttClient(clientOneCallbacks ,client1);

    clientOneCallbacks.addCallback(new MqttCallback() {
      @Override
      public void connectionLost(Throwable throwable) {
        System.out.print("Connection lost: client1");
      }

      @Override
      public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("client1 received message on topic: " + topic + ", Msg:" + new String(message.getPayload(), StandardCharsets.UTF_8));
      }

      @Override
      public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

      }
    });

    client1.connect();

    gateway.connectMqttClient("/vacuum-gripper/ref-switch-vertical", client1);
    System.out.println("Client1 connected");

    // create second client
    MultiMqttCallback clientTwoCallbacks = new MultiMqttCallback();

    MqttClient client2 = new MqttClient(
        "tcp://localhost:1883",
        "client2"); // TODO: move me up 1 level
    client2.setCallback(clientTwoCallbacks);

    client2.connect();

    clientTwoCallbacks.addCallback(new MqttCallback() {
      @Override
      public void connectionLost(Throwable throwable) {

      }

      @Override
      public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("client2 received message on topic: " + topic + ", Msg:" + new String(message.getPayload(), StandardCharsets.UTF_8));
      }

      @Override
      public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

      }
    });

    gateway.connectMqttClient("/vacuum-gripper/ref-switch-vertical", client2);
    System.out.println("Client2 connected");

    // create publisher
    MqttClient publisher = new MqttClient(
        "tcp://localhost:1883",
        "publisher");

    publisher.connect();
    System.out.println("Publisher connected");

  }*/
}
