import { FunctionStreamOverviewComponent, config } from '@src/gui/visualization/FunctionStreamOverviewComponent';

import { Value } from '@src/vacuumgripperdashboard/Value';

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
export  class FunctionStreamOverview extends FunctionStreamOverviewComponent  {

  public constructor(route: ActivatedRoute, router: Router) {
    super(route, router);
  }

 public  getValueTextValue (v: Value): string {
  let tmp = (v as any);
  if("getContent" in tmp){
    return tmp.getContent().toString();
  }else{
    return tmp.isContent().toString();
  }
 }
}
