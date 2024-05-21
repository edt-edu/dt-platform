package vacuum_gripper;

import montiarc.rte.log.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vacuum_gripper.VacuumGripperTypes.EncoderPulses;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VacuumGripperSimulationTest {
  VacuumGripper vacuumGripper;

  @BeforeEach
  public void setup(){
    vacuumGripper = new VacuumGripper();
    vacuumGripper.setUp();
    vacuumGripper.init();
    Log.setTraceEnabled(true);
    Log.setDebugEnabled(true);
  }

  @Test
  public void testRotationForward(){
    VacuumGripperInputs input = new VacuumGripperInputsBuilder().setRotationClockwise(true).build();
    VacuumGripperOutputs expectedOutputs = new VacuumGripperOutputsBuilder()
        .setPositionRotate(1.0f)
        .setEncoderPulsesRotate(new EncoderPulses(false, true, false))
        .build();

    input.applyTo(vacuumGripper);
    vacuumGripper.compute();

    assertEquals(expectedOutputs, VacuumGripperOutputs.from(vacuumGripper));
  }
}
