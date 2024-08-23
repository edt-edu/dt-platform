public interface ThreeDGripperObserver {

  // Outputs of the machine --------------

  default void onRefSwitchClaw(boolean bool){ }

  default void onEncCounterClaw(int encCounterClaw){ }

  default void onRefSwitchGripper(boolean bool){ }

  default void onEncCounterGripper(int encCounterGripper){ }

  default void onRefSwitchVertical(boolean bool){ }

  default void onRefSwitchRotation(boolean bool){ }

  default void onMotorVerticalEnc1(boolean bool){ }

  default void onMotorVerticalEnc2(boolean bool){ }

  default void onMotorRotationEnc1(boolean bool){ }

  default void onMotorRotationEnc2(boolean bool){ }

  default void onMotorVerticalPos(int motorVerticalPos){ }

  default void onMotorRotationPos(int motorRotationPos){ }

  // Inputs of the machine ----------------

  default void onOpenGripper(boolean bool){ }

  default void onCloseGripper(boolean bool){ }

  default void onMoveHorizontalForward(boolean bool){ }

  default void onMoveHorizontalBackward(boolean bool){ }

  default void onMoveVerticalUp(boolean bool){ }

  default void onMoveVerticalDown(boolean bool){ }

  default void onMoveRotationClockwise(boolean bool){ }

  default void onMoveRotationCounterclockwise(boolean bool){ }
}