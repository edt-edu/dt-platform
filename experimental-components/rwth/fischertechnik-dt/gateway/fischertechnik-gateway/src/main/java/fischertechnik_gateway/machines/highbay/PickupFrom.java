package fischertechnik_gateway.machines.highbay;

import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.message.JsonType;
import fischertechnik_gateway.message.Message;
import fischertechnik_gateway.message.Type;
import fischertechnik_gateway.parameters.NumberNaturalParameter;

import java.util.List;

public class PickupFrom extends Command {
    public PickupFrom(String topicName, double timestamp, int outputId, int row, int column) {
        super(topicName, timestamp, createMsg(outputId, row, column));
    }

    private static Message createMsg(int outputId, int row, int column) {
        return new Message(
                JsonType.COMMAND,
                Type.WAREHOUSE,
                outputId,
                "PICKUP_FROM",
                List.of(
                        new NumberNaturalParameter(row),
                        new NumberNaturalParameter(column)
                )
        );
    }
}
