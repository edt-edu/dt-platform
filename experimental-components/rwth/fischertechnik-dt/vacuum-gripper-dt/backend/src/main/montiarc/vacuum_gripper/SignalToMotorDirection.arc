package vacuum_gripper;

import vacuum_gripper.VacuumGripperTypes.*;

component SignalToMotorDirection {
  port in boolean signal1,
       in boolean signal2,
       out MotorDirection direction;

  compute {
    if(!signal1 && !signal2){
      direction = MotorDirection.STOP;
    } else if(signal1 && signal2){
      direction = MotorDirection.STOP;
    } else if(signal1){
      direction = MotorDirection.FORWARD;
    } else {
      direction = MotorDirection.REVERSE;
    }
  }
}