package fischertechnik_gateway;

import de.se_rwth.commons.logging.Log;
import fischertechnik_gateway.machines.highbay.HighBay;
import fischertechnik_gateway.util.Color;
import fischertechnik_gateway.util.Direction;
import fischertechnik_gateway.machines.Machine;
import fischertechnik_gateway.util.PositionThreeD;
import fischertechnik_gateway.machines.conveyorBelt.ConveyorBelt;
import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.machines.multiprocessing.MultiProcessing;
import fischertechnik_gateway.machines.sortingLine.SortingLine;
import fischertechnik_gateway.tcp.TcpPLCConnection;
import fischertechnik_gateway.machines.vacuumGripper.VacuumGripper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Demo {
  public static void main(String[] args) throws InterruptedException, IOException {
    Log.enableFailQuick(false);
    TcpPLCConnection connection = new TcpPLCConnection(
            new InetSocketAddress("192.168.178.201", 6001),
            new InetSocketAddress("192.168.178.201", 6011)
    );

    try {
      Consumer<Command> defaultHandler = command -> {
        try {
          System.out.println("Sending: " + command.toJson().toString());
          connection.sendCommand(command);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      };


      List<Machine> machines = new ArrayList<>();

      connection.addNotificationListener(notification -> {
        System.out.println("Received notification:" + notification);
        if (notification.isMachineFeedback()) {

          for (Machine machine : machines) {
            if (machine.getTopic().equals(notification.asMachineFeedback().getTopicName())) {
              machine.setStatus(notification.getStatus());
            }
          }
        } else if (notification.isCommandFeedback()) {
          for (Machine machine : machines) {
            machine.updateCommandStatus(notification.asCommandFeedback());
          }
        }
      });

      connection.connect();

      // Thread.sleep(20000L);

      ConveyorBelt conveyor1 = new ConveyorBelt("I1ConveyorBelt01", defaultHandler);
      SortingLine sortingLine1 = new SortingLine("I1SortingLine01", defaultHandler);
      VacuumGripper vacuumGripper1 = new VacuumGripper("I1VacuumGripper01", defaultHandler);
      VacuumGripper vacuumGripper2 = new VacuumGripper("I1VacuumGripper02", defaultHandler);
      MultiProcessing multiProcessing1 = new MultiProcessing("I1MultiProcessing01", defaultHandler);
      HighBay highbay1 = new HighBay("I1HighBay01", defaultHandler);

      machines.add(conveyor1);
      machines.add(sortingLine1);
      machines.add(vacuumGripper1);
      machines.add(vacuumGripper2);
      machines.add(multiProcessing1);
      machines.add(highbay1);

      multiProcessing1.setup().waitForDone();
      vacuumGripper1.setup().waitForDone();
      vacuumGripper2.setup().waitForDone();
      highbay1.setup().waitForDone();

      if (true) {
        System.out.println("Starting");
        // Move token through the sorting line
        sortingLine1.eject(Color.RED).waitForDone();

        // Move the token to the conveyor
        PositionThreeD sortingOutputRed = new PositionThreeD(2275, 1400, 1225);
        PositionThreeD conveyorBack = new PositionThreeD(1800, 1050, 1225);

        vacuumGripper2.move(sortingOutputRed, conveyorBack).waitForDone();
      }

      conveyor1.moveToSensor(Direction.FORWARD);

      PositionThreeD safetyPositionConveyorFront = new PositionThreeD(1180, 0, 0);
      PositionThreeD conveyorFront = new PositionThreeD(1180, 1300, 1750);
      PositionThreeD highbayInputOutput = new PositionThreeD(2520, 400, 1000);
      PositionThreeD safetyInputMultiprocessing = new PositionThreeD(270, 0, 0);
      PositionThreeD inputMultiprocessing = new PositionThreeD(270, 1000, 240);
      PositionThreeD safetyOutputMultiprocessing = new PositionThreeD(3000, 0, 0);
      PositionThreeD outputMultiprocessing = new PositionThreeD(3000, 1200, 1350);

      if(true) {
        vacuumGripper1.gotoposition(safetyPositionConveyorFront).waitForDone();
        vacuumGripper1.move(conveyorFront, inputMultiprocessing).waitForDone();
        vacuumGripper1.gotoposition(safetyInputMultiprocessing).waitForDone();
        vacuumGripper1.gotoposition(safetyPositionConveyorFront).waitForDone();

        multiProcessing1.process1().waitForDone();

        vacuumGripper1.gotoposition(safetyOutputMultiprocessing).waitForDone();

        highbay1.pickupFrom(1, 1).waitForDone();
//        Thread.sleep(1000L * 30); // TODO: highbay status seems to be broken
//        highbay1.waitForIdle();

        vacuumGripper1.move(outputMultiprocessing, highbayInputOutput).waitForDone();
        vacuumGripper1.gotoposition(safetyOutputMultiprocessing).waitForDone();

        highbay1.storeTo(1, 1).waitForDone();
      }
    } finally {
      connection.close();
    }
  }
}
