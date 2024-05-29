package vacuum_gripper;

public class LinearAxisState extends LinearAxisStateTOP {
  public LinearAxisState(float min, float max) {
    super(min, max);
  }

  public void setInternalPosition(float f){
    this.internalPosition = f;
  }
}
