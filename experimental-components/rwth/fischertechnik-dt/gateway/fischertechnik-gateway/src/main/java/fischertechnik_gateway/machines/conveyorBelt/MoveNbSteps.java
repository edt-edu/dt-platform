package fischertechnik_gateway.machines.conveyorBelt;

import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.message.JsonType;
import fischertechnik_gateway.message.Message;
import fischertechnik_gateway.message.Type;
import fischertechnik_gateway.parameters.DirectionParameter;
import fischertechnik_gateway.parameters.NumberNaturalParameter;
import fischertechnik_gateway.util.Direction;

import java.util.List;

public class MoveNbSteps extends Command {
  private final int number;
  private final Direction direction;

  public MoveNbSteps(String topicName, double timestamp, int outputId, int number, Direction direction) {
    super(topicName, timestamp, createMsg(outputId, number, direction), outputId);
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
