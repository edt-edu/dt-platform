package indexed_line;

public interface IndexedLineObserver {

  // Outputs of the machine --------------

  default void onRefSwitchSlider1Front(boolean bool){ }

  default void onRefSwitchSlider1Rear(boolean bool){ }

  default void onRefSwitchSlider2Front(boolean bool){ }

  default void onRefSwitchSlider2Rear(boolean bool){ }

  default void onLightBarrierSlider1(boolean bool){ }

  default void onLightBarrierMillingMachine(boolean bool){ }

  default void onLightBarrierLoadingStation(boolean bool){ }

  default void onLightBarrierDrillingMachine(boolean bool){ }

  default void onLightBarrierConveyorSwap(boolean bool){ }

  // Inputs of the machine ----------------

  default void onMoveSlider1Backward(boolean bool){ }

  default void onMoveSlider1Forward(boolean bool){ }

  default void onMoveSlider2Backward(boolean bool){ }

  default void onMoveSlider2Forward(boolean bool){ }

  default void onMoveConveyorFeed(boolean bool){ }

  default void onMoveConveyorMillingMachine(boolean bool){ }

  default void onMoveMillingMachine(boolean bool){ }

  default void onMoveConveyorDrillingMachine(boolean bool){ }

  default void onMoveDrillingMachine(boolean bool){ }

  default void onMoveConveyorSwap(boolean bool){ }
}
