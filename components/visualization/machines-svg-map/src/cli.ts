
import { SvgGenerator } from './modules/SvgGenerator.js';


const [, , fileName, outFilename] = process.argv;

if (!fileName) {
  console.error('Please provide a conf filename as an argument (e.g., node index.js  myconf.json myconf.svg)');
  process.exit(1);
}

if (!outFilename) {
  console.error('Please provide an output filename as an argument (e.g., node myconf.json myconf.svg)');
  process.exit(1);
}

console.log('reading '+fileName + ' -> '+outFilename);
const svgGenerator = new SvgGenerator();
svgGenerator.generate(fileName);
svgGenerator.writeToFile(outFilename);

//generateSVG(fileName);