package multiprocessing;

public interface MultiprocessingObserver {

    // Outputs of the machine --------------

    default void onRefSwitchRotationAtVacuum(int refSwitchRotationAtVacuum){ }

    default void onRefSwitchRotationAtBelt(int refSwitchRotationAtBelt){ }

    default void onLightBarrierConveyorEnd(boolean bool){ }

    default void onRefSwitchTurnTableAtSaw(int refSwitchTurnTableAtSaw){ }

    default void onRefSwitchVacuumAtTurnTable(boolean bool){ }

    default void onRefSwitchFeederInside(boolean bool){ }

    default void onRefSwitchFeederOutside(boolean bool){ }

    default void onRefSwitchVacuumAtOven(int refSwitchVacuumAtOven){ }

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