import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SortingLineGateway implements MqttCallback {

  protected final String machineId;
  protected List<SortingLineObserver> observers = new ArrayList<>();

  public SortingLineGateway(String machineId) {
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

      if(topic.equals("/" + machineId + "/pulse-counter")){
        observers.forEach(o -> o.onPulseCounter(Integer.parseInt(msgStr)));

      }else if(topic.equals("/" + machineId + "/light-barrier-inlet")){
        observers.forEach(o -> o.onLightBarrierInlet(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/light-barrier-behind-color-sensor")){
        observers.forEach(o -> o.onLightBarrierBehindColorSensor(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/color-sensor")){
        observers.forEach(o -> o.onColorSensor(Integer.parseInt(msgStr)));

      }else if(topic.equals("/" + machineId + "/light-barrier-white")){
        observers.forEach(o -> o.onLightBarrierWhite(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/light-barrier-red")){
        observers.forEach(o -> o.onLightBarrierRed(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/light-barrier-blue")){
        observers.forEach(o -> o.onLightBarrierBlue(msgStr.equals("true")));
      }

      // Inputs of the machine

      else if(topic.equals("/" + machineId + "/move-conveyor")) {
        observers.forEach(o -> o.onMoveConveyor(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/enable-compressor")){
        observers.forEach(o -> o.onEnableCompressor(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/enable-valve-first-ejector")){
        observers.forEach(o -> o.onEnableValveFirstEjector(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/enable-valve-second-ejector")){
        observers.forEach(o -> o.onEnableValveSecondEjector(msgStr.equals("true")));

      }else if(topic.equals("/" + machineId + "/enable-valve-third-ejector")){
        observers.forEach(o -> o.onEnableValveThirdEjector(msgStr.equals("true")));
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
