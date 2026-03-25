import { G } from '@svgdotjs/svg.js'

export interface IComponentGenerator {
 
    
    /**
     * Generate all available graphical elements
     * graphical element are added to the provided group
     *
     * @param componentGroup group where the svg element will be added
     */
    generateAll(componentGroup: G) : void;


    /**
     * Generate all available graphical elements relative to static information
     * 
     * @param componentGroup group where the svg element will be added
     */
    generateAllStatic(componentGroup: G) : void;

    getWidth() : number;
        
    getLength() : number;

}