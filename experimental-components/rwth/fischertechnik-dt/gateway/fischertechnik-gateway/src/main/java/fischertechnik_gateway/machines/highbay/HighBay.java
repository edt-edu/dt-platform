package fischertechnik_gateway.machines.highbay;

import fischertechnik_gateway.machines.Machine;
import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.message.CommandStatus;

import java.util.function.Consumer;

public class HighBay extends Machine {
    public HighBay(String topic, Consumer<Command> commandConsumer) {
        super(topic, commandConsumer);
    }

    public CommandStatus<HighBay, Setup> setup() {
        Setup res = new Setup(topic, System.currentTimeMillis(), msgId++);
        commandConsumer.accept(res);
        setStatus("RUNNING");
        return new CommandStatus<>(this, res, "RUNNING");
    }

    public CommandStatus<HighBay, StoreTo> storeTo(int row, int column) {
        StoreTo res = new StoreTo(topic, System.currentTimeMillis(), msgId++, row, column);
        commandConsumer.accept(res);
        setStatus("RUNNING");
        return new CommandStatus<>(this, res, "RUNNING");
    }

    public CommandStatus<HighBay, PickupFrom> pickupFrom(int row, int column) {
        PickupFrom res = new PickupFrom(topic, System.currentTimeMillis(), msgId++, row, column);
        commandConsumer.accept(res);
        setStatus("RUNNING");
        return new CommandStatus<>(this, res, "RUNNING");
    }
}
