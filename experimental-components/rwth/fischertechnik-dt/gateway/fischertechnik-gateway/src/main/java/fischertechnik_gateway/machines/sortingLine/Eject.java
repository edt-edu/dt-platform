package fischertechnik_gateway.machines.sortingLine;

import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.message.JsonType;
import fischertechnik_gateway.message.Message;
import fischertechnik_gateway.message.Type;
import fischertechnik_gateway.parameters.ColorParameter;
import fischertechnik_gateway.util.Color;

import java.util.List;

public class Eject extends Command {
  private final Color color;

  public Eject(String topicName, double timestamp, int outputId, Color color) {
    super(topicName, timestamp, createMsg(outputId, color), outputId);
    this.color = color;
  }

  private static Message createMsg(int outputId, Color color) {
    return new Message(
        JsonType.COMMAND,
        Type.SORTING,
        outputId,
        "EJECT",
        List.of(
            new ColorParameter(color)
        )
    );
  }
}
