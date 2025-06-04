package fischertechnik_gateway.machines.conveyorBelt;

import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.message.JsonType;
import fischertechnik_gateway.message.Message;
import fischertechnik_gateway.message.Type;
import fischertechnik_gateway.parameters.DirectionParameter;
import fischertechnik_gateway.util.Direction;

import java.util.List;

public class MoveOut extends Command {
  private final Direction direction;

  public MoveOut(String topicName, double timestamp, int outputId, Direction direction) {
    super(topicName, timestamp, createMsg(outputId, direction) ,outputId);
    this.direction = direction;
  }

  private static Message createMsg(int outputId, Direction direction) {
    return new Message(
        JsonType.COMMAND,
        Type.CONVEYOR,
        outputId,
        "MOVE_OUT",
        List.of(
            new DirectionParameter(direction)
        )
    );
  }
}
