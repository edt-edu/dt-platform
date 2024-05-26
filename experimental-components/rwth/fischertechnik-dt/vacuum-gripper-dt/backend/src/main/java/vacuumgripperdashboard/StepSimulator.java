package vacuumgripperdashboard;

import montiarc.rte.timesync.BooleanInPort;
import montiarc.rte.timesync.FloatInPort;
import umlp.backendrte.common.Gem;
import vacuum_gripper.VacuumGripper;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class StepSimulator {
    private VacuumGripper simulator;
    protected Category booleanCategory;
    AtomicInteger step = new AtomicInteger(0);

    public StepSimulator(FFunction vacuumGripperFunction) {
        simulator = new VacuumGripper();
        simulator.setUp();
        simulator.init();

        simulator.getVerticalUp().getObservers().add(new BooleanInPortConsumer((b) -> addInBooleanValue(vacuumGripperFunction, "verticalUp", b)));
        simulator.getVerticalDown().getObservers().add(new BooleanInPortConsumer((b) -> addInBooleanValue(vacuumGripperFunction, "verticalDown", b)));
        simulator.getHorizontalForward().getObservers().add(new BooleanInPortConsumer((b) -> addInBooleanValue(vacuumGripperFunction, "horizontalForward", b)));
        simulator.getHorizontalBack().getObservers().add(new BooleanInPortConsumer((b) -> addInBooleanValue(vacuumGripperFunction, "horizontalBack", b)));
        simulator.getRotationClockwise().getObservers().add(new BooleanInPortConsumer((b) -> addInBooleanValue(vacuumGripperFunction, "rotationClockwise", b)));
        simulator.getRotationCounterclockwise().getObservers().add(new BooleanInPortConsumer((b) -> addInBooleanValue(vacuumGripperFunction, "rotationCounterclockwise", b)));

        simulator.getPositionRotate().getObservers().add(new FloatOutPortConsumer(step, f -> addOutFloatValue(vacuumGripperFunction, "positionRotate", f)));
        simulator.getPositionHorizontal().getObservers().add(new FloatOutPortConsumer(step, f -> addOutFloatValue(vacuumGripperFunction, "positionHorizontal", f)));
        simulator.getPositionVertical().getObservers().add(new FloatOutPortConsumer(step, f -> addOutFloatValue(vacuumGripperFunction, "positionVertical", f)));
    }

    private Category getSimpleTypeCategory(String typeName){
        for (Category category : VacuumGripperDashboardManager.getCategoryList()) {
            if(category.getType().getMctype().equals(typeName)){
                return category;
            }
        }

        throw new IllegalStateException("Can not find category for " + typeName);
    }

    public void addInBooleanValue(FFunction func, String channelName, boolean b) {
        Channel channel = func.getInChannel(channelName).get();
        Gem<FunctionStream> fsGem = channel.getFunctionStreamGem();
        if(!fsGem.isPresent()){
            channel.setFunctionStream(
                    VacuumGripperDashboardManager.functionStreamBuilder()
                    .channel(channel)
                            .category(getSimpleTypeCategory("boolean")).build().get()
            );
        }

        channel.getFunctionStream().addValue(
                VacuumGripperDashboardManager.booleanValueBuilder().content(b).build().get()
        );
    }

    public void addOutFloatValue(FFunction func, String channelName, float f){
        Channel channel = func.getOutChannel(channelName).get();
        if(!channel.getFunctionStreamGem().isPresent()){
            channel.setFunctionStream(
                    VacuumGripperDashboardManager.functionStreamBuilder()
                            .channel(channel)
                            .category(getSimpleTypeCategory("float"))
                            .build().get()
            );
        }

        channel.getFunctionStream().addValue(
                VacuumGripperDashboardManager.floatValueBuilder().content(f).build().get()
        );
    }

    public void step() {
        App app = VacuumGripperDashboardManager.getApp();
        _step(
                simulator,
                app.getSimulationInput(),
                app.getSimulationOutput()
        );
    }

    private void _step(VacuumGripper simulator,
                       VacuumGripperInput input,
                       VacuumGripperOutput output
    ) {
        simulator.getVerticalUp().update(input.getVerticalUp().isContent());
        simulator.getVerticalDown().update(input.getVerticalDown().isContent());
        simulator.getHorizontalForward().update(input.getHorizontalForward().isContent());
        simulator.getHorizontalBack().update(input.getHorizontalBack().isContent());
        simulator.getRotationClockwise().update(input.getRotationClockwise().isContent());
        simulator.getRotationCounterclockwise().update(input.getRotationCounterclockwise().isContent());

        simulator.compute();

        output.getPositionRotate().setContent(simulator.getPositionRotate().getValue());
        output.getPositionHorizontal().setContent(simulator.getPositionHorizontal().getValue());
        output.getPositionVertical().setContent(simulator.getPositionVertical().getValue());

        simulator.tick();
        step.incrementAndGet();
    }

    static class BooleanInPortConsumer extends BooleanInPort {
        final Consumer<Boolean> consumer;

        BooleanInPortConsumer(Consumer<Boolean> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void update(boolean _boolean) {
            consumer.accept(_boolean);
        }
    }

    static class FloatOutPortConsumer extends FloatInPort {
        final Consumer<Float> consumer;
        AtomicInteger step;
        int lastStep = -1;

        FloatOutPortConsumer(AtomicInteger step, Consumer<Float> consumer) {
            this.consumer = consumer;
            this.step = step;
        }

        @Override
        public void update(float _float) {
            if(lastStep < step.get()) {
                consumer.accept(_float);
            }
            lastStep = step.get();
        }
    }
}
