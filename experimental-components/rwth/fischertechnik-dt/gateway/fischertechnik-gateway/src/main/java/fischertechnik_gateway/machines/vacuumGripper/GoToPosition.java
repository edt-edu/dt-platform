package fischertechnik_gateway.machines.vacuumGripper;

import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.message.JsonType;
import fischertechnik_gateway.message.Message;
import fischertechnik_gateway.message.Type;
import fischertechnik_gateway.util.Position;
import fischertechnik_gateway.util.PositionMeaning;

import java.util.List;

public class GoToPosition extends Command {

  public GoToPosition(String topicName, double timestamp, int outputId, Position position) {
    super(topicName, timestamp, createMsg(outputId, position), outputId);
  }

  private static Message createMsg(int outputId, Position position) {
    return new Message(
        JsonType.COMMAND,
        Type.VACUUM,
        outputId,
        "GO_TO_POSITION",
        List.of(position.toParameter(PositionMeaning.START))
    );
  }
}
