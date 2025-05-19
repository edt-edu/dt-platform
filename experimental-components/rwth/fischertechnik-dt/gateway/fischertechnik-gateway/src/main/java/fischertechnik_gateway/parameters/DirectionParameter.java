package fischertechnik_gateway.parameters;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symboltable.serialization.json.UserJsonString;
import fischertechnik_gateway.util.Direction;

public class DirectionParameter implements Parameter {
  private final Direction direction;

  public DirectionParameter(Direction direction) {
    this.direction = direction;
  }

  @Override
  public JsonObject toJson() {
    JsonObject res = new JsonObject();
    JsonObject passable = new JsonObject();
    passable.putMember("direction", new UserJsonString(direction.name()));

    res.putMember("passableType", new UserJsonString(PassableType.DIRECTION.name()));
    res.putMember("passable", passable);
    return res;
  }
}
