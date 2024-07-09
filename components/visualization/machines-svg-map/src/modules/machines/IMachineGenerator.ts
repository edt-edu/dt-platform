
import { G } from '@svgdotjs/svg.js'

export interface IMachineGenerator {
 
    
    /**
     * Generate all available graphical elements
     * graphical element are added to the provided group
     *
     * @param machineGroup grup where the svg element will be added
     */
    generateAll(machineGroup: G) : void;


    /**
     * Generate all available graphical elements relative to static information
     * This includes access zones
     * 
     * @param machineGroup grup where the svg element will be added
     */
    generateAllStatic(machineGroup: G) : void;

}