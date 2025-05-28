package fischertechnik_gateway.machines.indexedLine;

import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.message.JsonType;
import fischertechnik_gateway.message.Message;
import fischertechnik_gateway.message.Type;

import java.util.List;

public class Process1 extends Command {
  public Process1(String topicName, double timestamp, int outputId) {
    super(topicName, timestamp, createMsg(outputId));
  }

  private static Message createMsg(int outputId) {
    return new Message(
        JsonType.COMMAND,
        Type.INDEXEDLINE,
        outputId,
        "PROCESS1",
        List.of()
    );
  }
}
