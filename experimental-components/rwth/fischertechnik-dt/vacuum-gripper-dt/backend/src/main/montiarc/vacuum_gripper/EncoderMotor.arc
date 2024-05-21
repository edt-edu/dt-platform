package vacuum_gripper;

import vacuum_gripper.VacuumGripperTypes.*;

component EncoderMotor {
  port in MotorDirection direction;
  port out EncoderPulses encoderPulses;

  // TODO: internal state for absolute position?

  boolean wasRising = false;
  compute {
    encoderPulses = EncoderPulses.EncoderPulses(direction == MotorDirection.STOP, direction == MotorDirection.FORWARD, wasRising);
    wasRising = !wasRising;
  }
}