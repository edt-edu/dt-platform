
import { parseArgs } from 'node:util';
import { SvgGenerator } from './modules/SvgGenerator.js';

const { values, positionals } = parseArgs({
  options: {
    addPositions: {
      type: 'boolean',
      default: false,
    },
    addDimensions: {
      type: 'boolean',
      default: false,
    },
    accessibleZoneOpacity: {
      type: 'string',   
      default: '0.5',
    },
    help: {
      type: 'boolean',
      short: 'h',
    },
  },
  allowPositionals: true,
});

function showHelp() {
  console.log(`
Usage:
  node index.js <configFile> <outputFile> [options]

Arguments:
  configFile        configuration JSON file
  outputFile        output SVG file

Options:
  --addPositions    add element positions
  --addDimensions   add element dimensions
  --accessibleZoneOpacity <value>  set the opacity for the accessible zones (default: 0.5)
  -h, --help        show this help
`);
}

if (values.help) {
  showHelp();
  process.exit(0);
}

const [fileName, outFilename] = positionals;

if (!fileName || !outFilename) {
  showHelp();
  process.exit(1);
}

console.log(`reading ${fileName} -> ${outFilename}`);

const accessibleZoneOpacity = Number(values.accessibleZoneOpacity);

if (Number.isNaN(accessibleZoneOpacity) || accessibleZoneOpacity < 0 || accessibleZoneOpacity > 1) {
  console.error('Opacity must be a decimal between 0 and 1');
  process.exit(1);
}

const svgGenerator = new SvgGenerator(values.addPositions ?? false, values.addDimensions ?? false, accessibleZoneOpacity);

svgGenerator.generateFromConfigFile(fileName);
svgGenerator.writeToFile(outFilename);
//generateSVG(fileName);