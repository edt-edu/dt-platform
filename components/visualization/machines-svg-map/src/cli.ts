
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

const svgGenerator = new SvgGenerator(values.addPositions ?? false, values.addDimensions ?? false);

svgGenerator.generateFromConfigFile(fileName);
svgGenerator.writeToFile(outFilename);
//generateSVG(fileName);