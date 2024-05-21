package vacuum_gripper;

import vacuum_gripper.VacuumGripperTypes.*;

component RotationalAxisState {
  port in MotorDirection direction;
  port out float position;

  // TODO: port to initially set internalPosition?
  float internalPosition = 0;

  compute {
    int encoderDiff = 0;
    int posPerDiff = 1;
    if(direction == MotorDirection.FORWARD){
      encoderDiff = 1;
    }else if(direction == MotorDirection.REVERSE){
      encoderDiff = -1;
    }

    internalPosition = internalPosition + encoderDiff*posPerDiff;

    float pi = 62832/20000; // approximation for pi
    position = internalPosition % (2*pi);
  }

}