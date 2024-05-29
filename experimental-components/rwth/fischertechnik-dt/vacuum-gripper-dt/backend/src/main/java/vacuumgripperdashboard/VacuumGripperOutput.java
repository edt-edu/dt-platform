package vacuumgripperdashboard;

public class VacuumGripperOutput extends VacuumGripperOutputTOP {

  @Override
  public boolean isInLoadingZone() {
    Float v = this.getPositionVertical().getContent();
    Float h = this.getPositionHorizontal().getContent();
    Float r = this.getPositionRotate().getContent();
    return positionCloseTo(40, v, 5)
           && positionCloseTo(100, h, 5)
           && rotationCloseTo(0, r, 5);
  }

  @Override
  public boolean isInDropoffZone() {
    Float v = this.getPositionVertical().getContent();
    Float h = this.getPositionHorizontal().getContent();
    Float r = this.getPositionRotate().getContent();
    return positionCloseTo(30, v, 5)
        && positionCloseTo(10, h, 5)
        && rotationCloseTo(90, r, 5);
  }

  public static boolean positionCloseTo(float goal, float cur, float diff){
    return Math.abs(goal - cur) <= diff;
  }

  public static boolean rotationCloseTo(float goal, float cur, float diff){
    float a = goal - cur;
    a = (a + 180f) % 360f - 180f;
    a = Math.abs(a);

    return a <= diff;
  }
}