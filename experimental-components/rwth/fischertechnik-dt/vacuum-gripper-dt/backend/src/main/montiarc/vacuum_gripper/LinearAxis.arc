package vacuum_gripper;

import vacuum_gripper.VacuumGripperTypes.*;

component LinearAxis {
  port in MotorDirection direction;
  port out float position,
       out EncoderPulses encoderPulses;

  EncoderMotor motor;
  LinearAxisState linearAxisState;

  direction -> motor.direction;
  direction -> linearAxisState.direction;
  linearAxisState.position -> position;
  motor.encoderPulses -> encoderPulses;
}