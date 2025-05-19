package fischertechnik_gateway.parameters;

import de.monticore.symboltable.serialization.json.JsonObject;


public interface Parameter {
  JsonObject toJson();
}
