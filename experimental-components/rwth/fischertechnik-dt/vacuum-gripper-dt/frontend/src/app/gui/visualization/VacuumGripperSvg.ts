import { VacuumGripperSvgComponent, config } from '@src/gui/visualization/VacuumGripperSvgComponent';

import { AfterViewInit, ViewChild } from '@angular/core';
import {
  Directive,
  Component,
  Input,
  Output,
  EventEmitter,
  TemplateRef,
} from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';
import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";

// TODO: do i need to override this?

@Component({
standalone: true,
imports: [
... config.imports
],
selector: config.selector,
templateUrl: config.templateUrl,
styles: config.styles,
styleUrls: config.styleUrls
})
export  class VacuumGripperSvg extends VacuumGripperSvgComponent  {

  public constructor(route: ActivatedRoute, router: Router) {
    super(route, router);
  }

}


