import {FunctionOverviewComponent,config} from '@src/gui/FunctionOverviewComponent';
import {Component} from '@angular/core';
import {CommandManager} from '@umlp/common';
import {ActivatedRoute, Router} from '@angular/router';
import { VacuumGripperDashboardManager } from '@src/vacuumgripperdashboard/VacuumGripperDashboardManager';

import { Port } from '@src/vacuumgripperdashboard/Port';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class FunctionOverview extends FunctionOverviewComponent {

 public  getInPortNavItemTarget (p: Port): string {
  return "/gui/visualization/FunctionStreamOverview/" + p.getChannel()?.getFunctionStream()?.getGemId();
 }

  public  getOutPortNavItemTarget (p: Port): string {
   return "/gui/visualization/FunctionStreamOverview/" + p.getChannel()?.getFunctionStream()?.getGemId();
  }
}