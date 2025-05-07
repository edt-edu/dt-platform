package fischertechnik_gateway.machines.parameters;

import de.monticore.symboltable.serialization.json.JsonObject;


public interface Parameter {
  JsonObject toJson();
}
