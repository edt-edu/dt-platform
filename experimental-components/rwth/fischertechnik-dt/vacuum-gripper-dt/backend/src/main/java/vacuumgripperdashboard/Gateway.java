package vacuumgripperdashboard;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import umlp.backendrte.common.NotificationScope;
import vacuumgripperdashboard.util.SimpleMqtt;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;

import static vacuumgripperdashboard.VacuumGripperDashboardManager.getApp;

public class Gateway {
  public void initMqttConnector(StepSimulator simulator) throws MqttException {
    MqttAsyncClient client = new MqttAsyncClient(
        System.getenv().getOrDefault("MQTT_BROKER_ADDRESS", "tcp://localhost:1883"),
        "VacuumGripperDt-" + Math.abs(new Random().nextInt())
    );

    SimpleMqtt callbacks = new SimpleMqtt(client);
    client.setCallback(callbacks);
    client.connect().waitForCompletion();
    client.publish("/vacuum-gripper-dt/payload/position", new MqttMessage("UNKNOWN".getBytes(StandardCharsets.UTF_8)));

    App app = getApp();
    VacuumGripperInput input = app.getPhysicalTwinInput();
    VacuumGripperOutput output = app.getPhysicalTwinOutput();

    String prefix = "/vacuum-gripper/2.5-Vac";
    callbacks.subscribeBool(prefix + "/verticalUp", b -> input.getVerticalUp().setContent(b));
    callbacks.subscribeBool(prefix + "/verticalDown", b -> input.getVerticalDown().setContent(b));
    callbacks.subscribeBool(prefix + "/horizontalForward", b -> input.getHorizontalForward().setContent(b));
    callbacks.subscribeBool(prefix + "/horizontalBack", b -> input.getHorizontalBack().setContent(b));
    callbacks.subscribeBool(prefix + "/rotationClockwise", b -> input.getRotationClockwise().setContent(b));
    callbacks.subscribeBool(prefix + "/rotationCounterclockwise", b -> input.getRotationCounterclockwise().setContent(b));

    callbacks.subscribeLong(prefix + "/counterHorizontal", i -> {
      float content = i / 20f;
      output.getPositionHorizontal().setContent(content);
      simulator.setHorizontalPosition(content);
    });
    callbacks.subscribeLong(prefix + "/counterVertical", i -> {
      float content = 100f - (i / 20f);
      output.getPositionVertical().setContent(content);
      simulator.setVerticalPosition(content);
    });
    callbacks.subscribeLong(prefix + "/counterRotation", i -> {
      float content = 180f - (i / 10f);
      content = Math.abs(content);
      content = content % 360f;
      output.getPositionRotate().setContent(content);
      simulator.setRotationPosition(content);
    });

    app.getPayloadPosition().addObserver(new IntegerValueObserver() {
      @Override
      public void notifySetContent(IntegerValue value, Integer oldValue, Integer o) {
        if(Objects.equals(o, oldValue)){
          return;
        }

        String payload;
        payload = payloadPositionToStr(o);

        try {
          System.out.println("Pre publish");
          MqttMessage msg = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
          System.out.println("Msg created");
          client.publish("/vacuum-gripper-dt/payload/position", msg);
          System.out.println("Post publish");
        } catch (MqttException e) {
          System.out.println("Error while publishing!");
          throw new RuntimeException(e);
        }
      }

      @Override
      public NotificationScope getScope() {
        return NotificationScope.MINIMAL;
      }
    });

    callbacks.subscribeStr(
        "/vacuum-gripper-dt/payload/position/update",
        str -> app.getPayloadPosition().setContent(strToPayloadPosition(str))
    );
  }

  private static String payloadPositionToStr(Integer o) {
    String payload;
    if(o == 1){
      payload = "IN_TRANSIT";
    } else if(o == 2){
      payload = "DROPOFF_ZONE";
    } else if(o == 0){
      payload = "LOADING_ZONE";
    } else {
      payload = "UNKNOWN";
    }
    return payload;
  }

  private static int strToPayloadPosition(String str){
    switch (str){
      case "IN_TRANSIT": return 1;
      case "DROPOFF_ZONE": return 2;
      case "LOADING_ZONE": return 0;
      default: return 3;
    }
  }

}
