package indexed_line;

public interface IndexedLineObserver {

  // Outputs of the machine --------------

  default void onButtonSlider1Front(boolean bool){ }

  default void onButtonSlider1Rear(boolean bool){ }

  default void onButtonSlider2Front(boolean bool){ }

  default void onButtonSlider2Rear(boolean bool){ }

  default void onPhototransistorSlider1(boolean bool){ }

  default void onPhototransistorMillingMachine(boolean bool){ }

  default void onPhototransistorLoadingStation(boolean bool){ }

  default void onPhototransistorDrillingMachine(boolean bool){ }

  default void onPhototransistorConveyorSwap(boolean bool){ }

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
