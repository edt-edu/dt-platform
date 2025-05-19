package fischertechnik_gateway.machines;

import fischertechnik_gateway.message.Command;

import java.util.function.Consumer;

public class Machine {
  protected static int msgId = 0;

  protected final String topic;
  protected final Consumer<Command> commandConsumer;
  // TODO: Replace with Enum!
  protected String status = "UNKOWN";

  public Machine(String topic, Consumer<Command> commandConsumer) {
    this.topic = topic;
    this.commandConsumer = commandConsumer;
  }

  // TODO: we could also use command feedback with Future
  public void waitForIdle() throws InterruptedException {
    while(!status.contains("IDLE")){
      Thread.sleep(1L);
    }
  }

  public void setStatus(String status){
    this.status = status;
  }

  public String getTopic() {
    return topic;
  }
}
