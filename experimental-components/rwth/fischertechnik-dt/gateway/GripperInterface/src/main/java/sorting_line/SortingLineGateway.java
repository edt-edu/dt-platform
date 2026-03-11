package sorting_line;

import utils.AbstractGateway;
import utils.MachineIdBuilder;
import utils.MachineId;

import java.util.Map;

public class SortingLineGateway extends AbstractGateway<SortingLineObserver> {

    public SortingLineGateway(MachineIdBuilder machineIdBuilder) {
        super(machineIdBuilder, MachineIdBuilder.ComponentType.SORTING_LINE, Map.of());
        this.addMethods(Map.of(
                // Sensors of the machine
                this.machineId.getTopic(MachineId.ValueType.Sensor, "InputLightBarrier"),
                (observer, msg) -> observer.onLightBarrierInlet(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "MiddleLightBarrier"),
                (observer, msg) -> observer.onLightBarrierBehindColorSensor(msg.getBooleanMember("value")),
                //TODO: color sensor is missing from the data -> no valueName known
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "WhiteLightBarrier"),
                (observer, msg) -> observer.onLightBarrierWhite(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "BlueLightBarrier"),
                (observer, msg) -> observer.onLightBarrierBlue(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "RedLightBarrier"),
                (observer, msg) -> observer.onLightBarrierRed(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "ImpulseCounterRaw"),
                (observer, msg) -> observer.onPulseCounter(msg.getBooleanMember("value"))
        ));

        this.addMethods(Map.of(
                // Actuators of the machine
                this.machineId.getTopic(MachineId.ValueType.Actuator, "MotorConveyor"),
                (observer, msg) -> observer.onMoveConveyor(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "CompressorOn"),
                (observer, msg) -> observer.onEnableCompressor(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "WhiteEjector"),
                (observer, msg) -> observer.onEnableValveWhiteEjector(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "RedEjector"),
                (observer, msg) -> observer.onEnableValveRedEjector(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "BlueEjector"),
                (observer, msg) -> observer.onEnableValveBlueEjector(msg.getBooleanMember("value"))
        ));
    }
}
