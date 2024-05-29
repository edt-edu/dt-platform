package vacuumgripperdashboard;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VacuumGripperOutputTest {

  @Test
  public void testAngleClose(){
    assertTrue(VacuumGripperOutput.rotationCloseTo(0,0, 5));
    assertTrue(VacuumGripperOutput.rotationCloseTo(359,0, 5));

    assertFalse(VacuumGripperOutput.rotationCloseTo(0,6, 5));
    assertFalse(VacuumGripperOutput.rotationCloseTo(350,0, 5));
  }

}