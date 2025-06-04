package fischertechnik_gateway.machines.vacuumGripper;

import fischertechnik_gateway.util.PositionMeaning;
import fischertechnik_gateway.util.PositionThreeD;
import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.parameters.PositionParameterThreeD;
import fischertechnik_gateway.message.JsonType;
import fischertechnik_gateway.message.Message;
import fischertechnik_gateway.message.Type;

import java.util.List;

public class GoToPosition extends Command {
  private final PositionThreeD position;

  public GoToPosition(String topicName, double timestamp, int outputId, PositionThreeD position) {
    super(topicName, timestamp, createMsg(outputId, position), outputId);
    this.position = position;
  }

  private static Message createMsg(int outputId, PositionThreeD position) {
    return new Message(
        JsonType.COMMAND,
        Type.VACUUM,
        outputId,
        "GO_TO_POSITION",
        List.of(new PositionParameterThreeD(position, PositionMeaning.START))
    );
  }
}
