package fischertechnik_gateway.machines.conveyorBelt;

import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.message.JsonType;
import fischertechnik_gateway.message.Message;
import fischertechnik_gateway.message.Type;
import fischertechnik_gateway.parameters.DirectionParameter;
import fischertechnik_gateway.util.Direction;

import java.util.List;

public class MoveToSensor extends Command {
  private final Direction direction;

  public MoveToSensor(String topicName, double timestamp, int outputId, Direction direction) {
    super(topicName, timestamp, createMsg(outputId, direction));
    this.direction = direction;
  }

  private static Message createMsg(int outputId, Direction direction) {
    return new Message(
        JsonType.COMMAND,
        Type.CONVEYOR,
        outputId,
        "MOVE_TO_SENSOR",
        List.of(
            new DirectionParameter(direction)
        )
    );
  }
}
