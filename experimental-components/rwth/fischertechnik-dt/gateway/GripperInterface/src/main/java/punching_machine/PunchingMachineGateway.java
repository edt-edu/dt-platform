package punching_machine;

import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PunchingMachineGateway implements MqttCallback {

  protected final String machineId;
  protected List<PunchingMachineObserver> observers = new ArrayList<>();

  public PunchingMachineGateway(String machineId) {
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

      if(topic.equals("/" + machineId + "/phototransistor-goods-in-out")){
        observers.forEach(o -> o.onPhototransistorGoodsInOut(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/phototransistor-punching-machine")){
        observers.forEach(o -> o.onPhototransistorPunchingMachine(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/switch-punching-machine-up")){
        observers.forEach(o -> o.onSwitchPunchingMachineUp(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/switch-punching-machine-down")){
        observers.forEach(o -> o.onSwitchPunchingMachineDown(msgStr.equals("true")));
      }

      // Inputs of the machine

      else if(topic.equals("/" + machineId + "/move-conveyor-forward")){
        observers.forEach(o -> o.onMoveConveyorForward(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-conveyor-backward")){
        observers.forEach(o -> o.onMoveConveyorBackward(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-punching-machine-up")){
        observers.forEach(o -> o.onMovePunchingMachineUp(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-punching-machine-down")){
        observers.forEach(o -> o.onMovePunchingMachineDown(msgStr.equals("true")));
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

  public void addObserver(PunchingMachineObserver observer){
    observers.add(observer);
  }

  public void removeObserver(PunchingMachineObserver observer){
    observers.remove(observer);
  }
}
