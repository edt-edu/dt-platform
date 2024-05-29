package vacuumgripperdashboard;

public class AppBuilder extends AppBuilderTOP {

  public AppBuilder(long gemId) {
    super(gemId);
    setPayloadPosition(VacuumGripperDashboardManager.integerValueBuilder().content(0).build().get());
  }
}