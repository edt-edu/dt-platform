package fischertechnik_gateway.machines.punching;

import fischertechnik_gateway.machines.Machine;
import fischertechnik_gateway.message.Command;

import java.util.function.Consumer;

public class PunchingMachine extends Machine {
    public PunchingMachine(String topic, Consumer<Command> commandConsumer) {
        super(topic, commandConsumer);
    }
}
