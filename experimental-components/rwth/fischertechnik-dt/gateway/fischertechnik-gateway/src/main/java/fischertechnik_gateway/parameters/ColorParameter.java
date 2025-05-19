package fischertechnik_gateway.parameters;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symboltable.serialization.json.UserJsonString;
import fischertechnik_gateway.util.Color;

public class ColorParameter implements Parameter {
  private final Color color;

  public ColorParameter(Color color) {
    this.color = color;
  }

  @Override
  public JsonObject toJson() {
    JsonObject res = new JsonObject();
    JsonObject passable = new JsonObject();
    passable.putMember("color", new UserJsonString(color.name()));

    res.putMember("passableType", new UserJsonString(PassableType.COLOR.name()));
    res.putMember("passable", passable);
    return res;
  }
}