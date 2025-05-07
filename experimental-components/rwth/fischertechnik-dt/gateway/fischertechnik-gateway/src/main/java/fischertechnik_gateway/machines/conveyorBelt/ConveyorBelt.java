package fischertechnik_gateway.machines.conveyorBelt;

import fischertechnik_gateway.machines.Direction;
import fischertechnik_gateway.machines.Machine;
import fischertechnik_gateway.machines.message.Command;

import java.util.function.Consumer;

public class ConveyorBelt extends Machine {
  private final Consumer<Command> messageChannel;

  public ConveyorBelt(String topic, Consumer<Command> messageChannel) {
    super(topic);
    this.messageChannel = messageChannel;
  }

  public MoveOut moveOut(Direction direction){
    MoveOut moveOut = new MoveOut(topic, System.currentTimeMillis(), msgId++, direction);
    messageChannel.accept(moveOut);
    return moveOut;
  }

  public MoveNbSteps moveNbSteps(int number, Direction direction){
    MoveNbSteps moveNbSteps = new MoveNbSteps(topic, System.currentTimeMillis(), msgId++, number, direction);
    messageChannel.accept(moveNbSteps);
    return moveNbSteps;
  }

  public MoveToSensor moveToSensor(Direction direction){
    MoveToSensor moveToSensor = new MoveToSensor(topic, System.currentTimeMillis(), msgId++, direction);
    messageChannel.accept(moveToSensor);
    return moveToSensor;
  }

  public Stop stop(){
    Stop stop = new Stop(topic, System.currentTimeMillis(), msgId++);
    messageChannel.accept(stop);
    return stop;
  }
}
