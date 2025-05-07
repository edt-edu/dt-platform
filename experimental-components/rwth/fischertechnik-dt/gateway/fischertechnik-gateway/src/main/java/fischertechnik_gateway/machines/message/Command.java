package fischertechnik_gateway.machines.message;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symboltable.serialization.json.UserJsonString;

public class Command {
  private final String topicName;
  private final double timestamp;
  private final Message message;

  public Command(String topicName, double timestamp, Message message) {
    this.topicName = topicName;
    this.timestamp = timestamp;
    this.message = message;
  }

  public JsonObject toJson(){
    JsonObject res = new JsonObject();

    res.putMember("topicName", new UserJsonString(topicName));
    res.putMember("timestamp", JsonFactory.createNumber(timestamp));
    res.putMember("message",  message.toJson());

    return res;
  }
}
