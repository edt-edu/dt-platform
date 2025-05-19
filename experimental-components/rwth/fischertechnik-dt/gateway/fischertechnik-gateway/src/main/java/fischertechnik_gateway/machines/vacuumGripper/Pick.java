package fischertechnik_gateway.machines.vacuumGripper;

import fischertechnik_gateway.util.PositionMeaning;
import fischertechnik_gateway.util.PositionThreeD;
import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.message.JsonType;
import fischertechnik_gateway.message.Message;
import fischertechnik_gateway.message.Type;
import fischertechnik_gateway.parameters.PositionParameterThreeD;

import java.util.List;

public class Pick extends Command {
  private final PositionThreeD position;

  public Pick(String topicName, double timestamp, int outputId, PositionThreeD position) {
    super(topicName, timestamp, createMsg(outputId, position));
    this.position = position;
  }

  private static Message createMsg(int outputId, PositionThreeD position) {
    return new Message(
        JsonType.COMMAND,
        Type.VACUUM,
        outputId,
        "PICK",
        List.of(new PositionParameterThreeD(position, PositionMeaning.START))
    );
  }
}
