package vacuum_gripper;

public class VacuumGripperInputsBuilder {
  private boolean verticalUp = false;
  private boolean verticalDown = false;
  private boolean horizontalForward = false;
  private boolean horizontalBack = false;
  private boolean rotationClockwise = false;
  private boolean rotationCounterclockwise = false;

  public VacuumGripperInputsBuilder setVerticalUp(boolean verticalUp) {
    this.verticalUp = verticalUp;
    return this;
  }

  public VacuumGripperInputsBuilder setVerticalDown(boolean verticalDown) {
    this.verticalDown = verticalDown;
    return this;
  }

  public VacuumGripperInputsBuilder setHorizontalForward(boolean horizontalForward) {
    this.horizontalForward = horizontalForward;
    return this;
  }

  public VacuumGripperInputsBuilder setHorizontalBack(boolean horizontalBack) {
    this.horizontalBack = horizontalBack;
    return this;
  }

  public VacuumGripperInputsBuilder setRotationClockwise(boolean rotationClockwise) {
    this.rotationClockwise = rotationClockwise;
    return this;
  }

  public VacuumGripperInputsBuilder setRotationCounterclockwise(boolean rotationCounterclockwise) {
    this.rotationCounterclockwise = rotationCounterclockwise;
    return this;
  }

  public VacuumGripperInputs build() {
    return new VacuumGripperInputs(verticalUp, verticalDown, horizontalForward, horizontalBack, rotationClockwise, rotationCounterclockwise);
  }
}