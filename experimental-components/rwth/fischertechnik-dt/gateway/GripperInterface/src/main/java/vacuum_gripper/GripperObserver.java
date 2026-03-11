package vacuum_gripper;

public interface GripperObserver {

  // Outputs of the machine --------------

  default void onRefSwitchVertical(boolean bool){ }

  default void onRefSwitchHorizontal(boolean bool){ }

  default void onRefSwitchRotation(boolean bool){ }

  default void onEncoderVerticalPos(int motorVerticalPos){ }

  default void onEncoderHorizontalPos(int motorHorizontalPos){ }

  default void onEncoderRotationPos(int motorRotationPos){ }

  // Inputs of the machine ----------------

  default void onMoveVerticalUp(boolean bool){ }

  default void onMoveVerticalDown(boolean bool){ }

  default void onMoveHorizontalUp(boolean bool){ }

  default void onMoveHorizontalDown(boolean bool){ }

  default void onMoveRotationClockwise(boolean bool){ }

  default void onMoveRotationCounterclockwise(boolean bool){ }

  default void onEnableCompressor(boolean bool){ }

  default void onEnableValve(boolean bool){ }
}
