package high_bay;

public interface HighBayObserver {

  // Outputs of the machine --------------

  default void onRefSwitchHorizontal(boolean bool){ }

  default void onLightBarrierInside(boolean bool){ }

  default void onLightBarrierOutside(boolean bool){ }

  default void onRefSwitchVertical(boolean bool){ }

  default void onTrailSensorLower(boolean bool){ }

  default void onTrailSensorUpper(boolean bool){ }

  default void onHorizontalEnc1(boolean bool){ }

  default void onHorizontalEnc2(boolean bool){ }

  default void onVerticalEnc1(boolean bool){ }

  default void onVerticalEnc2(boolean bool){ }

  default void onRefSwitchCantileverFront(boolean bool){ }

  default void onRefSwitchCantileverBack(boolean bool){ }

  default void onVerticalPos(int verticalPos){ }

  default void onHorizontalPos(int horizontalPos){ }

  // Inputs of the machine ----------------

  default void onMoveConveyorForward(boolean bool){ }

  default void onMoveConveyorBackward(boolean bool){ }

  default void onMoveArmToRack(boolean bool){ }

  default void onMoveArmToConveyor(boolean bool){ }

  default void onMoveVerticalDown(boolean bool){ }

  default void onMoveVerticalUp(boolean bool){ }

  default void onMoveCantileverForward(boolean bool){ }

  default void onMoveCantileverBackward(boolean bool){ }
}
