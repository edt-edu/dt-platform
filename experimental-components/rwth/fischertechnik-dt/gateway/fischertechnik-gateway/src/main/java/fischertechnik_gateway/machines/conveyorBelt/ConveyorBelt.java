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
    return sendCommand(this, new MoveOut(topic, System.currentTimeMillis(), msgId++, direction));
  }

  public CommandStatus<ConveyorBelt, MoveNbSteps> moveNbSteps(int number, Direction direction) {
    return sendCommand(this, new MoveNbSteps(topic, System.currentTimeMillis(), msgId++, number, direction));
  }

  public CommandStatus<ConveyorBelt, MoveToSensor> moveToSensor(Direction direction) {
    return sendCommand(this, new MoveToSensor(topic, System.currentTimeMillis(), msgId++, direction));
  }

  public CommandStatus<ConveyorBelt, Stop> stop() {
    return sendCommand(this, new Stop(topic, System.currentTimeMillis(), msgId++));
  }
}
