package fischertechnik_gateway.machines.conveyorBelt;

import fischertechnik_gateway.machines.*;
import fischertechnik_gateway.machines.message.Command;
import fischertechnik_gateway.machines.message.JsonType;
import fischertechnik_gateway.machines.message.Message;
import fischertechnik_gateway.machines.message.Type;
import fischertechnik_gateway.machines.parameters.*;

import java.util.List;

public class MoveNbSteps extends Command {
  private final int number;
  private final Direction direction;

  public MoveNbSteps(String topicName, double timestamp, int outputId, int number, Direction direction) {
    super(topicName, timestamp, createMsg(outputId, number, direction));
    this.number = number;
    this.direction = direction;
  }

  private static Message createMsg(int outputId, int number, Direction direction) {
    return new Message(
        JsonType.COMMAND,
        Type.CONVEYOR,
        outputId,
        "MOVE_NB_STEPS",
        List.of(
            new NumberNaturalParameter(number),
            new DirectionParameter(direction)
        )
    );
  }
}
