package fischertechnik_gateway.machines.multiprocessing;

import fischertechnik_gateway.machines.Machine;
import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.message.CommandStatus;

import java.util.function.Consumer;

public class MultiProcessing extends Machine {
  public MultiProcessing(String topic, Consumer<Command> commandConsumer) {
    super(topic, commandConsumer);
  }

  public CommandStatus<MultiProcessing, Process1> process1() {
    return sendCommand(this, new Process1(topic, System.currentTimeMillis(), msgId++));
  }

  public CommandStatus<MultiProcessing, Setup> setup() {
    return sendCommand(this, new Setup(topic, System.currentTimeMillis(), msgId++));
  }

  public CommandStatus<MultiProcessing, Stop> stop() {
    return sendCommand(this, new Stop(topic, System.currentTimeMillis(), msgId++));
  }
}

