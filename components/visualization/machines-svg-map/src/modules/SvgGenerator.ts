
// list for colors https://www.w3.org/TR/css-color-3/#svg-color

import { createSVGWindow } from 'svgdom'
import { SVG, Svg, Defs, registerWindow, G } from '@svgdotjs/svg.js'

import { readFileSync, writeFileSync } from 'fs';

import { VGRSvgGenerator } from './machines/VGRSvgGenerator.js';
import { CBSvgGenerator } from './machines/CBSvgGenerator.js';
import { SLCSvgGenerator } from './machines/SLCSvgGenerator.js';
import { HBWSvgGenerator } from './machines/HBWSvgGenerator.js';
import { MPOSvgGenerator } from './machines/MPOSvgGenerator.js';

import { FactoryLayout, MachineKind, MachinePosition } from './config/FactoryLayout.js';


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


  generateFromConfigFile(fileName: string) {

    // load from configuration file 
    const content = readFileSync(fileName,'utf-8')
    const factoryLayout : FactoryLayout =JSON.parse(content);

    this.canvasXSize = factoryLayout.xSize
    this.canvasYSize = factoryLayout.xSize

    this.createSVGCanvas(this.canvasXSize, this.canvasYSize);

    // add rulers
    this.createSVGRulers(this.canvasXSize, this.canvasYSize);

    this.generateFromLayout(factoryLayout);

    // Serialize to JSON
    //const jsonFactoryLayout = JSON.stringify(factoryLayout, null, 2);
    //console.log(jsonFactoryLayout);
  }

  /**
   * generate the SVG for the given FactoryLayout configuration
   * @param fLayout  
   */
  generateFromLayout(fLayout : FactoryLayout) {
    for (let index = 0; index < fLayout.positions.length; index++) {
      const machine = fLayout.positions[index];
      const group = this.svg.group();
      group.id(machine.id);
      switch (machine.kind) {
        case MachineKind.CB:
          new CBSvgGenerator('_'+machine.id).generateAll(group);
          break;
        case MachineKind.HBW:
           new HBWSvgGenerator('_'+machine.id).generateAll(group);
          break;
        case MachineKind.SLC:
            new SLCSvgGenerator('_'+machine.id).generateAll(group);
          break;
        case MachineKind.MPO:
            new MPOSvgGenerator('_'+machine.id).generateAll(group);
          break;
        case MachineKind.VGR:
            new VGRSvgGenerator('_'+machine.id).generateAll(group);
          break;
    
        default:
          throw new Error(`Non-existent machine kind : ${machine.kind}`);
      }
      if(machine.degrees != 0 ) {
        group.rotate(machine.degrees, 0, 0);
      }
      if(machine.x != 0 || machine.y != 0) {
        group.translate(machine.x, machine.y);
      }
    }
  }

  /**
   * save the SVG content in a file
   * applies some formatting to make the svg readable by human
   * @param fileName 
   */
  writeToFile(fileName: string) {

    console.log('writing '+fileName);

    const svgString = this.svg.svg();
    writeFileSync(fileName, this.formatXml(svgString));
  }

  /**
   * for testing purpose: create and print the json of a sample FactoryLayout
   * @returns 
   */
  createSampleFactoryLayout() : FactoryLayout {

    const cb1 = new MachinePosition('cb1', 730, 1010, 180, MachineKind.CB, 'ConveyorBelt1');
    const slc1 = new MachinePosition('slc1', 150, 1350, 270, MachineKind.SLC, 'SortingLine1');
    const hbw1 = new MachinePosition('hbw1', 460, 150, 0, MachineKind.HBW, 'HighBayWarehouse1');
    const mpo1 = new MachinePosition('hbw1', 460, 470, 90, MachineKind.MPO, 'MultiProcessingStation1');
    const vgr1 = new MachinePosition('vgr1', 750, 810, 180, MachineKind.VGR, 'VacuumGripper1');
    const vgr2 = new MachinePosition('vgr2', 660, 1020, 90, MachineKind.VGR, 'VacuumGripper2');
    const factoryLayout = new FactoryLayout('RennesFactory_Setup1', 1500, 2000, [cb1, hbw1, slc1, mpo1, vgr1, vgr2]);
    // Serialize to JSON
    const jsonFactoryLayout = JSON.stringify(factoryLayout, null, 2);
    console.log(jsonFactoryLayout);

    return factoryLayout;

  }
 
  
  
}