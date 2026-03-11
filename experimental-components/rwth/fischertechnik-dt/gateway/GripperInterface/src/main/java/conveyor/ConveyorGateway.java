package conveyor;

import utils.AbstractGateway;
import utils.MachineId;
import utils.MachineIdBuilder;

import java.util.Map;

public class ConveyorGateway extends AbstractGateway<ConveyorObserver> {

    public ConveyorGateway(MachineIdBuilder machineIdBuilder) {
        super(machineIdBuilder, MachineIdBuilder.ComponentType.CONVEYOR_BELT, Map.of());
        this.addMethods(Map.of(
                // Sensors of the machine
                this.machineId.getTopic(MachineId.ValueType.Sensor, "Feed"),
                (observer, msg) -> observer.onLightBarrierFeedStation(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "Swap"),
                (observer, msg) -> observer.onLightBarrierSwapStation(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "Impulse"),
                (observer, msg) -> observer.onPulseButton(msg.getBooleanMember("value"))
        ));
        this.addMethods(Map.of(
                // Actuators of the machine
                this.machineId.getTopic(MachineId.ValueType.Actuator, "Forward"),
                (observer, msg) -> observer.onMoveConveyorForward(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "Backward"),
                (observer, msg) -> observer.onMoveConveyorBackward(msg.getBooleanMember("value"))
        ));
    }
}
