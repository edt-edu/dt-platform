package vacuum_gripper;

import vacuum_gripper.VacuumGripperTypes.*;

component RotationalAxis {
  port in MotorDirection direction;
  port out float position,
       out EncoderPulses encoderPulses;

  EncoderMotor motor;
  RotationalAxisState rotationalAxisState;

  direction -> motor.direction;
  direction -> rotationalAxisState.direction;
  rotationalAxisState.position -> position;

  motor.encoderPulses -> encoderPulses;
}