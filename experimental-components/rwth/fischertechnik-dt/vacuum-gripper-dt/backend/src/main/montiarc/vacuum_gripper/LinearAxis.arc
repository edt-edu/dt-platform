package vacuum_gripper;

import vacuum_gripper.VacuumGripperTypes.*;

component LinearAxis(float min, float max) {
  port in MotorDirection direction;
  port out float position,
       out EncoderPulses encoderPulses;

  EncoderMotor motor;
  LinearAxisState linearAxisState(min, max);

  direction -> motor.direction;
  direction -> linearAxisState.direction;
  linearAxisState.position -> position;
  motor.encoderPulses -> encoderPulses;
}