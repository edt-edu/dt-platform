package fischertechnik_gateway.machines.punching;

import fischertechnik_gateway.machines.Machine;
import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.message.CommandStatus;

import java.util.function.Consumer;

public class PunchingMachine extends Machine {
  public PunchingMachine(String topic, Consumer<Command> commandConsumer) {
    super(topic, commandConsumer);
  }

  public CommandStatus<PunchingMachine, Punch> punch() {
    return sendCommand(this, new Punch(topic, System.currentTimeMillis(), msgId++));
  }

  public CommandStatus<PunchingMachine, Stop> stop() {
    return sendCommand(this, new Stop(topic, System.currentTimeMillis(), msgId++));
  }
}
