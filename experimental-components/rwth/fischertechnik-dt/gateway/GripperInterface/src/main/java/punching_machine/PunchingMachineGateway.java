package punching_machine;

import org.eclipse.paho.client.mqttv3.*;
import utils.AbstractGateway;
import utils.MachineIdBuilder;
import utils.MachineId;

import java.util.Map;

public class PunchingMachineGateway extends AbstractGateway<PunchingMachineObserver> {

    public PunchingMachineGateway(MachineIdBuilder machineIdBuilder) {
        super(machineIdBuilder, MachineIdBuilder.ComponentType.PUNCHING_MACHINE, Map.of());
        this.addMethods(Map.of(
                // Sensors of the machine
                this.machineId.getTopic(MachineId.ValueType.Sensor, "Goods"),
                (observer, msg) -> observer.onLightBarrierGoodsInOut(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "Machine"),
                (observer, msg) -> observer.onLightBarrierPunchingMachine(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "Up"),
                (observer, msg) -> observer.onSwitchPunchingMachineUp(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Sensor, "Down"),
                (observer, msg) -> observer.onSwitchPunchingMachineDown(msg.getBooleanMember("value"))
        ));
        this.addMethods(Map.of(
                // Actuators of the machine
                this.machineId.getTopic(MachineId.ValueType.Actuator, "ConveyorForward"),
                (observer, msg) -> observer.onMoveConveyorForward(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "ConveyorBackward"),
                (observer, msg) -> observer.onMoveConveyorBackward(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "Up"),
                (observer, msg) -> observer.onMovePunchingMachineUp(msg.getBooleanMember("value")),
                //
                this.machineId.getTopic(MachineId.ValueType.Actuator, "Down"),
                (observer, msg) -> observer.onMovePunchingMachineDown(msg.getBooleanMember("value"))
        ));
    }
}
