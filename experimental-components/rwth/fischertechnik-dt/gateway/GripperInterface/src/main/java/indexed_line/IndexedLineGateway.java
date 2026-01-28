package indexed_line;

import utils.AbstractGateway;
import utils.MachineId;
import utils.MachineIdBuilder;


import java.util.Map;

public class IndexedLineGateway extends AbstractGateway<IndexedLineObserver> {

    public IndexedLineGateway(MachineIdBuilder machineIdBuilder) {
        super(machineIdBuilder, MachineIdBuilder.ComponentType.INDEXED_LINE, Map.of());
        this.addMethods(Map.of(
                // Sensors of the machine
                this.machineId.getTopic(MachineId.ValueType.Sensor, "Slider1Front"),
                (observer, msg) -> observer.onRefSwitchSlider1Front(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "Slider1Rear"),
                (observer, msg) -> observer.onRefSwitchSlider1Rear(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "Slider2Front"),
                (observer, msg) -> observer.onRefSwitchSlider2Front(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "Slider2Rear"),
                (observer, msg) -> observer.onRefSwitchSlider2Rear(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "SensSlider1"),
                (observer, msg) -> observer.onLightBarrierSlider1(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "Milling"),
                (observer, msg) -> observer.onLightBarrierMillingMachine(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "Loading"),
                (observer, msg) -> observer.onLightBarrierLoadingStation(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "Drilling"),
                (observer, msg) -> observer.onLightBarrierDrillingMachine(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "Swap"),
                (observer, msg) -> observer.onLightBarrierConveyorSwap(msg.getBooleanMember("value"))
        ));

        this.addMethods(Map.of(
                // Actuators of the machine
                this.machineId.getTopic(MachineId.ValueType.Actuator, "Slider1Forward"),
                (observer, msg) -> observer.onMoveSlider1Forward(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "Slider1Backward"),
                (observer, msg) -> observer.onMoveSlider1Backward(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "Slider2Forward"),
                (observer, msg) -> observer.onMoveSlider2Forward(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "Slider2Backward"),
                (observer, msg) -> observer.onMoveSlider2Backward(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "FeedConveyor"),
                (observer, msg) -> observer.onMoveConveyorFeed(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "MillingConveyor"),
                (observer, msg) -> observer.onMoveConveyorMillingMachine(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "DrillingConveyor"),
                (observer, msg) -> observer.onMoveConveyorDrillingMachine(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "SwapConveyor"),
                (observer, msg) -> observer.onMoveConveyorSwap(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "Drilling"),
                (observer, msg) -> observer.onMoveDrillingMachine(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "Milling"),
                (observer, msg) -> observer.onMoveMillingMachine(msg.getBooleanMember("value"))
        ));
    }
}
