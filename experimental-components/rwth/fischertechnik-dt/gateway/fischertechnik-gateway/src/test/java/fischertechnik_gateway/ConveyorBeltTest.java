package fischertechnik_gateway;

import fischertechnik_gateway.util.Direction;
import fischertechnik_gateway.machines.conveyorBelt.ConveyorBelt;
import org.junit.jupiter.api.Test;

// TODO: this is not a test, since no asserts
public class ConveyorBeltTest {
  ConveyorBelt conveyorBelt = new ConveyorBelt("Conveyor01", command -> {});

  @Test
  public void testMoveOutCommand(){
    System.out.println(conveyorBelt.moveOut(Direction.FORWARD).getCommand().toJson());
    System.out.println(conveyorBelt.moveOut(Direction.BACKWARD).getCommand().toJson());
  }

  @Test
  public void testMoveNbSteps(){
    System.out.println(conveyorBelt.moveNbSteps(1, Direction.FORWARD).getCommand().toJson());
    System.out.println(conveyorBelt.moveNbSteps(2, Direction.BACKWARD).getCommand().toJson());
  }

  @Test
  public void testMoveToSensor(){
    System.out.println(conveyorBelt.moveToSensor(Direction.FORWARD).getCommand().toJson());
    System.out.println(conveyorBelt.moveToSensor(Direction.BACKWARD).getCommand().toJson());
  }

  @Test
  public void testStop(){
    System.out.println(conveyorBelt.stop().getCommand().toJson());
  }
}
