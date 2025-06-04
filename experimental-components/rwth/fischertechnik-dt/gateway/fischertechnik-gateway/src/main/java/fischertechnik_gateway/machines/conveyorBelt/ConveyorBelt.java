package fischertechnik_gateway.machines.conveyorBelt;

import fischertechnik_gateway.message.CommandStatus;
import fischertechnik_gateway.util.Direction;
import fischertechnik_gateway.machines.Machine;
import fischertechnik_gateway.message.Command;

import java.util.function.Consumer;

public class ConveyorBelt extends Machine {
  public ConveyorBelt(String topic, Consumer<Command> commandConsumer) {
    super(topic, commandConsumer);
  }

  public CommandStatus<ConveyorBelt, MoveOut> moveOut(Direction direction) {
    MoveOut moveOut = new MoveOut(topic, System.currentTimeMillis(), msgId++, direction);
    commandConsumer.accept(moveOut);
    setStatus("RUNNING");
    return new CommandStatus<>(this, moveOut, "RUNNING");
  }

  public CommandStatus<ConveyorBelt, MoveNbSteps> moveNbSteps(int number, Direction direction) {
    MoveNbSteps moveNbSteps = new MoveNbSteps(topic, System.currentTimeMillis(), msgId++, number, direction);
    commandConsumer.accept(moveNbSteps);
    setStatus("RUNNING");
    return new CommandStatus<>(this, moveNbSteps, "RUNNING");
  }

  public CommandStatus<ConveyorBelt, MoveToSensor> moveToSensor(Direction direction) {
    MoveToSensor moveToSensor = new MoveToSensor(topic, System.currentTimeMillis(), msgId++, direction);
    commandConsumer.accept(moveToSensor);
    setStatus("RUNNING");
    return new CommandStatus<>(this, moveToSensor, "RUNNING");
  }

  public CommandStatus<ConveyorBelt, Stop> stop() {
    Stop stop = new Stop(topic, System.currentTimeMillis(), msgId++);
    commandConsumer.accept(stop);
    setStatus("RUNNING");
    return new CommandStatus<>(this, stop, "RUNNING");
  }
}
