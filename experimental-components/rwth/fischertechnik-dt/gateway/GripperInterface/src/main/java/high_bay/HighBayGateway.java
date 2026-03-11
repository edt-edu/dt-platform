package high_bay;

import org.eclipse.paho.client.mqttv3.*;
import utils.AbstractGateway;
import utils.MachineId;
import utils.MachineIdBuilder;

import java.util.Map;

public class HighBayGateway extends AbstractGateway<HighBayObserver> {

    public HighBayGateway(MachineIdBuilder machineIdBuilder) {
        super(machineIdBuilder, MachineIdBuilder.ComponentType.HIGH_BAY, Map.of());
        this.addMethods(Map.of(
                // Sensors of the machine
                this.machineId.getTopic(MachineId.ValueType.Sensor, "CantileverBack"),
                (observer, msg) -> observer.onRefSwitchCantileverBack(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "CantileverFront"),
                (observer, msg) -> observer.onRefSwitchCantileverFront(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "Horizontal"),
                (observer, msg) -> observer.onRefSwitchHorizontal(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "HorizontalEncoderCounter"),
                (observer, msg) -> observer.onEncoderHorizontalPos(msg.getIntegerMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "Inside"),
                (observer, msg) -> observer.onLightBarrierInside(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "Outside"),
                (observer, msg) -> observer.onLightBarrierOutside(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "Vertical"),
                (observer, msg) -> observer.onRefSwitchVertical(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "VerticalEncoderCounter"),
                (observer, msg) -> observer.onEncoderVerticalPos(msg.getIntegerMember("value"))

        ));
        this.addMethods(Map.of(
                // Actuators of the machine
                this.machineId.getTopic(MachineId.ValueType.Actuator, "CantileverBackward"),
                (observer, msg) -> observer.onMoveCantileverBackward(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "CantileverForward"),
                (observer, msg) -> observer.onMoveCantileverForward(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "ConveyorBackward"),
                (observer, msg) -> observer.onMoveConveyorBackward(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "ConveyorForward"),
                (observer, msg) -> observer.onMoveConveyorForward(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "HorizontalToRack"),
                (observer, msg) -> observer.onMoveArmToRack(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "HorizontalToConveyor"),
                (observer, msg) -> observer.onMoveArmToConveyor(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "Down"),
                (observer, msg) -> observer.onMoveVerticalDown(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "Up"),
                (observer, msg) -> observer.onMoveVerticalUp(msg.getBooleanMember("value"))
        ));
    }
}
