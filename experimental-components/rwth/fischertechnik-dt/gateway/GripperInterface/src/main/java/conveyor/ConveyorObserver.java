package conveyor;

public interface ConveyorObserver {

  // Outputs of the machine --------------

  default void onLightBarrierFeedStation(boolean bool){ }

  default void onLightBarrierSwapStation(boolean bool){ }

  default void onPulseButton(boolean bool){ }

  // Inputs of the machine ----------------

  default void onMoveConveyorForward(boolean bool){ }

  default void onMoveConveyorBackward(boolean bool){ }
}
