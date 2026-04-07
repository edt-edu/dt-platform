package fischertechnik_gateway.util;

import fischertechnik_gateway.parameters.Parameter;
import fischertechnik_gateway.parameters.PositionParameterThreeD;

public class PositionThreeD implements Position {
  private final int horizontal;
  private final int vertical;
  private final int rotation;

  public PositionThreeD(int rotation, int vertical, int horizontal) {
    this.horizontal = horizontal;
    this.vertical = vertical;
    this.rotation = rotation;
  }

  public int getHorizontal() {
    return horizontal;
  }

  public int getRotation() {
    return rotation;
  }

  public int getVertical() {
    return vertical;
  }

  @Override
  public Parameter toParameter(PositionMeaning meaning) {
    return new PositionParameterThreeD(this, meaning);
  }
}
