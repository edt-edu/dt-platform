

import { SVG, Svg, Defs, registerWindow, G } from '@svgdotjs/svg.js'
import { RectShape } from '../RectShape.js';


/**
 * SVG generator for the High Bay Ware House
 */
export class HBWSvgGenerator {
 
  woodBase = new RectShape(690, 440, 0, 0);
  plasticBase = new RectShape(576, 258, 57, 84 );
  conveyor =  new RectShape(31, 132, 144, 215 );
  sensorIn1 =  new RectShape(72, 15, 125, 325 ); 
  sensorIn2 =  new RectShape(72, 15, 125, 222 ); 
  electronicCard =  new RectShape(86,126, 546,215);
  armAccessiblezone =  new RectShape(340,70, 144,175);

  storage1 =  new RectShape(30,30, 262,218);
  storage2 =  new RectShape(30,30, 352,218);
  storage3 =  new RectShape(30,30, 442,218);


  id_postfix = '';

  constructor(id_postfix: string) {
    this.id_postfix = id_postfix;
  }

  generateAll(machineGroup: G) {
    this.generateWoodBase(machineGroup);
    this.generatePlasticBase(machineGroup);
    this.generateConveyor(machineGroup);
    this.generateElectronicCard(machineGroup);
    this.generateStorageZones(machineGroup);
    this.generateSensorZones(machineGroup);
    this.generateArmAccessibleZone(machineGroup);
  }

  generateConveyor(machineGroup: G) {
    // arm accessible zone
    var zoneGroup = machineGroup.group();
    zoneGroup.id("conveyorZone"+this.id_postfix)
    const mainShape = zoneGroup.rect(this.conveyor.xSize, this.conveyor.ySize ).addClass('conveyorZone');
    ///*.fill('lightblue').attr({ 'fill-opacity': 0.5 })*/;
    mainShape.move(this.conveyor.xPos, this.conveyor.yPos);
    
  }
  generateArmAccessibleZone(machineGroup: G) {
    // arm accessible zone
    var group = machineGroup.group();
    group.id("ArmArmAccessZone"+this.id_postfix)    
    group.rect(this.armAccessiblezone.xSize, this.armAccessiblezone.ySize)
      .move(this.armAccessiblezone.xPos, this.armAccessiblezone.yPos).addClass('accessZone');
  }

  generateSensorZones(machineGroup: G) {
    const crossSize = 5;
    const sensorsGroup = machineGroup.group();
    sensorsGroup.rect(this.sensorIn1.xSize, this.sensorIn1.ySize)
      .move(this.sensorIn1.xPos, this.sensorIn1.yPos)
      .addClass("lightBarrier");
    sensorsGroup.rect(this.sensorIn2.xSize, this.sensorIn2.ySize)
      .move(this.sensorIn2.xPos, this.sensorIn2.yPos)
      .addClass("lightBarrier");
    
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

  generateStorageZones(machineGroup: G) {
    const group = machineGroup.group();
    group.id('storage'+this.id_postfix)
    group.rect(this.storage1.xSize, this.storage1.ySize)
      .move(this.storage1.xPos, this.storage1.yPos).addClass('storage');
    group.rect(this.storage2.xSize, this.storage2.ySize)
      .move(this.storage2.xPos, this.storage2.yPos).addClass('storage');
      group.rect(this.storage3.xSize, this.storage3.ySize)
        .move(this.storage3.xPos, this.storage3.yPos).addClass('storage');
    //.fill('burlywood').stroke('black');
  }
}