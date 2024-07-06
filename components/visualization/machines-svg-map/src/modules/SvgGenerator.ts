
// list for colors https://www.w3.org/TR/css-color-3/#svg-color

import { createSVGWindow } from 'svgdom'
import { SVG, Svg, Defs, registerWindow, G } from '@svgdotjs/svg.js'

import { readFileSync, writeFileSync } from 'fs';

import { VgrSvgGenerator } from './machines/VGRSvgGenerator.js';
import { CbSvgGenerator } from './machines/CBSvgGenerator.js';
import { SlcSvgGenerator } from './machines/SLCSvgGenerator.js';
import { HBWSvgGenerator } from './machines/HBWSvgGenerator.js';
import { MPOSvgGenerator } from './machines/MPOSvgGenerator.js';


export class SvgGenerator {

  svg!: Svg;
  defs!: Defs;

  canvasXSize! : number;
  canvasYSize! : number;

  constructor() {
    const window = createSVGWindow()
    const document = window.document
    
    // register window and document
    registerWindow(window, document)

  }

 

  createSVGCanvas( xSize : number, ySize : number ) {
    this.svg  = SVG().size(xSize, ySize).viewbox(0, 0, xSize, ySize); 
    const style = this.svg.style();
    style.rule(".accessZone", {
      fill: 'lightblue', 
      'fill-opacity': 0.5}
    );
    style.rule(".woodBase", {
      fill: 'burlywood',
      'stroke': 'black'
    });
   style.rule(".plasticBase", {
      fill: 'darkgray', 
      'fill-opacity': 0.5,
      'stroke': 'black'
    })
    style.rule(".conveyorZone", {
       fill: 'dimgray'
     })

    style.rule(".lightBarrier", {
      fill: 'yellow', 
      'fill-opacity': 0.5,
      'stroke': 'black'
    })
    style.rule(".colorLightBarrier", {
      fill: 'red', 
      'fill-opacity': 0.3,
      'stroke': 'black'
    })
    
    style.rule(".electronicCard", {
      fill: 'green', 
      'stroke': 'black'
    })
    style.rule(".slider", { // todo some color change or arrow for direction of the slider ?
      fill: 'dimgray', 
      'stroke': 'black'
    })
    style.rule(".ejector", { // todo some color change or arrow for direction of the slider ?
      fill: 'lightgray', 
      'stroke': 'black'
    })

    style.rule(".storage", { 
      fill: 'lightgray', 
      'stroke': 'black'
    })
    this.defs = this.svg.defs();   
  }

  /**
   * Create a Rulers on the side of the canvas, with ticks mark every 100
   * @param xSize 
   * @param ySize 
   */
  createSVGRulers( xSize : number, ySize : number ) {
    const rulersGroup = this.svg.group();
    rulersGroup.id('rulers')
    rulersGroup.line(0, 0, xSize, 0).stroke({ width: 1, color: 'black' });
    const xNbTicks = xSize / 100;
    for (let index = 0; index < xNbTicks; index++) {
      rulersGroup.line(index*100, 0, index*100, 10).stroke({ width: 1, color: 'black' });
    }
    rulersGroup.line(0, 0, 0, ySize).stroke({ width: 1, color: 'black' });
    const yNbTicks = ySize / 100;
    for (let index = 0; index < yNbTicks; index++) {
      rulersGroup.line(0, index*100, 10, index*100).stroke({ width: 1, color: 'black' });
    }
  }

  /** tool function allowing to pretty print the svg xml */
  formatXml(xml: string) { 
    var formatted = '', indent= '';
    const tab = '\t';
    xml.split(/>\s*</).forEach(function(node) {
        if (node.match( /^\/\w/ )) indent = indent.substring(tab.length); // decrease indent by one 'tab'
        formatted += indent + '<' + node + '>\r\n';
        if (node.match( /^<?\w([^>/]*|[^>]*[^/])$/ )) indent += tab;      // increase indent
    });
    return formatted.substring(1, formatted.length-3);
  }

  generate(fileName: string) {

    // TODO load from configuration file 

    this.canvasXSize = 1500
    this.canvasYSize = 2000

    this.createSVGCanvas(this.canvasXSize, this.canvasYSize);

    // add rulers
    this.createSVGRulers(this.canvasXSize, this.canvasYSize);


    // add conveyor belts
    const cb1Group = this.svg.group();
    const cb1Gen = new CbSvgGenerator('_cb1');
    cb1Gen.generateAll(cb1Group);
    cb1Group.rotate(180, 0, 0).translate(730,1010);

    // add sorting line

    const slc1Group = this.svg.group();
    const slc1Gen= new SlcSvgGenerator('_slc1');
    slc1Gen.generateAll(slc1Group);
    slc1Group.rotate(270, 0, 0).translate(150,1350);

    // add high bay warehouse

    const hbw1Group = this.svg.group();
    const hbw1Gen= new HBWSvgGenerator('_hbw1');
    hbw1Gen.generateAll(hbw1Group);
    hbw1Group.translate(460,150);


    // add multi processing station with oven
    const mpo1Group = this.svg.group();
    const mpo1Gen= new MPOSvgGenerator('_mpo1');
    mpo1Gen.generateAll(mpo1Group);
    mpo1Group.rotate(90, 0, 0).translate(460,470);


    // add Vacuum Grippers
    const vgrGenerator = new VgrSvgGenerator('_vgr1');
    
    const vgr1Group = this.svg.group();
    // vgrGenerator.generateWoodBase(vgr1Group);
    // vgrGenerator.generatePlasticBase(vgr1Group);
    // vgrGenerator.generateAxis(vgr1Group);
    // vgrGenerator.generateArmAccessibleZone(vgr1Group);
    vgrGenerator.generateAll(vgr1Group);
    vgr1Group.rotate(180, 0, 0).translate(750,810);//.translate(450,610);

    const vgr2Group = this.svg.group();
    const vgr2Generator = new VgrSvgGenerator('_vgr2');
    vgr2Generator.generateAll(vgr2Group);
    vgr2Group.rotate(90, 0, 0).translate(660,1020);

  }
 
  writeToFile(fileName: string) {

    console.log('writing '+fileName);

    const svgString = this.svg.svg();
    writeFileSync(fileName, this.formatXml(svgString));
  }
  
}