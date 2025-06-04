package fischertechnik_gateway.message;

import fischertechnik_gateway.machines.Machine;

import java.util.regex.Pattern;

/**
 * Captures all information about the current status of a Command
 */
public class CommandStatus<MachineType extends Machine, CommandType extends Command> {
  private final MachineType machine;
  private final CommandType command;
  private String status;

  public CommandStatus(MachineType machine, CommandType command, String status) {
    this.machine = machine;
    this.command = command;
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public CommandType getCommand() {
    return command;
  }

  public MachineType getMachine() {
    return machine;
  }

  public void waitForStatus(Pattern target) throws InterruptedException {
    while (!target.matcher(status).matches()){
      Thread.sleep(1L);
    }
  }

  public void waitForDone() throws InterruptedException {
    waitForStatus(Pattern.compile("DONE"));
  }
}
