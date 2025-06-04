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
    Process1 res = new Process1(topic, System.currentTimeMillis(), msgId++);
    commandConsumer.accept(res);
    setStatus("RUNNING");
    return new CommandStatus<>(this, res, "RUNNING");
  }

  public CommandStatus<MultiProcessing, Setup> setup() {
    Setup res = new Setup(topic, System.currentTimeMillis(), msgId++);
    commandConsumer.accept(res);
    setStatus("RUNNING");
    return new CommandStatus<>(this, res, "RUNNING");
  }

  public CommandStatus<MultiProcessing, Stop> stop() {
    Stop res = new Stop(topic, System.currentTimeMillis(), msgId++);
    commandConsumer.accept(res);
    setStatus("RUNNING");
    return new CommandStatus<>(this, res, "RUNNING");
  }
}

