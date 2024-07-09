

import { SVG, Svg, Defs, registerWindow, G } from '@svgdotjs/svg.js'
import { REFUSED } from 'dns';
import { RectShape } from '../RectShape.js';
import { ShapeHelper } from '../ShapeHelper.js';

export class VGRSvgGenerator {

  woodBase = new RectShape (257,186, 0,0);
  electronicCard =  new RectShape(86,126, 0,60); // to be verified

  axisXPos = 214;
  axisYPos = 105;
  axisMaxRadius = 315; // from axis to vacuum  center
  axisMinRadius = 165

  id_postfix = '';

  constructor(id_postfix: string) {
    this.id_postfix = id_postfix;
  }

  generateAll(machineGroup: G) {
    this.generateWoodBase(machineGroup);
    this.generatePlasticBase(machineGroup);
    this.generateElectronicCard(machineGroup);
    this.generateAxis(machineGroup);
    this.generateArmAccessibleZone(machineGroup);
  }

  generateArmAccessibleZone(machineGroup: G) {
    // arm accessible zone
    var armAccessZoneGroup = machineGroup.group();
    armAccessZoneGroup.id("vgrArmAccessZone"+this.id_postfix)
    const mainShape = armAccessZoneGroup.circle(this.axisMaxRadius * 2).addClass('accessZone');
    ///*.fill('lightblue').attr({ 'fill-opacity': 0.5 })*/;
    mainShape.move(-(this.axisMaxRadius) + this.axisXPos, -(this.axisMaxRadius) + this.axisYPos);
    const mask = armAccessZoneGroup.mask();
    // This white rectangle allows the entire area to be visible through the mask by default
    mask.rect(this.axisMaxRadius * 2, this.axisMaxRadius * 5).fill('white').move(-(this.axisMaxRadius) + this.axisXPos, -(this.axisMaxRadius) + this.axisYPos);
    // part in black will be masked
    mask.circle(this.axisMinRadius * 2).fill('black').move(-(this.axisMinRadius) + this.axisXPos, -(this.axisMinRadius) + this.axisYPos); // Adjust position and size
    mask.polygon([[0, 0], [this.axisMaxRadius, 0], [this.axisMaxRadius, this.axisMaxRadius]]).fill('black').move( this.axisXPos, this.axisYPos);
    mainShape.maskWith(mask);
  }
  generateAxis(machineGroup: G) {
    const crossSize = 5;
    const axisGroup = machineGroup.group();
    axisGroup.id('vgrRotationAxis'+this.id_postfix);
    const helper = new ShapeHelper();
    helper.generateAxisCross(axisGroup, this.axisMinRadius, this.axisYPos,5 );
  }

  generateWoodBase(machineGroup: G) {
    const woodBaseGroup = machineGroup.group();
    woodBaseGroup.id('vgrWoodBase'+this.id_postfix)
    woodBaseGroup.rect(this.woodBase.xSize, this.woodBase.ySize).addClass('woodBase');
    //.fill('burlywood').stroke('black');
    var refPoint = woodBaseGroup.rect(5, 5).fill('red');
    refPoint.id('vgrReferencePoint'+this.id_postfix);
  }

  generatePlasticBase(machineGroup: G) {
    const plasticBaseGroup = machineGroup.group();
    plasticBaseGroup.id('vgrPlasticBase'+this.id_postfix)
    plasticBaseGroup.rect(this.woodBase.xSize, this.woodBase.ySize).addClass('plasticBase');
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