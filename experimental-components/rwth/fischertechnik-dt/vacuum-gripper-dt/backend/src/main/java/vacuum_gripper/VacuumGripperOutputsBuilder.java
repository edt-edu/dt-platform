package vacuum_gripper;

import vacuum_gripper.VacuumGripperTypes.EncoderPulses;

public class VacuumGripperOutputsBuilder {
  private EncoderPulses encoderPulsesRotate = new EncoderPulses(false, false, false);
  private EncoderPulses encoderPulsesHorizontal = new EncoderPulses(false, false, false);
  private EncoderPulses encoderPulsesVertical = new EncoderPulses(false, false, false);
  private float positionRotate = 0;
  private float positionHorizontal = 0;
  private float positionVertical = 0;

  public VacuumGripperOutputsBuilder setEncoderPulsesRotate(EncoderPulses encoderPulsesRotate) {
    this.encoderPulsesRotate = encoderPulsesRotate;
    return this;
  }

  public VacuumGripperOutputsBuilder setEncoderPulsesHorizontal(EncoderPulses encoderPulsesHorizontal) {
    this.encoderPulsesHorizontal = encoderPulsesHorizontal;
    return this;
  }

  public VacuumGripperOutputsBuilder setEncoderPulsesVertical(EncoderPulses encoderPulsesVertical) {
    this.encoderPulsesVertical = encoderPulsesVertical;
    return this;
  }

  public VacuumGripperOutputsBuilder setPositionRotate(float positionRotate) {
    this.positionRotate = positionRotate;
    return this;
  }

  public VacuumGripperOutputsBuilder setPositionHorizontal(float positionHorizontal) {
    this.positionHorizontal = positionHorizontal;
    return this;
  }

  public VacuumGripperOutputsBuilder setPositionVertical(float positionVertical) {
    this.positionVertical = positionVertical;
    return this;
  }

  public VacuumGripperOutputs build() {
    return new VacuumGripperOutputs(encoderPulsesRotate, encoderPulsesHorizontal, encoderPulsesVertical, positionRotate, positionHorizontal, positionVertical);
  }
}