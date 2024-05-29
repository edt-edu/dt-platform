package vacuumgripperdashboard;

public class PositionService {
  boolean payloadGrabbed = false;

  public void updatePayloadPosition(){
    App app = VacuumGripperDashboardManager.getApp();
    VacuumGripperOutput output = app.getSimulationOutput();

    Integer payloadPosition = app.getPayloadPosition();
    if (output.isInLoadingZone()) {
      System.out.println("In loading zone, with payload position " + payloadPosition);
      if (0 == payloadPosition) {
        System.out.println("Payload in loading zone");
        if (!payloadGrabbed) {
          System.out.println("No payload grabbed");
          payloadGrabbed = true;
          app.setPayloadPosition(1);
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
          app.setPayloadPosition(2);
        }
      }
    }
  }
}
