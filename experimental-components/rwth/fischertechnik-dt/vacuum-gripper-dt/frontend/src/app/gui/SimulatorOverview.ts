import {SimulatorOverviewComponent,config} from '@src/gui/SimulatorOverviewComponent';
import {Component} from '@angular/core';
import {CommandManager} from '@umlp/common';
import {ActivatedRoute, Router} from '@angular/router';
import { VacuumGripperDashboardManager } from '@src/vacuumgripperdashboard/VacuumGripperDashboardManager';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class SimulatorOverview extends SimulatorOverviewComponent {
  simulatorRunning = false;

  public constructor(route: ActivatedRoute, protected router: Router) {
    super(route, router);
    this.router = router;
    this.initAutoSimulator();
  }

  public simulatorStepBtnLeftClick(): void {
    VacuumGripperDashboardManager.simulatorStepBuilder().then(step => step.build());
  }

  public simulatorStopBtnLeftClick(): void {
    this.simulatorRunning = false;
  }

  public simulatorStartBtnLeftClick(): void {
    this.simulatorRunning = true;
  }

  public payloadUnknownBtnLeftClick(): void {
    this.getApp().setPayloadPosition(3);
  }

  public payloadLoadingZoneBtnLeftClick(): void {
    this.getApp().setPayloadPosition(0);
  }

  public payloadInTransitBtnLeftClick(): void {
    this.getApp().setPayloadPosition(1);
  }

  public payloadDropoffZoneBtnLeftClick(): void {
    this.getApp().setPayloadPosition(2);
  }

  public initAutoSimulator(){
    setTimeout(() => {
      if(this.simulatorRunning){
        this.simulatorStepBtnLeftClick();
      }
      this.initAutoSimulator();
    }, 1000);
  }

}