package vacuumgripperdashboard;

import umlp.backendrte.common.NotificationScope;

public class LogicProcessor {
  public LogicProcessor(StepSimulator simulator) {
    VacuumGripperDashboardManager.addObserver(new VacuumGripperDashboardObserver() {
      @Override
      public void maybeNotifySimulatorStepAdded(SimulatorStep simulatorStep, NotificationScope scope) {
        simulator.step();
      }
    });
  }
}
