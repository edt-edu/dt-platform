package fischertechnik_gateway.machines.message;

import de.monticore.symboltable.serialization.json.*;
import fischertechnik_gateway.machines.parameters.Parameter;

import java.util.List;

public class Message {
  private final JsonType jsonType;
  private final Type type;
  private final int outputId;
  private final String name;
  private final List<Parameter> parameters;

  public Message(JsonType jsonType, Type type, int outputId, String name, List<Parameter> parameters) {
    this.jsonType = jsonType;
    this.type = type;
    this.outputId = outputId;
    this.name = name;
    this.parameters = parameters;
  }

  public JsonObject toJson() {
    JsonObject res = new JsonObject();

    res.putMember("jsonType", new UserJsonString(jsonType.name()));
    res.putMember("type", new UserJsonString(type.name()));
    res.putMember("outputId", JsonFactory.createNumber(outputId));
    res.putMember("name", new UserJsonString(name));

    JsonArray p = new JsonArray();
    for (Parameter parameter : parameters) {
      p.add(parameter.toJson());
    }

    res.putMember("parameters", p);
    return res;
  }
}
