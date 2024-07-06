

import { SVG, Svg, Defs, registerWindow, G } from '@svgdotjs/svg.js'
import { RectShape } from '../RectShape.js';


/**
 * SVG generator for the Sorting Line with Color
 */
export class SlcSvgGenerator {
 
  woodBase = new RectShape(440, 310, 0, 0);
  plasticBase = new RectShape(380, 258, 0, 12 );
  conveyor =  new RectShape(400, 30, -10, 105 );
  sensorIn1 =  new RectShape(15, 72, 10, 85 );
  sensorIn2 =  new RectShape(15, 72, 195, 85 );
  electronicCard =  new RectShape(126,86, 30,180);
  sensorOut1 =  new RectShape(68, 15, 193, 200 );
  sensorOut2 =  new RectShape(68, 15, 252, 185 );
  sensorOut3 =  new RectShape(68, 15, 315, 200 );

  colorSensor =  new RectShape( 110, 60, 48, 90 ); // need t be verified

  slider1 =  new RectShape(30,86, 210,135);
  slider2 =  new RectShape(30,86, 270,135);
  slider3 =  new RectShape(30,86, 333,135);

  ejector1 =  new RectShape(15,93, 220,12);
  ejector2 =  new RectShape(15,93, 280,12);
  ejector3 =  new RectShape(15,93, 338,12);


  id_postfix = '';

  constructor(id_postfix: string) {
    this.id_postfix = id_postfix;
  }

  generateAll(machineGroup: G) {
    this.generateWoodBase(machineGroup);
    this.generatePlasticBase(machineGroup);
    this.generateConveyor(machineGroup);
    this.generateSliderZones(machineGroup);
    this.generateElectronicCard(machineGroup);
    this.generateEjectors(machineGroup);
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
    sensorsGroup.rect(this.sensorIn1.xSize, this.sensorIn1.ySize)
      .move(this.sensorIn1.xPos, this.sensorIn1.yPos)
      .addClass("lightBarrier");
    sensorsGroup.rect(this.sensorIn2.xSize, this.sensorIn2.ySize)
      .move(this.sensorIn2.xPos, this.sensorIn2.yPos)
      .addClass("lightBarrier");
    sensorsGroup.rect(this.sensorOut1.xSize, this.sensorOut1.ySize)
      .move(this.sensorOut1.xPos, this.sensorOut1.yPos)
      .addClass("lightBarrier");
    sensorsGroup.rect(this.sensorOut2.xSize, this.sensorOut2.ySize)
      .move(this.sensorOut2.xPos, this.sensorOut2.yPos)
      .addClass("lightBarrier");
    sensorsGroup.rect(this.sensorOut3.xSize, this.sensorOut3.ySize)
      .move(this.sensorOut3.xPos, this.sensorOut3.yPos)
      .addClass("lightBarrier");
    
    sensorsGroup.rect(this.colorSensor.xSize, this.colorSensor.ySize)
      .move(this.colorSensor.xPos, this.colorSensor.yPos)
      .addClass("colorLightBarrier");
    // sensorsGroup.rect(this.sensorXSize, this.sensorYSize)
    //   .move(this.sensor2XPos, this.sensor2YPos)
    //   .addClass("lightBarrier")
    // TODO
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

  generateEjectors(machineGroup: G) {
    const group = machineGroup.group();
    group.id('slider'+this.id_postfix)
    group.rect(this.ejector1.xSize, this.ejector1.ySize)
      .move(this.ejector1.xPos, this.ejector1.yPos).addClass('ejector');
    group.rect(this.ejector2.xSize, this.ejector2.ySize)
      .move(this.ejector2.xPos, this.ejector2.yPos).addClass('ejector');
      group.rect(this.ejector3.xSize, this.ejector3.ySize)
        .move(this.ejector3.xPos, this.ejector3.yPos).addClass('ejector');
    //.fill('burlywood').stroke('black');
  }
  generateSliderZones(machineGroup: G) {
    const group = machineGroup.group();
    group.id('slider'+this.id_postfix)
    group.rect(this.slider1.xSize, this.slider1.ySize)
      .move(this.slider1.xPos, this.slider1.yPos).addClass('slider');
    group.rect(this.slider2.xSize, this.slider2.ySize)
      .move(this.slider2.xPos, this.slider2.yPos).addClass('slider');
      group.rect(this.slider3.xSize, this.slider3.ySize)
        .move(this.slider3.xPos, this.slider3.yPos).addClass('slider');
    //.fill('burlywood').stroke('black');
  }
}