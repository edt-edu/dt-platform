
// list for colors https://www.w3.org/TR/css-color-3/#svg-color

import { createSVGWindow } from 'svgdom'
import { SVG, Svg, Defs, registerWindow, G } from '@svgdotjs/svg.js'

import { readFileSync, writeFileSync } from 'fs';


import { VGRSvgGenerator } from './generators/machines/VGRSvgGenerator.js';
import { CBSvgGenerator } from './generators/machines/CBSvgGenerator.js';
import { SLCSvgGenerator } from './generators/machines/SLCSvgGenerator.js';
import { HBWSvgGenerator } from './generators/machines/HBWSvgGenerator.js';
import { MPOSvgGenerator } from './generators/machines/MPOSvgGenerator.js';

import { StraightSlideSvgGenerator } from './generators/components/StraightSlideSvgGenerator.js';
import { RHSlideSvgGenerator } from './generators/components/RHSlideSvgGenerator.js';

import { ComponentKind, CustomSizeComponentKind, CustomSizeComponentPosition, ElementType, FactoryLayout, MachineKind, MachinePosition } from './config/FactoryLayout.js';
import { IComponentGenerator } from './generators/IComponentGenerator.js';
import { TableSvgGenerator } from './generators/customsizecomponents/TableSvgGenerator.js';
import { plainToInstance } from 'class-transformer';
import { validateSync } from 'class-validator';


type GeneratorConstructor = new (id: string) => IComponentGenerator;
type CustomSizeComponentGeneratorConstructor = new (id: string, width: number, length: number) => IComponentGenerator;
const MACHINE_GENERATOR_REGISTRY: Record<string, GeneratorConstructor> = {
  [MachineKind.CB]: CBSvgGenerator,
  [MachineKind.HBW]: HBWSvgGenerator,
  [MachineKind.SLC]: SLCSvgGenerator,
  [MachineKind.MPO]: MPOSvgGenerator,
  [MachineKind.VGR]: VGRSvgGenerator,
};
const COMPONENT_GENERATOR_REGISTRY: Record<string, GeneratorConstructor> = {
  [ComponentKind.S_SLI]: StraightSlideSvgGenerator,
  [ComponentKind.RH_SLI]: RHSlideSvgGenerator,
};
const CUSTOMSIZECOMPONENT_GENERATOR_REGISTRY: Record<string, CustomSizeComponentGeneratorConstructor> = {
  [CustomSizeComponentKind.TABLE]: TableSvgGenerator,
};

export class SvgGenerator {

  svg!: Svg;
  defs!: Defs;

  canvasXSize!: number;
  canvasYSize!: number;
  addPositions: boolean;
  addDimensions: boolean;
  accessibleZoneOpacity: number;

  constructor(addPositions: boolean, addDimensions: boolean, accessibleZoneOpacity: number) {
    const window = createSVGWindow()
    const document = window.document

    this.addPositions = addPositions;
    this.addDimensions = addDimensions;
    this.accessibleZoneOpacity = accessibleZoneOpacity;
    // register window and document
    registerWindow(window, document)



  }

  createSVGCanvas(xSize: number, ySize: number) {
    this.svg = SVG().size(xSize, ySize).viewbox(0, 0, xSize, ySize);
    const style = this.svg.style();
    style.rule(".accessZone", {
      fill: 'lightblue',
      'fill-opacity': this.accessibleZoneOpacity
    }
    );
    style.rule(".woodBase", {
      fill: 'burlywood',
      'stroke': 'black'
    });

    style.rule(".tableBase", {
      fill: 'burlywood',
      'fill-opacity': 0,
      'stroke': 'black',
      'stroke-width': 1,
      'stroke-dasharray': '5, 5'
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
    style.rule(".slider", { // todo some color change or arrow for direction of the slide ?
      fill: 'dimgray',
      'stroke': 'black'
    })
    style.rule(".ejector", { // todo some color change or arrow for direction of the slide ?
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
  createSVGRulers(xSize: number, ySize: number) {
    const rulersGroup = this.svg.group();
    rulersGroup.id('rulers')
    rulersGroup.line(0, 0, xSize, 0).stroke({ width: 1, color: 'black' });
    const xNbTicks = xSize / 100;
    for (let index = 0; index < xNbTicks; index++) {
      rulersGroup.line(index * 100, 0, index * 100, 10).stroke({ width: 1, color: 'black' });
      if (index > 0 && (this.addPositions || this.addDimensions)) {
        rulersGroup.text(`${index * 10}cm`).move(index * 100 + 2, -5).font({ size: 10 }).fill('black');
      }
    }
    rulersGroup.line(0, 0, 0, ySize).stroke({ width: 1, color: 'black' });
    const yNbTicks = ySize / 100;
    for (let index = 0; index < yNbTicks; index++) {
      rulersGroup.line(0, index * 100, 10, index * 100).stroke({ width: 1, color: 'black' });
      if (index > 0 && (this.addPositions || this.addDimensions)) {
        rulersGroup.text(`${index * 10}cm`).move(5, index * 100 - 5).font({ size: 10 }).fill('black');
      }
    }
  }

  generateLocationString(machinePosition: MachinePosition, machineGenerator: IComponentGenerator, group: G) {
    const positiongroup = group.group();
    positiongroup.id(group.id() + '_positionGroup');
    positiongroup.translate(machinePosition.x, machinePosition.y)
    if (this.addPositions) {
      positiongroup.text(`x: ${machinePosition.x}mm, y: ${machinePosition.y}mm`).move(0, -20).font({ size: 10 }).fill('black');
    }
    if (this.addDimensions) {
      positiongroup.text(`W: ${machineGenerator.getWidth()}mm L: ${machineGenerator.getLength()}mm`).move(0, -35).font({ size: 10 }).fill('black');
    }
  }

  /** tool function allowing to pretty print the svg xml */
  formatXml(xml: string) {
    var formatted = '', indent = '';
    const tab = '\t';
    xml.split(/>\s*</).forEach(function (node) {
      if (node.match(/^\/\w/)) indent = indent.substring(tab.length); // decrease indent by one 'tab'
      formatted += indent + '<' + node + '>\r\n';
      if (node.match(/^<?\w([^>/]*|[^>]*[^/])$/)) indent += tab;      // increase indent
    });
    return formatted.substring(1, formatted.length - 3);
  }


  generateFromConfigFile(fileName: string) {

    // load from configuration file 
    const content = readFileSync(fileName, 'utf-8')
    const rawObject = JSON.parse(content);

    const factoryLayout = plainToInstance(FactoryLayout, rawObject as FactoryLayout);

    const errors = validateSync(factoryLayout, { whitelist: true, forbidNonWhitelisted: true });

    if (errors.length > 0) {
      console.error("❌ validation error :");
      const detailedErrors = errors.map(err => err.toString()).join('\n');
      throw new Error(`Validation failed for ${fileName}:\n${detailedErrors}`);
    }


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
  generateFromLayout(fLayout: FactoryLayout) {
    for (let index = 0; index < fLayout.positions.length; index++) {
      const rawComponent = fLayout.positions[index];
      const parentgroup = this.svg.group();
      parentgroup.id(rawComponent.id + '_parentGroup');
      const group = parentgroup.group();
      group.id(rawComponent.id);

      var generator: IComponentGenerator;
      switch (rawComponent.elementType) {
        case ElementType.MACHINE:
          const MachineGeneratorClass = MACHINE_GENERATOR_REGISTRY[rawComponent.kind];
          var generator = new MachineGeneratorClass('_' + rawComponent.id);
          break;
        case ElementType.COMPONENT:
          const componentGeneratorClass = COMPONENT_GENERATOR_REGISTRY[rawComponent.kind];
          var generator = new componentGeneratorClass('_' + rawComponent.id);
          break;
        case ElementType.CUSTOMSIZECOMPONENT:
          const CustomSizeComponentGeneratorClass = CUSTOMSIZECOMPONENT_GENERATOR_REGISTRY[rawComponent.kind];
          const component = rawComponent as CustomSizeComponentPosition;
          var generator = new CustomSizeComponentGeneratorClass('_' + rawComponent.id,
            component.width,
            component.length);
          break;
      }

      generator.generateAll(group);

      if (rawComponent.elementType === ElementType.MACHINE) {
        this.generateLocationString(rawComponent as MachinePosition, generator, parentgroup);
      }

      if (rawComponent.degrees != 0) {
        group.rotate(rawComponent.degrees, 0, 0);
      }
      if (rawComponent.x != 0 || rawComponent.y != 0) {
        group.translate(rawComponent.x, rawComponent.y);
      }
    }
  }

  /**
   * save the SVG content in a file
   * applies some formatting to make the svg readable by human
   * @param fileName 
   */
  writeToFile(fileName: string) {

    console.log('writing ' + fileName);

    const svgString = this.svg.svg();
    writeFileSync(fileName, this.formatXml(svgString));
  }

}