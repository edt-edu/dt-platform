package three_d_gripper;

public interface ThreeDGripperObserver {

  // Outputs of the machine --------------

  default void onRefSwitchClaw(boolean bool){ }

  default void onEncoderClawPos(int encCounterClaw){ }

  default void onRefSwitchGripper(boolean bool){ }

  default void onEncoderGripperPos(int encCounterGripper){ }

  default void onRefSwitchVertical(boolean bool){ }

  default void onRefSwitchRotation(boolean bool){ }

  default void onEncoderVerticalPos(int motorVerticalPos){ }

  default void onEncoderRotationPos(int motorRotationPos){ }

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