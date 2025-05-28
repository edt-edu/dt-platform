package fischertechnik_gateway.machines.punching;

import fischertechnik_gateway.machines.Machine;
import fischertechnik_gateway.message.Command;

import java.util.function.Consumer;

public class PunchingMachine extends Machine {
  public PunchingMachine(String topic, Consumer<Command> commandConsumer) {
    super(topic, commandConsumer);
  }

  public Punch punch() {
    Punch res = new Punch(topic, System.currentTimeMillis(), msgId++);
    commandConsumer.accept(res);
    setStatus("RUNNING");
    return res;
  }

  public Stop stop() {
    Stop res = new Stop(topic, System.currentTimeMillis(), msgId++);
    commandConsumer.accept(res);
    setStatus("RUNNING");
    return res;
  }
}
