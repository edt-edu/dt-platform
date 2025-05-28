package fischertechnik_gateway.machines.indexedLine;

import fischertechnik_gateway.machines.Machine;
import fischertechnik_gateway.message.Command;

import java.util.function.Consumer;

public class IndexedLine extends Machine {
  public IndexedLine(String topic, Consumer<Command> commandConsumer) {
    super(topic, commandConsumer);
  }

  public Process1 process1() {
    Process1 res = new Process1(topic, System.currentTimeMillis(), msgId++);
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
