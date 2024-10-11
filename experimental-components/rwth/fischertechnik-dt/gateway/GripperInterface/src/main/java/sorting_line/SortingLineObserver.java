package sorting_line;

public interface SortingLineObserver {

  // Outputs of the machine --------------

  default void onPulseCounter(int pulseCounter){ }

  default void onLightBarrierInlet(boolean bool){ }

  default void onLightBarrierBehindColorSensor(boolean bool){ }

  default void onColorSensor(int colorSensor){ }

  default void onLightBarrierWhite(boolean bool){ }

  default void onLightBarrierRed(boolean bool){ }

  default void onLightBarrierBlue(boolean bool){ }

  // Inputs of the machine ----------------

  default void onMoveConveyor(boolean bool){ }

  default void onEnableCompressor(boolean bool){ }

  default void onEnableValveFirstEjector(boolean bool){ }

  default void onEnableValveSecondEjector(boolean bool){ }

  default void onEnableValveThirdEjector(boolean bool){ }
}
