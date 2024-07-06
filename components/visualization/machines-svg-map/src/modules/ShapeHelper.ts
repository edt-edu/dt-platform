

import { SVG, Svg, Defs, registerWindow, G, Point, PointArray } from '@svgdotjs/svg.js'

export class ShapeHelper {

    drawStar(cx : number, cy : number, spikes : number, outerRadius : number, innerRadius : number) : PointArray {
        var rot = Math.PI / 2 * 3;
        var x = cx;
        var y = cy;
        var step = Math.PI / spikes;
    
        var pointArray : PointArray = new PointArray()
        pointArray.push([cx, cy - outerRadius])
        var prevPoint : Point = new Point(cx, cy - outerRadius)
        
    
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
}




//drawStar(100, 100, 5, 30, 15);