package punching_machine;

public interface PunchingMachineObserver {

  // Outputs of the machine --------------

  default void onPhototransistorGoodsInOut(boolean bool){ }

  default void onPhototransistorPunchingMachine(boolean bool){ }

  default void onSwitchPunchingMachineUp(boolean bool){ }

  default void onSwitchPunchingMachineDown(boolean bool){ }

  // Inputs of the machine ----------------

  default void onMoveConveyorForward(boolean bool){ }

  default void onMoveConveyorBackward(boolean bool){ }

  default void onMovePunchingMachineUp(boolean bool){ }

  default void onMovePunchingMachineDown(boolean bool){ }
}
