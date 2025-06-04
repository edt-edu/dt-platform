package fischertechnik_gateway.machines.punching;

import fischertechnik_gateway.machines.Machine;
import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.message.CommandStatus;

import java.util.function.Consumer;

public class PunchingMachine extends Machine {
  public PunchingMachine(String topic, Consumer<Command> commandConsumer) {
    super(topic, commandConsumer);
  }

  public  CommandStatus<PunchingMachine, Punch> punch() {
    Punch res = new Punch(topic, System.currentTimeMillis(), msgId++);
    commandConsumer.accept(res);
    setStatus("RUNNING");
    return new CommandStatus<>(this, res, "RUNNING");
  }

  public CommandStatus<PunchingMachine, Stop> stop() {
    Stop res = new Stop(topic, System.currentTimeMillis(), msgId++);
    commandConsumer.accept(res);
    setStatus("RUNNING");
    return new CommandStatus<>(this, res, "RUNNING");
  }
}
