
export enum MachineKind {
    VGR = "VGR",
    MPO = "MPO",
    CB = "CB",
    SLC = "SLC",
    HBW = "HBW",
    CB_MPS_SLI = "CB_MPS_SLI",
    SL_SLI = "SL_SLI"
}

export class MachinePosition {

    id: string;
    x: number;
    y: number;
    degrees: number
    kind: MachineKind
    name: string;

    constructor(id: string, x: number, y: number, degrees: number, kind: MachineKind, name: string) {
        this.x = x;
        this.y = y;
        this.degrees = degrees;
        this.kind = kind;
        this.name = name;
        this.id = id;
    }
}

export class FactoryLayout {
    name: string;
    xSize: number;
    ySize: number;
    positions: MachinePosition[];

    constructor(name: string,  xSize: number,  ySize: number, positions: MachinePosition[]) {
        this.name = name;
        this.xSize = xSize;
        this.ySize = ySize;
        this.positions = positions;
    }
}