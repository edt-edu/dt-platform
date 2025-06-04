package fischertechnik_gateway.machines.vacuumGripper;

import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.message.JsonType;
import fischertechnik_gateway.message.Message;
import fischertechnik_gateway.message.Type;

import java.util.List;

public class Grip extends Command {
  public Grip(String topicName, double timestamp, int outputId) {
    super(topicName, timestamp, createMsg(outputId), outputId);
  }

  private static Message createMsg(int outputId) {
    return new Message(
        JsonType.COMMAND,
        Type.VACUUM,
        outputId,
        "GRIP",
        List.of()
    );
  }
}
