package fischertechnik_gateway.parameters;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symboltable.serialization.json.UserJsonString;
import fischertechnik_gateway.message.JsonFactory;

public class NumberNaturalParameter implements Parameter {
  private final int number;

  public NumberNaturalParameter(int number) {
    this.number = number;
  }

  @Override
  public JsonObject toJson() {
    JsonObject res = new JsonObject();
    JsonObject passable = new JsonObject();
    passable.putMember("number", JsonFactory.createNumber(number));

    res.putMember("passableType", new UserJsonString("NUMBERNATURAL"));
    res.putMember("passable", passable);
    return res;
  }
}
