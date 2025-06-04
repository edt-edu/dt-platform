package fischertechnik_gateway.machines;

import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.message.CommandStatus;
import fischertechnik_gateway.notifications.CommandFeedback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class Machine {
  protected static int msgId = 0;

  protected final String topic;
  protected final Consumer<Command> commandConsumer;
  // TODO: Replace with Enum!
  protected String status = "UNKOWN";
  protected Map<Integer, CommandStatus<?,?>> sendCommands = new HashMap<>();

  public Machine(String topic, Consumer<Command> commandConsumer) {
    this.topic = topic;
    this.commandConsumer = commandConsumer;
  }

  // TODO: we could also use command feedback with Future
  public void waitForStatus(Pattern target) throws InterruptedException {
    while (!target.matcher(status).matches()){
      Thread.sleep(1L);
    }
  }

  public void waitForIdle() throws InterruptedException {
    waitForStatus(Pattern.compile(".*IDLE.*"));
  }

  public void setStatus(String status){
    this.status = status;
  }

  public String getTopic() {
    return topic;
  }

  public boolean updateCommandStatus(CommandFeedback commandFeedback){
    if(sendCommands.containsKey(commandFeedback.getCommandId())){
      String status = commandFeedback.getStatus();
      sendCommands.get(commandFeedback.getCommandId()).setStatus(status);
      return true;
    } else {
      return false;
    }
  }

  // Parameter machine is only needed to properly use generics, and needs to always be called via sendCommand(this, ...)
  protected <MachineType extends Machine, CommandType extends Command> CommandStatus<MachineType, CommandType> sendCommand(MachineType machine, CommandType command){
    setStatus("RUNNING");
    CommandStatus<MachineType, CommandType> res = new CommandStatus<>(machine, command, "RUNNING");
    sendCommands.put(command.getOutputId(), res);

    commandConsumer.accept(command);

    return res;
  }
}
