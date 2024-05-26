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
  public constructor(route: ActivatedRoute, protected router: Router) {
    super(route, router);
    this.router = router;
  }

  public simulatorButtonLeftClick(): void {
    VacuumGripperDashboardManager.simulatorStepBuilder().then(step => step.build());
  }

   /*public getVerticalUpInputEntry(){
    return this.getApp().getSimulationInput().getVerticalUp().isContent().toString();
   }

   public getVerticalDownInputEntry(){
    return this.getApp().getSimulationInput().getVerticalDown().isContent().toString();
   }

   public getHorizontalForwardInputEntry(){
    return this.getApp().getSimulationInput().getHorizontalForward().isContent().toString();
   }

   public getHorizontalBackInputEntry(){
    return this.getApp().getSimulationInput().getHorizontalBack().isContent().toString();
   }

   public getRotationClockwiseInputEntry(){
    return this.getApp().getSimulationInput().getRotationClockwise().isContent().toString();
   }

   public getRotationCounterclockwiseInputEntry(){
    return this.getApp().getSimulationInput().getRotationCounterclockwise().isContent().toString();
   }

   public setVerticalUpInputEntry(s: string){
    this.getApp().getSimulationInput().getVerticalUp().setContent(s === "true");
   }

   public setVerticalDownInputEntry(s: string){
    this.getApp().getSimulationInput().getVerticalDown().setContent(s === "true");
   }

   public setHorizontalForwardInputEntry(s: string){
    this.getApp().getSimulationInput().getHorizontalForward().setContent(s === "true");
   }

   public setHorizontalBackInputEntry(s: string){
    this.getApp().getSimulationInput().getHorizontalBack().setContent(s === "true");
   }

   public setRotationClockwiseInputEntry(s: string){
    this.getApp().getSimulationInput().getRotationClockwise().setContent(s === "true");
   }

   public setRotationCounterclockwiseInputEntry(s: string){
    this.getApp().getSimulationInput().getRotationCounterclockwise().setContent(s === "true");
   }*/
}