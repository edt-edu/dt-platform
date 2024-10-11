import org.eclipse.paho.client.mqttv3.*;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ConveyorGateway implements MqttCallback{

  protected final String machineId;
  protected List<ConveyorObserver> observers = new ArrayList<>();

  public ConveyorGateway(String machineId) {
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

      if(topic.equals("/" + machineId + "/phototransistor-feed-station")){
        observers.forEach(o -> o.onPhototransistorFeedStation(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/phototransistor-swap-station")){
        observers.forEach(o -> o.onPhototransistorSwapStation(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/pulse-button")){
        observers.forEach(o -> o.onPulseButton(msgStr.equals("true")));
      }

      // Inputs of the machine

      else if(topic.equals("/" + machineId + "/move-conveyor-forward")){
        observers.forEach(o -> o.onMoveConveyorForward(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/move-conveyor-backward")){
        observers.forEach(o -> o.onMoveConveyorBackward(msgStr.equals("true")));
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
