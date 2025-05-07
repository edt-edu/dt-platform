package fischertechnik_gateway.machines;

import java.util.function.Supplier;

public class Machine {
  protected final String topic;
  protected int msgId = 0;

  public Machine(String topic) {
    this.topic = topic;
  }
}
