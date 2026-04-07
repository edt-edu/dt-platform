package fischertechnik_gateway.machines.vacuumGripper;

import fischertechnik_gateway.machines.Machine;
import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.message.CommandStatus;
import fischertechnik_gateway.util.Position;

import java.util.function.Consumer;

public class VacuumGripper extends Machine {
  public VacuumGripper(String topic, Consumer<Command> commandConsumer) {
    super(topic, commandConsumer);
  }

  public CommandStatus<VacuumGripper, GoToPosition> gotoposition(Position position) {
    return sendCommand(this, new GoToPosition(topic, System.currentTimeMillis(), msgId++, position));
  }

  public CommandStatus<VacuumGripper, Grip> grip() {
    return sendCommand(this, new Grip(topic, System.currentTimeMillis(), msgId++));
  }

  public CommandStatus<VacuumGripper, Move> move(Position start, Position end) {
    return sendCommand(this, new Move(topic, System.currentTimeMillis(), msgId++, start, end));
  }

  public CommandStatus<VacuumGripper, Pick> pick(Position position) {
    return sendCommand(this, new Pick(topic, System.currentTimeMillis(), msgId++, position));
  }

  public CommandStatus<VacuumGripper, Place> place(Position position) {
    return sendCommand(this, new Place(topic, System.currentTimeMillis(), msgId++, position));
  }

  public CommandStatus<VacuumGripper, Release> release() {
    return sendCommand(this, new Release(topic, System.currentTimeMillis(), msgId++));
  }

  public CommandStatus<VacuumGripper, Setup> setup() {
    return sendCommand(this, new Setup(topic, System.currentTimeMillis(), msgId++));
  }

  public CommandStatus<VacuumGripper, Stop> stop() {
    return sendCommand(this, new Stop(topic, System.currentTimeMillis(), msgId++));
  }
}
