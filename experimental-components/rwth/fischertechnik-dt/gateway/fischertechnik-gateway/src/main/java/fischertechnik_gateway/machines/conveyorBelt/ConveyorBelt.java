package fischertechnik_gateway.machines.conveyorBelt;

import fischertechnik_gateway.machines.Direction;
import fischertechnik_gateway.machines.Machine;

// TODO: in MontiArc/SysMLv2 the messages might not be relevant,
//  maybe change return type to void and add a Consumer<Message> for outgoing messages
public class ConveyorBelt extends Machine {
  public ConveyorBelt(String topic) {
    super(topic);
  }

  public MoveOut moveOut(Direction direction){
    return new MoveOut(topic, System.currentTimeMillis(), msgId++, direction);
  }

  public MoveNbSteps moveNbSteps(int number, Direction direction){
    return new MoveNbSteps(topic, System.currentTimeMillis(), msgId++, number, direction);
  }

  public MoveToSensor moveToSensor(Direction direction){
    return new MoveToSensor(topic, System.currentTimeMillis(), msgId++, direction);
  }

  public Stop stop(){
    return new Stop(topic, System.currentTimeMillis(), msgId++);
  }
}
