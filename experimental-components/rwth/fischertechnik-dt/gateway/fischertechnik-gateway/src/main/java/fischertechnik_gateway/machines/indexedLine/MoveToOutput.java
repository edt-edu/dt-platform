package fischertechnik_gateway.machines.indexedLine;

import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.message.JsonType;
import fischertechnik_gateway.message.Message;
import fischertechnik_gateway.message.Type;

import java.util.List;

public class MoveToOutput extends Command {
  public MoveToOutput(String topicName, double timestamp, int outputId) {
    super(topicName, timestamp, createMsg(outputId), outputId);
  }

  private static Message createMsg(int outputId) {
    return new Message(
        JsonType.COMMAND,
        Type.INDEXEDLINE,
        outputId,
        "MOVE_TO_OUTPUT",
        List.of()
    );
  }
}
