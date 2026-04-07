package fischertechnik_gateway.util;

import fischertechnik_gateway.parameters.Parameter;

public interface Position {
  Parameter toParameter(PositionMeaning meaning);
}
