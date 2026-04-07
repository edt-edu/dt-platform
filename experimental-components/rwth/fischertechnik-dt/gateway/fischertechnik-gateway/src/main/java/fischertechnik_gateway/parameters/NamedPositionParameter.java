package fischertechnik_gateway.parameters;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symboltable.serialization.json.UserJsonString;
import fischertechnik_gateway.util.NamedPosition;
import fischertechnik_gateway.util.PositionMeaning;

public class NamedPositionParameter implements Parameter {

  private final NamedPosition position;
  private final PositionMeaning meaning;

  public NamedPositionParameter(NamedPosition position, PositionMeaning meaning) {
    this.position = position;
    this.meaning = meaning;
  }

  @Override
  public JsonObject toJson() {
    var res = new JsonObject();
    var passable = new JsonObject();
    passable.putMember("name", new UserJsonString(position.getName()));
    passable.putMember("meaning", new UserJsonString(meaning.name()));

    res.putMember("passableType", new UserJsonString("NAMEDPOSITION"));
    res.putMember("passable", passable);
    return res;
  }
}
