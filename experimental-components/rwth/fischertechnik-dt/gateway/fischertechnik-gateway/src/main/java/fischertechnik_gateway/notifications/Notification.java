package fischertechnik_gateway.notifications;

import de.monticore.symboltable.serialization.json.JsonObject;
import fischertechnik_gateway.message.JsonType;

public abstract class Notification {
  protected final JsonType jsonType;
  protected final String status;
  protected final String info;


  protected Notification(JsonType jsonType, String status, String info) {
    this.jsonType = jsonType;
    this.status = status;
    this.info = info;
  }

  public static Notification fromJson(JsonObject jsonObject) {
    JsonObject message = jsonObject.getObjectMember("message");
    String jsonType = message.getStringMember("jsonType");
    String status = message.getStringMember("status");
    String info = message.getStringMember("info");

    if (jsonType.equals("COMMAND_FEEDBACK")) {
      return new CommandFeedback(
          status,
          info,
          message.getIntegerMember("commandId")
      );
    } else if (jsonType.equals("MACHINE_FEEDBACK")) {
      return new MachineFeedback(
          jsonObject.getStringMember("topicName"),
          status,
          info
      );
    } else {
      throw new IllegalStateException();
    }
  }

  public boolean isMachineFeedback() {
    return false;
  }

  public boolean isCommandFeedback() {
    return false;
  }

  public MachineFeedback asMachineFeedback() {
    throw new IllegalStateException();
  }

  public CommandFeedback asCommandFeedback() {
    throw new IllegalStateException();
  }

  public String getInfo() {
    return info;
  }

  public JsonType getJsonType() {
    return jsonType;
  }

  public String getStatus() {
    return status;
  }
}
