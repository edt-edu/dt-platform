package fischertechnik_gateway.machines.indexedLine;

import fischertechnik_gateway.machines.Machine;
import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.message.CommandStatus;

import java.util.function.Consumer;

public class IndexedLine extends Machine {
  public IndexedLine(String topic, Consumer<Command> commandConsumer) {
    super(topic, commandConsumer);
  }

  public CommandStatus<IndexedLine, Process1> process1() {
    return sendCommand(this, new Process1(topic, System.currentTimeMillis(), msgId++));
  }

  public CommandStatus<IndexedLine, Stop> stop() {
    return sendCommand(this, new Stop(topic, System.currentTimeMillis(), msgId++));
  }
}
