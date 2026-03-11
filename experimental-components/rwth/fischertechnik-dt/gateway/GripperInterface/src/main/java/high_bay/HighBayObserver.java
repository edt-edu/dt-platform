package high_bay;

public interface HighBayObserver {

  // Outputs of the machine --------------
  default void onLightBarrierInside(boolean bool){ }

  default void onLightBarrierOutside(boolean bool){ }

  default void onRefSwitchHorizontal(boolean bool){ }

  default void onRefSwitchVertical(boolean bool){ }

  default void onRefSwitchCantileverFront(boolean bool){ }

  default void onRefSwitchCantileverBack(boolean bool){ }

  default void onEncoderVerticalPos(int verticalPos){ }

  default void onEncoderHorizontalPos(int horizontalPos){ }

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
