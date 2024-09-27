package vacuum_gripper;

public interface GripperObserver {

  // Outputs of the machine --------------

  default void onRefSwitchVertical(boolean bool){ }

  default void onRefSwitchHorizontal(boolean bool){ }

  default void onRefSwitchRotation(boolean bool){ }

  default void onMotorVerticalEnc1(boolean bool){ }

  default void onMotorVerticalEnc2(boolean bool){ }

  default void onMotorHorizontalEnc1(boolean bool){ }

  default void onMotorHorizontalEnc2(boolean bool){ }

  default void onMotorRotationEnc1(boolean bool){ }

  default void onMotorRotationEnc2(boolean bool){ }

  default void onMotorVerticalPos(int motorVerticalPos){ }

  default void onMotorHorizontalPos(int motorHorizontalPos){ }

  default void onMotorRotationPos(int motorRotationPos){ }

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
