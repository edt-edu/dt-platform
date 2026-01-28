package multiprocessing;

import utils.AbstractGateway;
import utils.MachineId;
import utils.MachineIdBuilder;

import java.util.Map;


public class MultiprocessingGateway extends AbstractGateway<MultiprocessingObserver> {

    public MultiprocessingGateway(MachineIdBuilder machineIdBuilder) {
        super(machineIdBuilder, MachineIdBuilder.ComponentType.MULTI_PROCESSING, Map.of());
        this.addMethods(Map.of(
                // Sensors of the machine
                this.machineId.getTopic(MachineId.ValueType.Sensor, "TurntablePosVacuum"),
                (observer, msg) -> observer.onRefSwitchTurnTableAtVacuum(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "TurntablePosBelt"),
                (observer, msg) -> observer.onRefSwitchTurnTableAtBelt(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "TurntablePosSaw"),
                (observer, msg) -> observer.onRefSwitchTurnTableAtSaw(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "EndConveyor"),
                (observer, msg) -> observer.onLightBarrierConveyorEnd(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "Oven"),
                (observer, msg) -> observer.onLightBarrierOven(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "VacuumGripperAtTurntable"),
                (observer, msg) -> observer.onRefSwitchVacuumAtTurnTable(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "VacuumGripperAtOven"),
                (observer, msg) -> observer.onRefSwitchVacuumAtOven(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "FeederIn"),
                (observer, msg) -> observer.onRefSwitchFeederInside(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "FeederOut"),
                (observer, msg) -> observer.onRefSwitchFeederOutside(msg.getBooleanMember("value"))
        ));

        this.addMethods(Map.of(
                // Actuators of the machine
                this.machineId.getTopic(MachineId.ValueType.Actuator, "RotClockwise"),
                (observer, msg) -> observer.onMoveTurnTableClockwise(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "RotCounterclockwise"),
                (observer, msg) -> observer.onMoveTurnTableCounterclockwise(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "ConveyorForward"),
                (observer, msg) -> observer.onMoveConveyorForward(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "Saw"),
                (observer, msg) -> observer.onEnableSaw(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "OvenInward"),
                (observer, msg) -> observer.onMoveOvenFeederRetract(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "OvenOutward"),
                (observer, msg) -> observer.onMoveOvenFeederExtend(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "GripperToOven"),
                (observer, msg) -> observer.onMoveVacuumToOven(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "GripperToTurntable"),
                (observer, msg) -> observer.onMoveVacuumToTurnTable(msg.getBooleanMember("value"))
        ));

        this.addMethods(Map.of(
                //Base Actuators of the machine
                //TODO: do not follow naming convention of other gateways multiProcessingOvenLight instead of multiProcessingActOvenLight make custom naming
                /*this.machineId.getTopic(MachineId.ValueType.Actuator, "OvenLight"),
                (observer, msg) -> observer.onEnableOvenLight(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "Compressor"),
                (observer, msg) -> observer.onEnableCompressor(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "ValveVacuum"),
                (observer, msg) -> observer.onEnableValveVacuum(msg.getBooleanMember("value")),
                */
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "LowerValve"),
                (observer, msg) -> observer.onEnableValveMoveVacuum(msg.getBooleanMember("value"))

                // TODO: this has the same problem as the first ones
                /*
                this.machineId.getTopic(MachineId.ValueType.Actuator, "ValveOvenDoor"),
                (observer, msg) -> observer.onEnableValveMoveOvenDoor(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "ValveFeeder"),
                (observer, msg) -> observer.onEnableValveMoveFeeder(msg.getBooleanMember("value"))
                */
       ));

    }

}