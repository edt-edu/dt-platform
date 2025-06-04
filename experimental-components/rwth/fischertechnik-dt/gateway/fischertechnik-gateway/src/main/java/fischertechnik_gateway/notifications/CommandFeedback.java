package fischertechnik_gateway.notifications;

import fischertechnik_gateway.message.JsonType;

public class CommandFeedback extends Notification {
    protected final int commandId;

  protected CommandFeedback(String status, String info, int commandId) {
    super(JsonType.COMMAND_FEEDBACK, status, info);
    this.commandId = commandId;
  }

  public int getCommandId() {
    return commandId;
  }

  @Override
    public boolean isCommandFeedback() {
        return true;
    }

    @Override
    public CommandFeedback asCommandFeedback() {
        return this;
    }

    @Override
    public String toString() {
        return "CommandFeedback{" +
                "commandId=" + commandId +
                ", jsonType=" + jsonType +
                ", status='" + status + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}
