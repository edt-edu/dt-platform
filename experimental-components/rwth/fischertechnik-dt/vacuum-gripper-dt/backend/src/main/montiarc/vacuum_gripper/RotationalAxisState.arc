package vacuum_gripper;

import vacuum_gripper.VacuumGripperTypes.*;

component RotationalAxisState {
  port in MotorDirection direction;
  port out float position; // in degrees

  // TODO: port to initially set internalPosition?
  float internalPosition = 0;

  compute {
    int encoderDiff = 0;
    float posPerDiff = 360.0f / 32.0f;
    if(direction == MotorDirection.FORWARD){
      encoderDiff = 1;
    }else if(direction == MotorDirection.REVERSE){
      encoderDiff = -1;
    }

    internalPosition = internalPosition + encoderDiff*posPerDiff;
    position = internalPosition % 360.0f;
  }
}