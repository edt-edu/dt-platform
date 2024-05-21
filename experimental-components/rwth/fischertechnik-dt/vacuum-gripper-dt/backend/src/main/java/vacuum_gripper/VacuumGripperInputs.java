package vacuum_gripper;

public class VacuumGripperInputs {
  final boolean verticalUp;
  final boolean verticalDown;
  final boolean horizontalForward;
  final boolean horizontalBack;
  final boolean rotationClockwise;
  final boolean rotationCounterclockwise;

  public VacuumGripperInputs(boolean verticalUp, boolean verticalDown, boolean horizontalForward, boolean horizontalBack, boolean rotationClockwise, boolean rotationCounterclockwise) {
    this.verticalUp = verticalUp;
    this.verticalDown = verticalDown;
    this.horizontalForward = horizontalForward;
    this.horizontalBack = horizontalBack;
    this.rotationClockwise = rotationClockwise;
    this.rotationCounterclockwise = rotationCounterclockwise;
  }

  public void applyTo(VacuumGripper vacuumGripper){
    vacuumGripper.getVerticalUp().update(this.verticalUp);
    vacuumGripper.getVerticalDown().update(this.verticalDown);
    vacuumGripper.getHorizontalForward().update(this.horizontalForward);
    vacuumGripper.getHorizontalBack().update(this.horizontalBack);
    vacuumGripper.getRotationClockwise().update(this.rotationClockwise);
    vacuumGripper.getRotationCounterclockwise().update(this.rotationCounterclockwise);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    VacuumGripperInputs that = (VacuumGripperInputs) o;

    if (verticalUp != that.verticalUp) return false;
    if (verticalDown != that.verticalDown) return false;
    if (horizontalForward != that.horizontalForward) return false;
    if (horizontalBack != that.horizontalBack) return false;
    if (rotationClockwise != that.rotationClockwise) return false;
    return rotationCounterclockwise == that.rotationCounterclockwise;
  }

  @Override
  public int hashCode() {
    int result = (verticalUp ? 1 : 0);
    result = 31 * result + (verticalDown ? 1 : 0);
    result = 31 * result + (horizontalForward ? 1 : 0);
    result = 31 * result + (horizontalBack ? 1 : 0);
    result = 31 * result + (rotationClockwise ? 1 : 0);
    result = 31 * result + (rotationCounterclockwise ? 1 : 0);
    return result;
  }
}
