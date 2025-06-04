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
        return sendCommand(this, new Setup(topic, System.currentTimeMillis(), msgId++));
    }

    public CommandStatus<HighBay, StoreTo> storeTo(int row, int column) {
        return sendCommand(this, new StoreTo(topic, System.currentTimeMillis(), msgId++, row, column));
    }

    public CommandStatus<HighBay, PickupFrom> pickupFrom(int row, int column) {
        return sendCommand(this, new PickupFrom(topic, System.currentTimeMillis(), msgId++, row, column));
    }
}
