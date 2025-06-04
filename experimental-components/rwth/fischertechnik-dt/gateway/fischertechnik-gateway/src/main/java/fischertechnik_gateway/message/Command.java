package fischertechnik_gateway.message;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symboltable.serialization.json.UserJsonString;

public class Command {
  private final String topicName;
  private final double timestamp;
  private final Message message;
  private final int outputId;

  public Command(String topicName, double timestamp, Message message, int outputId) {
    this.topicName = topicName;
    this.timestamp = timestamp;
    this.message = message;
    this.outputId = outputId;
  }

  public JsonObject toJson(){
    JsonPrinter.setSerializeDefaults(true);
    JsonObject res = new JsonObject();

    res.putMember("topicName", new UserJsonString(topicName));
    res.putMember("timestamp", JsonFactory.createNumber(timestamp));
    res.putMember("message",  message.toJson());

    return res;
  }

  public int getOutputId() {
    return outputId;
  }
}
