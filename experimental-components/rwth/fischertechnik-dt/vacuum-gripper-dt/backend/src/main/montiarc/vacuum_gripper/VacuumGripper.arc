package vacuum_gripper;

import vacuum_gripper.VacuumGripperTypes.*;

/**
 * Simulation model of the VacuumGripper
 * The simulation step size must be fine enough for the encoder pulses
 * Input/outputs from 'Circuit layout of the Vacuum Gripper Robot 24V'
 * Electrical system not modelled
 * Vacuum pump and valve are not modelled, since they have no measurable output
 */
component VacuumGripper {
  // Physical ports
  port in boolean verticalUp,
       in boolean verticalDown,
       in boolean horizontalForward,
       in boolean horizontalBack,
       in boolean rotationClockwise,
       in boolean rotationCounterclockwise;

  //port out boolean referenceSwitchRotate,
  //     out boolean referenceSwitchHorizontal,
  //     out boolean referenceSwitchVertical;

  port out EncoderPulses encoderPulsesRotate,
       out EncoderPulses encoderPulsesHorizontal,
       out EncoderPulses encoderPulsesVertical;

  // Virtual sensors
  port out float positionRotate,
       out float positionHorizontal,
       out float positionVertical;

  // Subcomponents
  RotationalAxis axisRotate;
  LinearAxis axisHorizontal;
  LinearAxis axisVertical;

  // Connections
  SignalToMotorDirection convertRotate;
  SignalToMotorDirection convertHorizontal;
  SignalToMotorDirection convertVertical;

  verticalUp -> convertVertical.signal1;
  verticalDown -> convertVertical.signal2;
  convertVertical.direction -> axisVertical.direction;

  horizontalForward -> convertHorizontal.signal1;
  horizontalBack -> convertHorizontal.signal2;
  convertHorizontal.direction -> axisHorizontal.direction;

  rotationClockwise -> convertRotate.signal1;
  rotationCounterclockwise -> convertRotate.signal2;
  convertRotate.direction -> axisRotate.direction;

  axisRotate.encoderPulses -> encoderPulsesRotate;
  axisHorizontal.encoderPulses -> encoderPulsesHorizontal;
  axisVertical.encoderPulses -> encoderPulsesVertical;

  axisRotate.position -> positionRotate;
  axisHorizontal.position -> positionHorizontal;
  axisVertical.position -> positionVertical;
}