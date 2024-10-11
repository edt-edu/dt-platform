public interface ConveyorObserver {

  // Outputs of the machine --------------

  default void onPhototransistorFeedStation(boolean bool){ }

  default void onPhototransistorSwapStation(boolean bool){ }

  default void onPulseButton(boolean bool){ }

  // Inputs of the machine ----------------

  default void onMoveConveyorForward(boolean bool){ }

  default void onMoveConveyorBackward(boolean bool){ }
}
