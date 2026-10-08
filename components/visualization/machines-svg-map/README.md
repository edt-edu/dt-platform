
## MachinesSVGMap


Create SVG 2D map for the FischerTechnik machines.

Features:

- use physical measures of the machine (1 pixel = 1mm) (precision: about 5mm)
- Allows to place the machines in a layout
- print static informations of the machines
- allow to visualize access zones of the machines
- load configuration from json file
- id of machines is reported as svg group id so a transformaton may be acheived later on the image
- use of css styling (embedded in the svg) 


Note: the reference point (ie. x = 0, y = 0) of each machine is the upper left corner of each machine when the machine is positionned in a way we can read the Fischertechnik sticker (I suppose that for a given model this sticker is always at the same place)

TODO: 

- [ ] support for dynamic information

## Usage

### From release installation:

`npm install @edt-edu/machines-svg-map`

`npx machines-svg-map <configFile> <outputFile> [options]`

Arguments:
-  configFile        configuration JSON file
-  outputFile        output SVG file

Options:
-  `--addPositions`    add element positions
-  `--addDimensions`   add element dimensions
-  `--accessibleZoneOpacity <value>`  set the opacity for the accessible zones (default: 0.5)
-  `-h`, `--help`        show this help

Example:
- [configuration JSON file](https://github.com/edt-edu/dt-platform/blob/main/components/visualization/machines-svg-map/resources/machine_config.json)
- SVG output [![SVG corresponding to machine_config.json](https://github.com/edt-edu/dt-platform/blob/main/components/visualization/machines-svg-map/test/__snapshots__/machine_config.golden.svg)](https://github.com/edt-edu/dt-platform/blob/main/components/visualization/machines-svg-map/test/__snapshots__/machine_config.golden.svg)


## Build instructions


Setup environment

install nvm

use node version lts

`nvm use --lts`

install dependencies

`npm install`


build etc (see script section in `package.json`)

for example

`npm run watch`

`npm run generate`

`npm run watch:generate`



launch the test suite

`npm test`

you can update the golden reference by running

`npm run test:update-golden`


Run the command line interface:

`node dist/cli.js <configFile> <outputFile> [options]`

## Notes about installing the tool

https://git.rwth-aachen.de/help/user/packages/npm_registry/index
and https://git.rwth-aachen.de/help/user/packages/npm_registry/index#authenticate-to-the-package-registry


in our case the project Id is :


you can add in your $HOME/.npmrc
```
@edt-edu:registry=https://npm.pkg.github.com
//npm.pkg.github.com/:_authToken=YOUR_ACCESS_TOKEN
```
or 

```
@edt-edu:registry=https://npm.pkg.github.com
//npm.pkg.github.com/:_authToken=${NPM_TOKEN}
```

and make sure to export the var
```
export NPM_TOKEN=YOUR_ACCESS_TOKEN
```


then
`npm i @edt-edu/machines-svg-map`   should work in your node projects




