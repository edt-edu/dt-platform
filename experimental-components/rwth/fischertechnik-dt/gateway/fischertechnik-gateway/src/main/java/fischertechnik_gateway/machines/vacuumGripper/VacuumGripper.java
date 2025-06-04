package fischertechnik_gateway.machines.vacuumGripper;

import fischertechnik_gateway.machines.Machine;
import fischertechnik_gateway.message.CommandStatus;
import fischertechnik_gateway.util.PositionThreeD;
import fischertechnik_gateway.message.Command;

import java.util.function.Consumer;

public class VacuumGripper extends Machine {
  public VacuumGripper(String topic, Consumer<Command> commandConsumer) {
    super(topic, commandConsumer);
  }

  public CommandStatus<VacuumGripper, GoToPosition> gotoposition(PositionThreeD position) {
    GoToPosition res = new GoToPosition(topic, System.currentTimeMillis(), msgId++, position);
    commandConsumer.accept(res);
    setStatus("RUNNING");
    return new CommandStatus<>(this, res, "RUNNING");
  }

  public CommandStatus<VacuumGripper, Grip> grip() {
    Grip res = new Grip(topic, System.currentTimeMillis(), msgId++);
    commandConsumer.accept(res);
    setStatus("RUNNING");
    return new CommandStatus<>(this, res, "RUNNING");
  }

  public CommandStatus<VacuumGripper, Move> move(PositionThreeD start, PositionThreeD end) {
    Move res = new Move(topic, System.currentTimeMillis(), msgId++, start, end);
    commandConsumer.accept(res);
    setStatus("RUNNING");
    return new CommandStatus<>(this, res, "RUNNING");
  }

  public CommandStatus<VacuumGripper, Pick> pick(PositionThreeD position) {
    Pick res = new Pick(topic, System.currentTimeMillis(), msgId++, position);
    commandConsumer.accept(res);
    setStatus("RUNNING");
    return new CommandStatus<>(this, res, "RUNNING");
  }

  public CommandStatus<VacuumGripper, Place> place(PositionThreeD position) {
    Place res = new Place(topic, System.currentTimeMillis(), msgId++, position);
    commandConsumer.accept(res);
    setStatus("RUNNING");
    return new CommandStatus<>(this, res, "RUNNING");
  }

  public CommandStatus<VacuumGripper, Release> release() {
    Release res = new Release(topic, System.currentTimeMillis(), msgId++);
    commandConsumer.accept(res);
    setStatus("RUNNING");
    return new CommandStatus<>(this, res, "RUNNING");
  }

  public CommandStatus<VacuumGripper, Setup> setup() {
    Setup res = new Setup(topic, System.currentTimeMillis(), msgId++);
    commandConsumer.accept(res);
    setStatus("RUNNING");
    return new CommandStatus<>(this, res, "RUNNING");
  }

  public CommandStatus<VacuumGripper, Stop> stop() {
    Stop res = new Stop(topic, System.currentTimeMillis(), msgId++);
    commandConsumer.accept(res);
    setStatus("RUNNING");
    return new CommandStatus<>(this, res, "RUNNING");
  }
}
