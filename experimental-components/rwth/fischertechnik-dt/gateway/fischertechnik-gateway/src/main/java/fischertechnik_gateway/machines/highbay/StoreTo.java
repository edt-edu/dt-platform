package fischertechnik_gateway.machines.highbay;

import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.message.JsonType;
import fischertechnik_gateway.message.Message;
import fischertechnik_gateway.message.Type;
import fischertechnik_gateway.parameters.NumberNaturalParameter;

import java.util.List;

public class StoreTo extends Command {
    public StoreTo(String topicName, double timestamp, int outputId, int row, int column) {
        super(topicName, timestamp, createMsg(outputId, row, column), outputId);
    }

    private static Message createMsg(int outputId, int row, int column) {
        return new Message(
                JsonType.COMMAND,
                Type.WAREHOUSE,
                outputId,
                "STORE_TO",
                List.of(
                        new NumberNaturalParameter(row),
                        new NumberNaturalParameter(column)
                )
        );
    }
}
