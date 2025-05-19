package fischertechnik_gateway.parameters;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symboltable.serialization.json.UserJsonString;
import fischertechnik_gateway.util.PositionMeaning;
import fischertechnik_gateway.util.PositionThreeD;
import fischertechnik_gateway.message.JsonFactory;

public class PositionParameterThreeD implements Parameter {
  private final PositionThreeD position;
  private final PositionMeaning meaning;

  public PositionParameterThreeD(PositionThreeD position, PositionMeaning meaning) {
    this.position = position;
    this.meaning = meaning;
  }

  @Override
  public JsonObject toJson() {
    JsonObject res = new JsonObject();
    JsonObject passable = new JsonObject();
    passable.putMember("vertical", JsonFactory.createNumber(position.getVertical()));
    passable.putMember("horizontal", JsonFactory.createNumber(position.getHorizontal()));
    passable.putMember("rot", JsonFactory.createNumber(position.getRotation()));
    passable.putMember("meaning", new UserJsonString(meaning.name()));

    res.putMember("passableType", new UserJsonString("POSITIONPARAMETERTHREED"));
    res.putMember("passable", passable);
    return res;
  }
}
