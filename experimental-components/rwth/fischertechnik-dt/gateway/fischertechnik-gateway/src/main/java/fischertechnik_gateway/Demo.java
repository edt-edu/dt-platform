package fischertechnik_gateway;

import fischertechnik_gateway.machines.Direction;
import fischertechnik_gateway.machines.conveyorBelt.ConveyorBelt;
import fischertechnik_gateway.machines.tcp.TcpPLCConnection;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Demo {
  public static void main(String[] args) throws InterruptedException {
    TcpPLCConnection connection = new TcpPLCConnection(
        new InetSocketAddress("192.168.178.201", 6001),
        new InetSocketAddress("192.168.178.201", 6011)
    );


    ConveyorBelt conveyor = new ConveyorBelt("Conveyor01", command -> {
      try {
        connection.sendCommand(command);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });


    conveyor.moveOut(Direction.FORWARD);
    Thread.sleep(2000L);
    conveyor.stop();
    Thread.sleep(2000L);
    conveyor.moveOut(Direction.BACKWARD);
    Thread.sleep(2000L);
    conveyor.stop();
  }
}
