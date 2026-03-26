

import { G } from '@svgdotjs/svg.js'
import { IComponentGenerator } from '../IComponentGenerator.js';

/**
 * Class to generate a Straight Slide
 * useful for example on exit of the conveyor belt or the MPS conveyor
 */
export class StraightSlideSvgGenerator implements IComponentGenerator {

    id_postfix = '';

    constructor(id_postfix: string) {
        this.id_postfix = id_postfix;
    }

    getWidth(): number {
        throw new Error('Method not implemented.');
    }
    getLength(): number {
        throw new Error('Method not implemented.');
    }

    generateAll(machineGroup: G) {
        this.generateAllStatic(machineGroup);
    }

    generateAllStatic(machineGroup: G) {
        this.generateSlideTrapeze(machineGroup);
        this.generateSlideCircle(machineGroup);
        this.generateArrivalPosition(machineGroup);
    }

    generateArrivalPosition(machineGroup: G) {
        //Arrival position
        var zoneGroup = machineGroup.group();
        zoneGroup.id("arrivalPosition" + this.id_postfix);
        const mainShape = zoneGroup.circle(28);
        mainShape.move(17, 39);
        mainShape.fill('lightblue').attr({ 'fill-opacity': 0.75 });
    }

    generateSlideCircle(machineGroup: G) {
        //Circle Form
        var zoneGroup = machineGroup.group();
        zoneGroup.id("circleForm" + this.id_postfix);
        const mainShape = zoneGroup.circle(32);
        mainShape.move(15, 37);
        mainShape.fill('gray');
    }

    generateSlideTrapeze(machineGroup: G) {
        //Trapeze Form
        var zoneGroup = machineGroup.group();
        zoneGroup.id("trapezeForm" + this.id_postfix)
        const trapeze = makeTrapeze(
            zoneGroup,
            0,          //x
            0,          //y
            32,         // top width
            62,         // bottom width (example: wider than top)
            53          // height
        ).fill('gray');
        trapeze.move(0, 0);
        trapeze.rotate(180);
        var refPoint = zoneGroup.rect(2, 2).fill('red');
        refPoint.id('referencePoint' + this.id_postfix);
    }

}

function makeTrapeze(draw: any, x: number, y: number, topWidth: number, bottomWidth: number, height: number) {
    // difference between bottom and top width, split equally on both sides
    const dx = (bottomWidth - topWidth) / 2;

    const points = [
        [x, y],                           // top-left
        [x + topWidth, y],                // top-right
        [x + topWidth + dx, y + height],  // bottom-right
        [x - dx, y + height]              // bottom-left
    ];

    return draw.polygon(points);
}
