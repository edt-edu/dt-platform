package indexed_line;

import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class IndexedLineGateway implements MqttCallback{

  protected final String machineId;
  protected List<IndexedLineObserver> observers = new ArrayList<>();

  public IndexedLineGateway(String machineId) {
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

      if(topic.equals("/" + machineId + "/button-slider-1-front")){
        observers.forEach(o -> o.onButtonSlider1Front(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/button-slider-1-rear")){
        observers.forEach(o -> o.onButtonSlider1Rear(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/button-slider-2-front")){
        observers.forEach(o -> o.onButtonSlider2Front(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/button-slider-2-rear")){
        observers.forEach(o -> o.onButtonSlider2Rear(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/phototransistor-slider-1")){
        observers.forEach(o -> o.onPhototransistorSlider1(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/phototransistor-milling-machine")){
        observers.forEach(o -> o.onPhototransistorMillingMachine(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/phototransistor-loading-station")){
        observers.forEach(o -> o.onPhototransistorLoadingStation(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/phototransistor-drilling-machine")){
        observers.forEach(o -> o.onPhototransistorDrillingMachine(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/phototransistor-conveyor-swap")){
        observers.forEach(o -> o.onPhototransistorConveyorSwap(msgStr.equals("true")));
      }

      // Inputs of the machine

      else if(topic.equals("/" + machineId + "/move-slider-1-backward")){
        observers.forEach(o -> o.onMoveSlider1Backward(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-slider-1-forward")){
        observers.forEach(o -> o.onMoveSlider1Forward(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-slider-2-backward")){
        observers.forEach(o -> o.onMoveSlider2Backward(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-slider-2-forward")){
        observers.forEach(o -> o.onMoveSlider2Forward(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-conveyor-feed")){
        observers.forEach(o -> o.onMoveConveyorFeed(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-conveyor-milling-machine")){
        observers.forEach(o -> o.onMoveConveyorMillingMachine(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-milling-machine")){
        observers.forEach(o -> o.onMoveMillingMachine(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-conveyor-drilling-machine")){
        observers.forEach(o -> o.onMoveConveyorDrillingMachine(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-drilling-machine")){
        observers.forEach(o -> o.onMoveDrillingMachine(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-conveyor-swap")){
        observers.forEach(o -> o.onMoveConveyorSwap(msgStr.equals("true")));
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

  public void addObserver(IndexedLineObserver observer){
    observers.add(observer);
  }

  public void removeObserver(IndexedLineObserver observer){
    observers.remove(observer);
  }
}
