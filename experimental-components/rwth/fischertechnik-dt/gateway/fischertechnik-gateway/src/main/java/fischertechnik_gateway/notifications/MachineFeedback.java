package fischertechnik_gateway.notifications;

import fischertechnik_gateway.message.JsonType;

public class MachineFeedback extends Notification {
  protected final String topicName;

  protected MachineFeedback(String topicName, String status, String info) {
    super(JsonType.MACHINE_FEEDBACK, status, info);
    this.topicName = topicName;
  }

  public String getTopicName() {
    return topicName;
  }

  @Override
    public boolean isMachineFeedback() {
        return true;
    }

    @Override
    public MachineFeedback asMachineFeedback() {
        return this;
    }

  @Override
  public String toString() {
    return "MachineFeedback{" +
            "topicName='" + topicName + '\'' +
            ", jsonType=" + jsonType +
            ", status='" + status + '\'' +
            ", info='" + info + '\'' +
            '}';
  }
}
