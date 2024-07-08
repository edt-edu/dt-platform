

import { Container, Point, PointArray } from '@svgdotjs/svg.js'

export class ShapeHelper {

    /**
     * 
     * @param cx create an arrayPoint for a star (to be used in a polyline or polygon)
     * @param cy 
     * @param spikes 
     * @param outerRadius 
     * @param innerRadius 
     * @returns 
     */
    drawStar(cx : number, cy : number, spikes : number, outerRadius : number, innerRadius : number) : PointArray {
        var rot = Math.PI / 2 * 3;
        var x = cx;
        var y = cy;
        var step = Math.PI / spikes;
    
        var pointArray : PointArray = new PointArray([cx, cy - outerRadius]);
        
        for (var i = 0; i < spikes; i++) {
            x = cx + Math.cos(rot) * outerRadius;
            y = cy + Math.sin(rot) * outerRadius;
            pointArray.push([x, y])
            rot += step;
    
            x = cx + Math.cos(rot) * innerRadius;
            y = cy + Math.sin(rot) * innerRadius;
            pointArray.push([x, y])
            rot += step;
        }
    
        pointArray.push([cx, cy - outerRadius])
        return pointArray
    }

    /**
     * 
     * @param container Generate an axis cross if the provided container (suggestion  a : G group )
     * @param xPos 
     * @param yPos 
     * @param crossSize 
     */
    generateAxisCross(container: Container, xPos : number, yPos : number, crossSize : number) {
        container.circle(crossSize * 2).translate(xPos - crossSize, yPos - crossSize).stroke('black').fill('none');
        container.line(xPos - crossSize,
          yPos + crossSize,
          xPos + crossSize,
          yPos - crossSize).stroke({ width: 1, color: 'black' });
          container.line(xPos - crossSize,
            yPos - crossSize,
            xPos + crossSize,
            yPos + crossSize).stroke({ width: 1, color: 'black' });
      }
}




//drawStar(100, 100, 5, 30, 15);