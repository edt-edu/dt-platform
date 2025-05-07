package fischertechnik_gateway.machines.conveyorBelt;

import fischertechnik_gateway.machines.*;
import fischertechnik_gateway.machines.message.Command;
import fischertechnik_gateway.machines.message.JsonType;
import fischertechnik_gateway.machines.message.Message;
import fischertechnik_gateway.machines.message.Type;
import fischertechnik_gateway.machines.parameters.*;

import java.util.List;

public class MoveOut extends Command {
  private final Direction direction;

  public MoveOut(String topicName, double timestamp, int outputId, Direction direction) {
    super(topicName, timestamp, createMsg(outputId, direction));
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
