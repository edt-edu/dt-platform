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
    return sendCommand(this, new Detect(topic, System.currentTimeMillis(), msgId++));
  }

  public CommandStatus<SortingLine, Eject> eject(Color color) {
    return sendCommand(this, new Eject(topic, System.currentTimeMillis(), msgId++, color));
  }

  public CommandStatus<SortingLine, Stop> stop() {
    return sendCommand(this, new Stop(topic, System.currentTimeMillis(), msgId++));
  }
}
