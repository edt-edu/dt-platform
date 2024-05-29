import {VacuumGripperOutputTOP} from '@src/vacuumgripperdashboard/VacuumGripperOutputTOP';

export class VacuumGripperOutput extends VacuumGripperOutputTOP {

  public override isInLoadingZone(): boolean {
    let v = this.getPositionVertical().getContent();
    let h = this.getPositionHorizontal().getContent();
    let r = this.getPositionRotate().getContent();
    return this.positionCloseTo(40, v, 5)
           && this.positionCloseTo(100, h, 5)
           && this.rotationCloseTo(0, r, 5);
  }

  public override isInDropoffZone(): boolean {
    let v = this.getPositionVertical().getContent();
    let h = this.getPositionHorizontal().getContent();
    let r = this.getPositionRotate().getContent();
    return this.positionCloseTo(30, v, 5)
        && this.positionCloseTo(10, h, 5)
        && this.rotationCloseTo(90, r, 5);
  }

  public positionCloseTo(goal: number, cur: number, diff: number): boolean {
    return Math.abs(goal - cur) <= diff;
  }

  public rotationCloseTo(goal: number, cur: number, diff: number): boolean {
    let a = goal - cur;
    a = (a + 180.0) % 360.0 - 180.0;
    a = Math.abs(a);

    return a <= diff;
  }
}