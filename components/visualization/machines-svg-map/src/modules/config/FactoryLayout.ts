
export enum MachineKind {
    VGR = "VGR",
    MPO = "MPO",
    CB = "CB",
    SLC = "SLC",
    HBW = "HBW"
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
    positions: MachinePosition[];

    constructor(name: string, positions: MachinePosition[]) {
        this.name = name;
        this.positions = positions;
    }
}