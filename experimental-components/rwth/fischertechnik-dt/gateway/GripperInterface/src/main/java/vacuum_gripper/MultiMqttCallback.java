import org.eclipse.paho.client.mqttv3.*;

import java.util.ArrayList;
import java.util.List;

public class MultiMqttCallback implements MqttCallback{

  protected List<MqttCallback> callbacks = new ArrayList<>();

  // add callback to MultiMqttCallback list
  public void addCallback(MqttCallback mqttCallback) {
    callbacks.add(mqttCallback);
  }

  @Override
  public void connectionLost(Throwable throwable) {
    //Called when the client lost the connection to the broker
    for (MqttCallback callback : callbacks) {
      callback.connectionLost(throwable);
    }
  }

  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {
    //Called when the client received a message
    for (MqttCallback callback : callbacks) {
      callback.messageArrived(topic, message);
    }
  }

  @Override
  public void deliveryComplete(IMqttDeliveryToken deliveryToken) {
    //Called when an outgoing publish is complete
    for (MqttCallback callback : callbacks) {
      callback.deliveryComplete(deliveryToken);
    }
  }
}
