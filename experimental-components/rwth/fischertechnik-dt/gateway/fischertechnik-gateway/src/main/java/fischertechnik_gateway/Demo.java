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

      Thread.sleep(20000L);

      ConveyorBelt conveyor1 = new ConveyorBelt("ConveyorBelt01", defaultHandler);
      SortingLine sortingLine1 = new SortingLine("SortingLine01", defaultHandler);
      VacuumGripper vacuumGripper1 = new VacuumGripper("VacuumGripper01", defaultHandler);
      VacuumGripper vacuumGripper2 = new VacuumGripper("VacuumGripper02", defaultHandler);
      MultiProcessing multiProcessing1 = new MultiProcessing("MultiProcessing01", defaultHandler);
      HighBay highbay1 = new HighBay("HighBay01", defaultHandler);

      machines.add(conveyor1);
      machines.add(sortingLine1);
      machines.add(vacuumGripper1);
      machines.add(vacuumGripper2);
      machines.add(multiProcessing1);
      machines.add(highbay1);

      vacuumGripper1.setup();
      vacuumGripper1.waitForIdle();
      vacuumGripper2.setup();
      vacuumGripper2.waitForIdle();
      multiProcessing1.setup();
      highbay1.setup();
      // multiProcessing1.waitForIdle();

      if (true) {
        System.out.println("Starting");
        // Move token through the sorting line
        sortingLine1.eject(Color.RED);
        sortingLine1.waitForIdle();

        // Move the token to the conveyor
        PositionThreeD sortingOutputRed = new PositionThreeD(2275, 1400, 1225);
        PositionThreeD conveyorBack = new PositionThreeD(1800, 1050, 1850);

        vacuumGripper2.move(
                sortingOutputRed,
                conveyorBack
        );
        vacuumGripper2.waitForIdle();
      }

      conveyor1.moveToSensor(Direction.FORWARD);

      PositionThreeD safetyPositionConveyorFront = new PositionThreeD(2675, 0, 0);
      PositionThreeD conveyorFront = new PositionThreeD(2675, 1300, 900);
      PositionThreeD highbayInputOutput = new PositionThreeD(1030, 300, 800);
      PositionThreeD inputMultiprocessing = new PositionThreeD(2010, 950, 1850);
      PositionThreeD safetyInputMultiprocessing = new PositionThreeD(2010, 0, 0);
      PositionThreeD outputMultiprocessing = new PositionThreeD(1450, 1150, 2000);
      PositionThreeD safetyOutputMultiprocessing = new PositionThreeD(outputMultiprocessing.getRotation(), 0, 0);

      if(true) {
        vacuumGripper1.gotoposition(safetyPositionConveyorFront);
        vacuumGripper1.waitForIdle();
        vacuumGripper1.move(conveyorFront, inputMultiprocessing);
        vacuumGripper1.waitForIdle();
        vacuumGripper1.gotoposition(safetyInputMultiprocessing);
        vacuumGripper1.waitForIdle();

        multiProcessing1.process1();
        multiProcessing1.waitForIdle();

        vacuumGripper1.gotoposition(safetyOutputMultiprocessing);
        vacuumGripper1.waitForIdle();

        highbay1.pickupFrom(1, 1);
        Thread.sleep(1000L * 30); // TODO: highbay status seems to be broken
        highbay1.waitForIdle();

        vacuumGripper1.move(outputMultiprocessing, highbayInputOutput);
        vacuumGripper1.waitForIdle();
        vacuumGripper1.gotoposition(safetyOutputMultiprocessing);
        vacuumGripper1.waitForIdle();

        highbay1.storeTo(1, 1);
        highbay1.waitForIdle();
      }
    } finally {
      connection.close();
    }
  }
}
