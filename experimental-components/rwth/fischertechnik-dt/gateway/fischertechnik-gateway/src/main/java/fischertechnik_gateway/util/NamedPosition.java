package fischertechnik_gateway.util;

import fischertechnik_gateway.parameters.NamedPositionParameter;
import fischertechnik_gateway.parameters.Parameter;

public class NamedPosition implements Position {
  private final String name;

  public NamedPosition(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public Parameter toParameter(PositionMeaning meaning) {
    return new NamedPositionParameter(this, meaning);
  }
}
