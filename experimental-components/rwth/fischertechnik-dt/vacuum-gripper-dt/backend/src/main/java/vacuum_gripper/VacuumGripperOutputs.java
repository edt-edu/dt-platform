package vacuum_gripper;

import vacuum_gripper.VacuumGripperTypes.EncoderPulses;

import java.util.Objects;

public class VacuumGripperOutputs {
  final EncoderPulses encoderPulsesRotate;
  final EncoderPulses encoderPulsesHorizontal;
  final EncoderPulses encoderPulsesVertical;

  final float positionRotate;
  final float positionHorizontal;
  final float positionVertical;

  public VacuumGripperOutputs(EncoderPulses encoderPulsesRotate, EncoderPulses encoderPulsesHorizontal, EncoderPulses encoderPulsesVertical, float positionRotate, float positionHorizontal, float positionVertical) {
    this.encoderPulsesRotate = encoderPulsesRotate;
    this.encoderPulsesHorizontal = encoderPulsesHorizontal;
    this.encoderPulsesVertical = encoderPulsesVertical;
    this.positionRotate = positionRotate;
    this.positionHorizontal = positionHorizontal;
    this.positionVertical = positionVertical;
  }

  public static VacuumGripperOutputs from(VacuumGripper vacuumGripper){
    return new VacuumGripperOutputsBuilder()
        .setEncoderPulsesRotate(vacuumGripper.getEncoderPulsesRotate().getValue())
        .setEncoderPulsesHorizontal(vacuumGripper.getEncoderPulsesHorizontal().getValue())
        .setEncoderPulsesVertical(vacuumGripper.getEncoderPulsesVertical().getValue())
        .setPositionRotate(vacuumGripper.getPositionRotate().getValue())
        .setPositionHorizontal(vacuumGripper.getPositionHorizontal().getValue())
        .setPositionVertical(vacuumGripper.getPositionVertical().getValue())
        .build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    VacuumGripperOutputs that = (VacuumGripperOutputs) o;

    if (Float.compare(positionRotate, that.positionRotate) != 0) return false;
    if (Float.compare(positionHorizontal, that.positionHorizontal) != 0) return false;
    if (Float.compare(positionVertical, that.positionVertical) != 0) return false;
    if (!Objects.equals(encoderPulsesRotate, that.encoderPulsesRotate))
      return false;
    if (!Objects.equals(encoderPulsesHorizontal, that.encoderPulsesHorizontal))
      return false;
    return Objects.equals(encoderPulsesVertical, that.encoderPulsesVertical);
  }

  @Override
  public int hashCode() {
    int result = encoderPulsesRotate != null ? encoderPulsesRotate.hashCode() : 0;
    result = 31 * result + (encoderPulsesHorizontal != null ? encoderPulsesHorizontal.hashCode() : 0);
    result = 31 * result + (encoderPulsesVertical != null ? encoderPulsesVertical.hashCode() : 0);
    result = 31 * result + (positionRotate != 0.0f ? Float.floatToIntBits(positionRotate) : 0);
    result = 31 * result + (positionHorizontal != 0.0f ? Float.floatToIntBits(positionHorizontal) : 0);
    result = 31 * result + (positionVertical != 0.0f ? Float.floatToIntBits(positionVertical) : 0);
    return result;
  }

  @Override
  public String toString() {
    return "VacuumGripperOutputs{" +
        "encoderPulsesRotate=" + encoderPulsesRotate +
        ", encoderPulsesHorizontal=" + encoderPulsesHorizontal +
        ", encoderPulsesVertical=" + encoderPulsesVertical +
        ", positionRotate=" + positionRotate +
        ", positionHorizontal=" + positionHorizontal +
        ", positionVertical=" + positionVertical +
        '}';
  }
}
