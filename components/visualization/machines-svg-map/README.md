
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


Note: the reference point (ie. x = 0, y = 0) of each machine is the upper left corner of each machine when the machine is place in a way we can read the Fischertechnik sticker (I suppose that for a given model this sticker is always at the same place)

TODO: 

- [ ] support for dynamic information

## Build instructions


Setup environment

install nvm

use node vrsion lts

`nvm use --lts`

install dependancies

`npm install`


build etc (see script section in `package.json`)

for example

`npm run watch`

`npm run generate`

`npm run watch:generate`


