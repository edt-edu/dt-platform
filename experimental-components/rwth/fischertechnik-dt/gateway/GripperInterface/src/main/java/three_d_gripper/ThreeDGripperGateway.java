package three_d_gripper;

import utils.AbstractGateway;
import utils.MachineIdBuilder;

import java.util.Map;

public class ThreeDGripperGateway extends AbstractGateway<ThreeDGripperObserver> {

    public ThreeDGripperGateway(MachineIdBuilder machineIdBuilder) {
        super(machineIdBuilder, MachineIdBuilder.ComponentType.PUNCHING_MACHINE, Map.of());
        //TODO: add methods
    }
}