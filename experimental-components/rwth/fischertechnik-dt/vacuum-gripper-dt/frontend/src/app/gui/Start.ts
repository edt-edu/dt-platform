import {StartComponent,config} from '@src/gui/StartComponent';
import {Component} from '@angular/core';
import {CommandManager} from '@umlp/common';
import {ActivatedRoute, Router} from '@angular/router';
import { VacuumGripperDashboardManager } from '@src/vacuumgripperdashboard/VacuumGripperDashboardManager';

import { FFunction } from '@src/vacuumgripperdashboard/FFunction';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class Start extends StartComponent {

 public  getFuncNavItemTarget (f: FFunction): string {
  return "/gui/FunctionOverview/" + f.getGemId();
 }

}