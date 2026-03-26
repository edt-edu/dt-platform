import { IsEnum, IsNumber, IsString, ValidateNested, IsArray } from 'class-validator';
import { Expose, Type, plainToInstance } from 'class-transformer';
import 'reflect-metadata';

export enum ElementType {
    MACHINE = "MACHINE",
    COMPONENT = "COMPONENT",
    CUSTOMSIZECOMPONENT = "CUSTOMSIZECOMPONENT"
}

export enum MachineKind {
    VGR = "VGR",
    MPO = "MPO",
    CB = "CB",
    SLC = "SLC",
    HBW = "HBW",
}

export enum ComponentKind {
    S_SLI = "S_SLI",
    RH_SLI = "RH_SLI"
}

export enum CustomSizeComponentKind {
    TABLE = "TABLE"
}

export class BasePosition {
    @IsString() id: string;
    @IsNumber() x: number;
    @IsNumber() y: number;
    @IsNumber() degrees: number;
    @IsString() name: string;
    
    @Expose({ name: 'type' })
    @IsEnum(ElementType)
    elementType: ElementType;
    constructor(id: string, x: number, y: number, degrees: number, name: string, elementType: ElementType) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.degrees = degrees;
        this.name = name;
        this.elementType = elementType;
    }
}


export class MachinePosition extends BasePosition {
    @IsEnum(MachineKind)
    kind: MachineKind;
    constructor(id: string, x: number, y: number, degrees: number, name: string, kind: MachineKind) {
        super(id, x, y, degrees, name, ElementType.MACHINE);
        this.kind = kind;
    }
}

export class ComponentPosition extends BasePosition {

    @IsEnum(ComponentKind) kind: ComponentKind;

    constructor(id: string, x: number, y: number, degrees: number, name: string, kind: ComponentKind) {
        super(id, x, y, degrees, name, ElementType.COMPONENT);
        this.kind = kind;
    }
}

export class CustomSizeComponentPosition extends BasePosition {

    @IsEnum(CustomSizeComponentKind) kind: CustomSizeComponentKind;
    @IsNumber() width: number;
    @IsNumber() length: number;

    constructor(id: string, x: number, y: number, degrees: number, name: string, kind: CustomSizeComponentKind, width: number, length: number) {
        super(id, x, y, degrees, name, ElementType.CUSTOMSIZECOMPONENT);
        this.kind = kind;
        this.width = width;
        this.length = length;
    }
}


export class FactoryLayout {
    @IsString() name: string;
    @IsNumber() xSize: number;
    @IsNumber() ySize: number;

    @IsArray()
    @ValidateNested({ each: true })
    @Type(() => BasePosition, {
        keepDiscriminatorProperty: true,
        discriminator: {
            property: 'type',
            subTypes: [
                { value: MachinePosition, name: ElementType.MACHINE },
                { value: ComponentPosition, name: ElementType.COMPONENT },
                { value: CustomSizeComponentPosition, name: ElementType.CUSTOMSIZECOMPONENT },
            ],
        },
    })
    positions: (MachinePosition | ComponentPosition | CustomSizeComponentPosition)[];

    constructor(name: string, xSize: number, ySize: number, positions: (MachinePosition | ComponentPosition | CustomSizeComponentPosition)[] = []) {
        this.name = name;
        this.xSize = xSize;
        this.ySize = ySize;
        this.positions = positions;
    }
}