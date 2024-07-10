package vacuumgripperdashboard;

public class PositionService {
  boolean payloadGrabbed = false;

  public void updatePayloadPosition(){
    App app = VacuumGripperDashboardManager.getApp();
    VacuumGripperOutput output = app.getSimulationOutput();

    Integer payloadPosition = app.getPayloadPosition().getContent();
    if (output.isInLoadingZone()) {
      System.out.println("In loading zone, with payload position " + payloadPosition);
      if (0 == payloadPosition) {
        System.out.println("Payload in loading zone");
        if (!payloadGrabbed) {
          System.out.println("No payload grabbed");
          payloadGrabbed = true;
          app.getPayloadPosition().setContent(1);
        }
      }
    }

    if (output.isInDropoffZone()) {
      System.out.println("In dropoff zone, with payload position "+ payloadPosition);
      if (1 == payloadPosition) {
        System.out.println("Payload in transit");
        if (payloadGrabbed) {
          System.out.println("Payload was grabbed");
          payloadGrabbed = false;
          app.getPayloadPosition().setContent(2);
        }
      }
    }
  }

  public void connectTo(VacuumGripperOutput physicalTwinOutput) {
    physicalTwinOutput.getPositionRotate().addObserver(new FloatValueObserver() {
      @Override
      public void notifySetContent(FloatValue floatValue, Float oldValue, Float o) {
        updatePayloadPosition();
      }
    });

    physicalTwinOutput.getPositionVertical().addObserver(new FloatValueObserver() {
      @Override
      public void notifySetContent(FloatValue floatValue, Float oldValue, Float o) {
        updatePayloadPosition();
      }
    });

    physicalTwinOutput.getPositionHorizontal().addObserver(new FloatValueObserver() {
      @Override
      public void notifySetContent(FloatValue floatValue, Float oldValue, Float o) {
        updatePayloadPosition();
      }
    });
  }
}
