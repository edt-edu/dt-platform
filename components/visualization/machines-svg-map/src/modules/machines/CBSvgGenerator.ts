

import { SVG, Svg, Defs, registerWindow, G } from '@svgdotjs/svg.js'
import { RectShape } from '../RectShape.js';
import { IMachineGenerator } from './IMachineGenerator.js';

export class CBSvgGenerator implements IMachineGenerator{

  woodBase = new RectShape(260,186,0,0);
  plasticBase = new RectShape(257, 186, 0,0);
  conveyor = new RectShape(this.woodBase.xSize+10, 50, 0, 20);


  electronicCard =  new RectShape(108,80, 75,100); // to be verified
  
  sensor1 = new RectShape(20, 100, 30, -10);
  sensor2 = new RectShape(20, 100, 220, -10);

  id_postfix = '';

  constructor(id_postfix: string) {
    this.id_postfix = id_postfix;
  }

  generateAll(machineGroup: G) {
    this.generateAllStatic(machineGroup);
  }

  generateAllStatic(machineGroup: G) {
    this.generateWoodBase(machineGroup);
    this.generatePlasticBase(machineGroup);
    this.generateElectronicCard(machineGroup);
    this.generateConveyor(machineGroup);
    this.generateSensorZones(machineGroup);
  }

  generateConveyor(machineGroup: G) {
    // arm accessible zone
    var zoneGroup = machineGroup.group();
    zoneGroup.id("conveyorZone"+this.id_postfix)
    const mainShape = zoneGroup.rect(this.conveyor.xSize, this.conveyor.ySize ).addClass('conveyorZone');
    ///*.fill('lightblue').attr({ 'fill-opacity': 0.5 })*/;
    mainShape.move(this.conveyor.xPos, this.conveyor.yPos);
    
  }
  generateSensorZones(machineGroup: G) {
    const crossSize = 5;
    const sensorsGroup = machineGroup.group();
    sensorsGroup.rect(this.sensor1.xSize, this.sensor1.ySize)
      .move(this.sensor1.xPos, this.sensor1.yPos)
      .addClass("lightBarrier");
    sensorsGroup.rect(this.sensor2.xSize, this.sensor2.ySize)
      .move(this.sensor2.xPos, this.sensor2.yPos)
      .addClass("lightBarrier")
  }

  generateWoodBase(machineGroup: G) {
    const woodBaseGroup = machineGroup.group();
    woodBaseGroup.id('woodBase'+this.id_postfix)
    woodBaseGroup.rect(this.woodBase.xSize, this.woodBase.ySize).addClass('woodBase');
    //.fill('burlywood').stroke('black');
    var refPoint = woodBaseGroup.rect(5, 5).fill('red');
    refPoint.id('referencePoint'+this.id_postfix);
  }

  generatePlasticBase(machineGroup: G) {
    const plasticBaseGroup = machineGroup.group();
    plasticBaseGroup.id('plasticBase'+this.id_postfix)
    plasticBaseGroup.rect(this.plasticBase.xSize, this.plasticBase.ySize)
      .move(this.plasticBase.xPos, this.plasticBase.yPos).addClass('plasticBase');
    //.fill('burlywood').stroke('black');
  }

  generateElectronicCard(machineGroup: G) {
    const group = machineGroup.group();
    group.id('electronicCard'+this.id_postfix)
    group.rect(this.electronicCard.xSize, this.electronicCard.ySize)
      .move(this.electronicCard.xPos, this.electronicCard.yPos).addClass('electronicCard');
    //.fill('burlywood').stroke('black');
  }
}