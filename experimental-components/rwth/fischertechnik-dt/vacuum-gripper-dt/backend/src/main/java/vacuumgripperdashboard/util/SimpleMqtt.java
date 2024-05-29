package vacuumgripperdashboard.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import de.se_rwth.commons.logging.Log;
import org.eclipse.paho.client.mqttv3.*;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class SimpleMqtt implements MqttCallback {
  private Multimap<String, Consumer<MqttMessage>> actions = ArrayListMultimap.create();
  final MqttClient client;
  final MqttAsyncClient asyncClient;
  final boolean isAsync;

  public SimpleMqtt(MqttClient client) {
    this.client = client;
    this.asyncClient = null;
    isAsync = false;
  }
  public SimpleMqtt(MqttAsyncClient client) {
    this.asyncClient = client;
    this.client = null;
    isAsync = true;
  }

  public Closeable subscribe(String topic, Consumer<MqttMessage> action) throws MqttException {
    if(isAsync){
      asyncClient.subscribe(topic, 1);
    }else {
      client.subscribe(topic);
    }
    actions.put(topic, action);
    return () -> actions.remove(topic, action);
  }

  public Closeable subscribeStr(String topic, Consumer<String> action) throws MqttException {
    return subscribe(topic, msg -> action.accept(new String(msg.getPayload(), StandardCharsets.UTF_8)));
  }

  public Closeable subscribeBool(String topic, Consumer<Boolean> action) throws MqttException {
    return subscribeStr(topic, str -> action.accept(Boolean.parseBoolean(str)));
  }

  public Closeable subscribeInt(String topic, Consumer<Integer> action) throws MqttException {
    return subscribeStr(topic, str -> action.accept(Integer.parseInt(str)));
  }

  public Closeable subscribeLong(String topic, Consumer<Long> action) throws MqttException {
    return subscribeStr(topic, str -> action.accept(Long.parseLong(str)));
  }

  @Override
  public void connectionLost(Throwable throwable) {
    Log.error("Connection to mqtt lost", throwable);
  }

  @Override
  public void messageArrived(String s, MqttMessage mqttMessage) {
    actions.get(s).forEach(a -> a.accept(mqttMessage));
  }

  @Override
  public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

  }
}
