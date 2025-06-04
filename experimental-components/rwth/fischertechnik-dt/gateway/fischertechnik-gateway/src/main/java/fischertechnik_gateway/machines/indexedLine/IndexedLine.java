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
    Process1 res = new Process1(topic, System.currentTimeMillis(), msgId++);
    commandConsumer.accept(res);
    setStatus("RUNNING");
    return new CommandStatus<>(this, res, "RUNNING");
  }

  public CommandStatus<IndexedLine, Stop> stop() {
    Stop res = new Stop(topic, System.currentTimeMillis(), msgId++);
    commandConsumer.accept(res);
    setStatus("RUNNING");
    return new CommandStatus<>(this, res, "RUNNING");
  }
}
