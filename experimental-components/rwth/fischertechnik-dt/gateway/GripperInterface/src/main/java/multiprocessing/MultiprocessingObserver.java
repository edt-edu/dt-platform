package multiprocessing;

public interface MultiprocessingObserver {

    // Outputs of the machine --------------

    default void onRefSwitchTurnTableAtVacuum(boolean bool){ }

    default void onRefSwitchTurnTableAtBelt(boolean bool){ }

    default void onRefSwitchTurnTableAtSaw(boolean bool){ }

    default void onLightBarrierConveyorEnd(boolean bool){ }

    default void onRefSwitchFeederInside(boolean bool){ }

    default void onRefSwitchFeederOutside(boolean bool){ }

    default void onRefSwitchVacuumAtTurnTable(boolean bool){ }

    default void onRefSwitchVacuumAtOven(boolean bool){ }

    default void onLightBarrierOven(boolean bool){ }

    // Inputs of the machine ----------------

    default void onMoveTurnTableClockwise(boolean bool){ }

    default void onMoveTurnTableCounterclockwise(boolean bool){ }

    default void onMoveConveyorForward(boolean bool){ }

    default void onEnableSaw(boolean bool){ }

    default void onMoveOvenFeederRetract(boolean bool){ }

    default void onMoveOvenFeederExtend(boolean bool){ }

    default void onMoveVacuumToOven(boolean bool){ }

    default void onMoveVacuumToTurnTable(boolean bool){ }

    default void onEnableOvenLight(boolean bool){ }

    default void onEnableCompressor(boolean bool){ }

    default void onEnableValveVacuum(boolean bool){ }

    default void onEnableValveMoveVacuum(boolean bool){ }

    default void onEnableValveMoveOvenDoor(boolean bool){ }

    default void onEnableValveMoveFeeder(boolean bool){ }
}