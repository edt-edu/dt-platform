package fischertechnik_gateway.machines.conveyorBelt;

import fischertechnik_gateway.machines.message.Command;
import fischertechnik_gateway.machines.message.JsonType;
import fischertechnik_gateway.machines.message.Message;
import fischertechnik_gateway.machines.message.Type;

import java.util.List;

public class Stop extends Command {
  public Stop(String topicName, double timestamp, int outputId) {
    super(topicName, timestamp, createMsg(outputId));
  }

  private static Message createMsg(int outputId) {
    return new Message(
        JsonType.COMMAND,
        Type.CONVEYOR,
        outputId,
        "STOP",
        List.of()
    );
  }
}
