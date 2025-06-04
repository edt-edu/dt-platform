package fischertechnik_gateway.machines.vacuumGripper;

import fischertechnik_gateway.util.PositionMeaning;
import fischertechnik_gateway.util.PositionThreeD;
import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.message.JsonType;
import fischertechnik_gateway.message.Message;
import fischertechnik_gateway.message.Type;
import fischertechnik_gateway.parameters.PositionParameterThreeD;

import java.util.List;

public class Move extends Command {
  private final PositionThreeD start;
  private final PositionThreeD end;

  public Move(String topicName, double timestamp, int outputId, PositionThreeD start, PositionThreeD end) {
    super(topicName, timestamp, createMsg(outputId, start, end), outputId);
    this.start = start;
    this.end = end;
  }

  private static Message createMsg(int outputId, PositionThreeD start, PositionThreeD end) {
    return new Message(
        JsonType.COMMAND,
        Type.VACUUM,
        outputId,
        "MOVE",
        List.of(
            new PositionParameterThreeD(start, PositionMeaning.START),
            new PositionParameterThreeD(end, PositionMeaning.END)
        )
    );
  }
}
