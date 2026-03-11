package vacuum_gripper;

import org.eclipse.paho.client.mqttv3.*;
import utils.AbstractGateway;
import utils.MachineId;
import utils.MachineIdBuilder;

import java.util.Map;

public class GripperGateway extends AbstractGateway<GripperObserver> {

    public GripperGateway(MachineIdBuilder machineIdBuilder) {
        super(machineIdBuilder, MachineIdBuilder.ComponentType.VACUUM_GRIPPER, Map.of());
        this.addMethods(Map.of(
                // Sensors of the machine
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "VerticalEndUp"),
                (observer, msg) -> observer.onRefSwitchVertical(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "ArmEndIn"),
                (observer, msg) -> observer.onRefSwitchHorizontal(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "RotEnd"),
                (observer, msg) -> observer.onRefSwitchRotation(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "VerticalEncoderCounter"),
                (observer, msg) -> observer.onEncoderVerticalPos(msg.getIntegerMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "ArmEncoderCounter"),
                (observer, msg) -> observer.onEncoderHorizontalPos(msg.getIntegerMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "RotEncoderCounter"),
                (observer, msg) -> observer.onEncoderRotationPos(msg.getIntegerMember("value"))
        ));
        this.addMethods(Map.of(
                //Actuators of the machine
                //
                machineId.getTopic(MachineId.ValueType.Actuator, "VerticalUp"),
                (observer, msg) -> observer.onMoveVerticalUp(msg.getBooleanMember("value")),
                //
                machineId.getTopic(MachineId.ValueType.Actuator, "VerticalDown"),
                (observer, msg) -> observer.onMoveVerticalDown(msg.getBooleanMember("value")),
                //
                machineId.getTopic(MachineId.ValueType.Actuator, "ArmIn"),
                (observer, msg) -> observer.onMoveHorizontalDown(msg.getBooleanMember("value")),
                //
                machineId.getTopic(MachineId.ValueType.Actuator, "ArmOut"),
                (observer, msg) -> observer.onMoveHorizontalUp(msg.getBooleanMember("value")),
                //
                machineId.getTopic(MachineId.ValueType.Actuator, "RotLeft"),
                (observer, msg) -> observer.onMoveRotationCounterclockwise(msg.getBooleanMember("value")),
                //
                machineId.getTopic(MachineId.ValueType.Actuator, "RotRight"),
                (observer, msg) -> observer.onMoveRotationClockwise(msg.getBooleanMember("value")),
                //
                machineId.getTopic(MachineId.ValueType.Actuator, "CompressorOn"),
                (observer, msg) -> observer.onEnableCompressor(msg.getBooleanMember("value")),
                //
                machineId.getTopic(MachineId.ValueType.Actuator, "Valve"),
                (observer, msg) -> observer.onEnableValve(msg.getBooleanMember("value"))
        ));
    }
}