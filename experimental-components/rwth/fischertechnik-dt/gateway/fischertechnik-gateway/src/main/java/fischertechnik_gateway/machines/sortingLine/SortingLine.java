package fischertechnik_gateway.machines.sortingLine;

import fischertechnik_gateway.message.CommandStatus;
import fischertechnik_gateway.util.Color;
import fischertechnik_gateway.machines.Machine;
import fischertechnik_gateway.message.Command;

import java.util.function.Consumer;

public class SortingLine extends Machine {
  public SortingLine(String topic, Consumer<Command> commandConsumer) {
    super(topic, commandConsumer);
  }

  public CommandStatus<SortingLine, Detect> detect() {
    Detect res = new Detect(topic, System.currentTimeMillis(), msgId++);
    commandConsumer.accept(res);
    setStatus("RUNNING");
    return new CommandStatus<>(this, res, "RUNNING");
  }

  public CommandStatus<SortingLine, Eject> eject(Color color) {
    Eject res = new Eject(topic, System.currentTimeMillis(), msgId++, color);
    commandConsumer.accept(res);
    setStatus("RUNNING");
    return new CommandStatus<>(this, res, "RUNNING");
  }

  public CommandStatus<SortingLine, Stop> stop() {
    Stop res = new Stop(topic, System.currentTimeMillis(), msgId++);
    commandConsumer.accept(res);
    setStatus("RUNNING");
    return new CommandStatus<>(this, res, "RUNNING");
  }
}
