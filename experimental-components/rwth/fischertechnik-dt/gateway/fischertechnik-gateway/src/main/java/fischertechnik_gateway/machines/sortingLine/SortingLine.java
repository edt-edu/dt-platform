package fischertechnik_gateway.machines.sortingLine;

import fischertechnik_gateway.util.Color;
import fischertechnik_gateway.machines.Machine;
import fischertechnik_gateway.message.Command;

import java.util.function.Consumer;

public class SortingLine extends Machine {
  public SortingLine(String topic, Consumer<Command> commandConsumer) {
    super(topic, commandConsumer);
  }

  public Detect detect(){
    Detect res = new Detect(topic, System.currentTimeMillis(), msgId++);
    commandConsumer.accept(res);
	setStatus("RUNNING");
    return res;
  }

  public Eject eject(Color color){
    Eject res = new Eject(topic, System.currentTimeMillis(), msgId++, color);
    commandConsumer.accept(res);
	setStatus("RUNNING");
    return res;
  }

  public Stop stop(){
    Stop res = new Stop(topic, System.currentTimeMillis(), msgId++);
    commandConsumer.accept(res);
	setStatus("RUNNING");
    return res;
  }
}
