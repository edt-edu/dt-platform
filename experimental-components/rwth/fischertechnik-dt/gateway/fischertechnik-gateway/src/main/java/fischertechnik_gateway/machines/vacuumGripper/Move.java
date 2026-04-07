package fischertechnik_gateway.machines.vacuumGripper;

import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.message.JsonType;
import fischertechnik_gateway.message.Message;
import fischertechnik_gateway.message.Type;
import fischertechnik_gateway.util.Position;
import fischertechnik_gateway.util.PositionMeaning;

import java.util.List;

public class Move extends Command {

  public Move(String topicName, double timestamp, int outputId, Position start, Position end) {
    super(topicName, timestamp, createMsg(outputId, start, end), outputId);
  }

  private static Message createMsg(int outputId, Position start, Position end) {
    return new Message(
        JsonType.COMMAND,
        Type.VACUUM,
        outputId,
        "MOVE",
        List.of(start.toParameter(PositionMeaning.START),
            end.toParameter(PositionMeaning.END)
        )
    );
  }
}
