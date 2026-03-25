

import { SVG, Svg, Defs, registerWindow, G } from '@svgdotjs/svg.js'
import { RectShape } from '../../RectShape.js';
import { IComponentGenerator } from '../IComponentGenerator.js';

export class TableSvgGenerator implements IComponentGenerator {

  tableBase = new RectShape(260, 186, 0, 0);

  id_postfix = '';

  constructor(id_postfix: string, width: number, length: number) {
    this.id_postfix = id_postfix;
    this.tableBase.xSize = length;
    this.tableBase.ySize = width;
  }

  generateAll(machineGroup: G) {
    this.generateAllStatic(machineGroup);
  }

  getWidth(): number {
    return this.tableBase.xSize;
  }
  getLength(): number {
    return this.tableBase.ySize;
  }

  generateAllStatic(machineGroup: G) {
    this.generateWoodBase(machineGroup);
  }

  generateWoodBase(machineGroup: G) {
    const tableBaseGroup = machineGroup.group();
    tableBaseGroup.id('tableBase' + this.id_postfix)
    tableBaseGroup.rect(this.tableBase.xSize, this.tableBase.ySize).addClass('tableBase');
    //.fill('burlywood').stroke('black');
    var refPoint = tableBaseGroup.rect(5, 5).fill('red');
    refPoint.id('referencePoint' + this.id_postfix);
  }

}