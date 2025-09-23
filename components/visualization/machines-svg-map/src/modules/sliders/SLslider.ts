

import { G } from '@svgdotjs/svg.js'
import { IMachineGenerator } from './../machines/IMachineGenerator.js';

export class SLslider implements IMachineGenerator {

    id_postfix = '';

    constructor(id_postfix: string) {
        this.id_postfix = id_postfix;
    }

    generateAll(machineGroup: G) {
        this.generateAllStatic(machineGroup);
    }

    generateAllStatic(machineGroup: G) {
        this.generateSliderRectangle(machineGroup);
        this.generateSliderOtherRectangle(machineGroup);
        this.generateSliderCircle(machineGroup);
        this.generateArrivalPosition(machineGroup);
    }

    generateSliderRectangle(machineGroup: G) {
        //First rectanlge
        var zoneGroup = machineGroup.group();
        zoneGroup.id("firstRectanle" + this.id_postfix);
        const mainShape = zoneGroup.rect(34,36);
        mainShape.move(0, 0);
        mainShape.fill('gray');
    }

    generateSliderOtherRectangle(machineGroup: G) {
        //Second rectanlge
        var zoneGroup = machineGroup.group();
        zoneGroup.id("firstRectanle" + this.id_postfix);
        const mainShape = zoneGroup.rect(130,32);
        mainShape.move(0, 0);
        mainShape.fill('gray');
    }

    generateSliderCircle(machineGroup: G) {
        //Circle Form
        var zoneGroup = machineGroup.group();
        zoneGroup.id("circleForm" + this.id_postfix);
        const mainShape = zoneGroup.circle(32);
        mainShape.move(113, 0);
        mainShape.fill('gray');
    }

    generateArrivalPosition(machineGroup: G) {
        //Arrival position
        var zoneGroup = machineGroup.group();
        zoneGroup.id("arrivalPosition" + this.id_postfix);
        const mainShape = zoneGroup.circle(28);
        mainShape.move(115, 2);
        mainShape.fill('lightblue').attr({ 'fill-opacity': 0.75 });
    }
}
