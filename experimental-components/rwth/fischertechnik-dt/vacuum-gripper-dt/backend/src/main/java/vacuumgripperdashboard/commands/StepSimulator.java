package vacuumgripperdashboard.commands;

import jsweet.lang.Erased;
import umlp.backendrte.command.StatusResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;
import vacuum_gripper.VacuumGripper;
import vacuumgripperdashboard.*;

public class StepSimulator extends StepSimulatorTOP {
  @Erased
  static VacuumGripper simulator;

  @Erased
  public static VacuumGripper getSimulator() {
    if(simulator == null){
      simulator = new VacuumGripper();
      simulator.setUp();
      simulator.init();
    }

    return simulator;
  }

  @BackendOnly(value = BackendOnlyModes.THROW_ERROR)
  @Override
  public StatusResult doAction() {
    App app = VacuumGripperDashboardManager.getApp();
    simulationStep(
        app.getVacuumGripperSimulation(),
        getSimulator(),
        app.getSimulationInput(),
        app.getSimulationOutput()
    );

    return StatusResult.ok(getId());
  }

  @BackendOnly(value = BackendOnlyModes.THROW_ERROR)
  public void simulationStep(FFunction vacuumGripperFunction,
                             VacuumGripper simulator,
                             VacuumGripperInput input,
                             VacuumGripperOutput output
  ){
    simulator.getVerticalUp().update(input.getVerticalUp().isContent());
    simulator.getVerticalDown().update(input.getVerticalDown().isContent());
    simulator.getHorizontalForward().update(input.getHorizontalForward().isContent());
    simulator.getHorizontalBack().update(input.getHorizontalBack().isContent());
    simulator.getRotationClockwise().update(input.getRotationClockwise().isContent());
    simulator.getRotationCounterclockwise().update(input.getRotationCounterclockwise().isContent());

    simulator.compute();

    output.getPositionRotate().setContent(simulator.getPositionRotate().getValue());
    output.getPositionHorizontal().setContent(simulator.getPositionHorizontal().getValue());
    output.getPositionVertical().setContent(simulator.getPositionVertical().getValue());

    simulator.tick();
    // TODO: add values to function streams
  }
}
