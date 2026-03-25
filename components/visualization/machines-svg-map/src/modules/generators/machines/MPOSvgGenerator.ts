

import { SVG, Svg, Defs, registerWindow, G } from '@svgdotjs/svg.js'
import { RectShape } from '../../RectShape.js';
import { ShapeHelper } from '../../ShapeHelper.js';
import { IComponentGenerator } from '../IComponentGenerator.js';

/**
 * SVG generator for the Multi Processing station with Oven
 */
export class MPOSvgGenerator implements IComponentGenerator{
 
  woodBase = new RectShape(438, 310, 0, 0);
  plasticBase = new RectShape(410, 258, 25, 25 );
  conveyor =  new RectShape(31, 132, 46, 20 );
  sensorIn =  new RectShape(120, 15, 300, 58 ); 
  sensorOut =  new RectShape(96, 15, 10, 42 ); 
  electronicCard =  new RectShape(126,86, 160,196);
  armAccessiblezone =  new RectShape(260,15, 156, 60);

  slider1 =  new RectShape(60,30, 46,105);

  ovenDoor =  new RectShape(90,30, 322,86);

  ovenSlideAccessibleZone =  new RectShape(30,90, 348,50);
  ovenBox =  new RectShape(180,106, 252,86);

  turnTableAxisXPos = 174;
  turnTableAxisYPos = 120; 
  turnTableAxisMinRadius = 40;
  turnTableAxisMaxRadius = 64;

  sawAxisXPos = 156;
  sawAxisYPos = 180;

  id_postfix = '';

  constructor(id_postfix: string) {
    this.id_postfix = id_postfix;
  }
  
  getWidth(): number {
    return this.woodBase.xSize;
  }
  getLength(): number {
    return this.woodBase.ySize;
  }

  generateAll(machineGroup: G) {
      
    this.generateAllStatic(machineGroup);
  }

  generateAllStatic(machineGroup: G) {
    this.generateWoodBase(machineGroup);
    this.generatePlasticBase(machineGroup);
    this.generateConveyor(machineGroup);
    this.generateSliderZones(machineGroup);
    this.generateElectronicCard(machineGroup);
    this.generateOvenBox(machineGroup);
    this.generateSensorZones(machineGroup);
    this.generateArmAccessibleZone(machineGroup);
    this.generateTurntableAccessibleZone(machineGroup);
    this.generateTurntableAxis(machineGroup);
    this.generateSlideAccessibleZone(machineGroup);
    this.generateSaw(machineGroup);
  }

  generateConveyor(machineGroup: G) {
    // arm accessible zone
    var zoneGroup = machineGroup.group();
    zoneGroup.id("conveyorZone"+this.id_postfix)
    const mainShape = zoneGroup.rect(this.conveyor.xSize, this.conveyor.ySize ).addClass('conveyorZone');
    ///*.fill('lightblue').attr({ 'fill-opacity': 0.5 })*/;
    mainShape.move(this.conveyor.xPos, this.conveyor.yPos);
    
  }

  generateSliderZones(machineGroup: G) {
    const group = machineGroup.group();
    group.id('slider'+this.id_postfix)
    group.rect(this.slider1.xSize, this.slider1.ySize)
      .move(this.slider1.xPos, this.slider1.yPos).addClass('slider');
  }
  generateArmAccessibleZone(machineGroup: G) {
    // arm accessible zone
    var group = machineGroup.group();
    group.id("ArmArmAccessZone"+this.id_postfix)    
    group.rect(this.armAccessiblezone.xSize, this.armAccessiblezone.ySize)
      .move(this.armAccessiblezone.xPos, this.armAccessiblezone.yPos).addClass('accessZone');
  }
  generateTurntableAccessibleZone(machineGroup: G) {
    // accessible zone
    var armAccessZoneGroup = machineGroup.group();
    armAccessZoneGroup.id("turntableAccessZone"+this.id_postfix)
    const mainShape = armAccessZoneGroup.circle(this.turnTableAxisMaxRadius * 2).addClass('accessZone');
    mainShape.move(-(this.turnTableAxisMaxRadius) + this.turnTableAxisXPos, -(this.turnTableAxisMaxRadius) + this.turnTableAxisYPos);
    const mask = armAccessZoneGroup.mask();
    // This white rectangle allows the entire area to be visible through the mask by default
    mask.rect(this.turnTableAxisMaxRadius * 2, this.turnTableAxisMaxRadius * 5).fill('white').move(-(this.turnTableAxisMaxRadius) + this.turnTableAxisXPos, -(this.turnTableAxisMaxRadius) + this.turnTableAxisYPos);
    // part in black will be masked
    mask.circle(this.turnTableAxisMinRadius * 2).fill('black').move(-(this.turnTableAxisMinRadius) + this.turnTableAxisXPos, -(this.turnTableAxisMinRadius) + this.turnTableAxisYPos); // Adjust position and size
    mask.rect(this.turnTableAxisMaxRadius,this.turnTableAxisMaxRadius).move( this.turnTableAxisXPos-this.turnTableAxisMaxRadius, this.turnTableAxisYPos-this.turnTableAxisMaxRadius);
    //mask.polygon([[0, 0], [this.turnTableAxisMaxRadius, 0], [this.turnTableAxisMaxRadius, this.turnTableAxisMaxRadius]]).fill('black').move( this.turnTableAxisXPos, this.turnTableAxisYPos);
    mainShape.maskWith(mask);
  }

  generateTurntableAxis(machineGroup: G) {
    // accessible zone
    var group = machineGroup.group();
    group.id("turntableAxis"+this.id_postfix)
    const helper = new ShapeHelper();
    helper.generateAxisCross(group, this.turnTableAxisXPos, this.turnTableAxisYPos,5 );

  }
  generateSlideAccessibleZone(machineGroup: G) {
    // arm accessible zone
    var group = machineGroup.group();
    group.id("ArmArmAccessZone"+this.id_postfix)    
    group.rect(this.ovenSlideAccessibleZone.xSize, this.ovenSlideAccessibleZone.ySize)
      .move(this.ovenSlideAccessibleZone.xPos, this.ovenSlideAccessibleZone.yPos).addClass('accessZone');
  }

  generateSaw(machineGroup: G) {


    const helper = new ShapeHelper();
    const crossSize = 7;
    const group = machineGroup.group();
    group.id('saw'+this.id_postfix);
    group.polygon(helper.drawStar(this.sawAxisXPos, this.sawAxisYPos, 7, 15, 10)).stroke('black').fill('none');
  }

  generateSensorZones(machineGroup: G) {
    const crossSize = 5;
    const sensorsGroup = machineGroup.group();
    sensorsGroup.rect(this.sensorIn.xSize, this.sensorIn.ySize)
      .move(this.sensorIn.xPos, this.sensorIn.yPos)
      .addClass("lightBarrier");
    sensorsGroup.rect(this.sensorOut.xSize, this.sensorOut.ySize)
      .move(this.sensorOut.xPos, this.sensorOut.yPos)
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

  generateOvenBox(machineGroup: G) {
    const group = machineGroup.group();
    group.id('electronicCard'+this.id_postfix)
    group.rect(this.ovenBox.xSize, this.ovenBox.ySize)
      .move(this.ovenBox.xPos, this.ovenBox.yPos) //.addClass('ovenBox');
      .fill('None').stroke('black');
    group.rect(this.ovenDoor.xSize, this.ovenDoor.ySize)
      .move(this.ovenDoor.xPos, this.ovenDoor.yPos) //.addClass('ovenDoor');
      .fill('None').stroke('black');
  }


}