package tmp;

component PunchingMachineController {

  // Sensor data
  port in boolean phototransistorInputOutput,
       in boolean phototransistorPunchingMachine,
       in boolean punchingMachineTopPos,
       in boolean punchingMachineBottomPos;

  port out boolean moveConveyorForward,
       out boolean moveConveyorBackward,
       out boolean movePunchingMachineUp,
       out boolean movePunchingMachineDown;

  <<sync>> automaton {
    initital state S;
    state GoodsAtInputOutput;
    state GoodsAtPunchingMachine;
    state PunchingMachineDown;
    state PunchingMachineUp;
    state ConveyorForward;
    state ConveyorBack;


    S -> S [phototransistorInputOutput == false &&
            phototransistorPunchingMachine == false &&
            punchingMachineTopPos == true &&
            punchingMachineBottomPos == false] / { };

    S -> GoodsAtInputOutput [phototransistorInputOutput == true &&
                             phototransistorPunchingMachine == false &&
                             punchingMachineTopPos == true &&
                             punchingMachineBottomPos == false] / { };

    GoodsAtInputOutput -> ConveyorForward [phototransistorInputOutput == false &&
                                           phototransistorPunchingMachine == false &&
                                           punchingMachineTopPos == true &&
                                           punchingMachineBottomPos == false] / {
      moveConveyorForward = true;
      moveConveyorBackward = false;
      movePunchingMachineUp = false;
      movePunchingMachineDown = false;
    };

    ConveyorForward -> ConveyorForward [phototransistorInputOutput == false &&
                                        phototransistorPunchingMachine == false &&
                                        punchingMachineTopPos == true &&
                                        punchingMachineBottomPos == false] / {
      moveConveyorForward = true;
      moveConveyorBackward = false;
      movePunchingMachineUp = false;
      movePunchingMachineDown = false;
    };

    ConveyorForward -> GoodsAtPunchingMachine [phototransistorInputOutput == false &&
                                               phototransistorPunchingMachine == true &&
                                               punchingMachineTopPos == true &&
                                               punchingMachineBottomPos == false] / {
      moveConveyorForward = false;
      moveConveyorBackward = false;
      movePunchingMachineUp = false;
      movePunchingMachineDown = false;
    };

    GoodsAtPunchingMachine -> PunchingMachineDown [phototransistorInputOutput == false &&
                                                   phototransistorPunchingMachine == true &&
                                                   punchingMachineTopPos == true &&
                                                   punchingMachineBottomPos == false] / {
      moveConveyorForward = false;
      moveConveyorBackward = false;
      movePunchingMachineDown = true;
      movePunchingMachineUp = false;
    };

    PunchingMachineDown -> PunchingMachineDown [phototransistorInputOutput == false &&
                                                phototransistorPunchingMachine == true &&
                                                punchingMachineTopPos == false &&
                                                punchingMachineBottomPos == false] / {
      moveConveyorForward = false;
      moveConveyorBackward = false;
      movePunchingMachineDown = true;
      movePunchingMachineUp = false;
    };

    PunchingMachineDown -> PunchingMachineUp [phototransistorInputOutput == false &&
                                              phototransistorPunchingMachine == true &&
                                              punchingMachineTopPos == false &&
                                              punchingMachineBottomPos == true] / {
      moveConveyorForward = false;
      moveConveyorBackward = false;
      movePunchingMachineDown = false;
      movePunchingMachineUp = true;
    };

    PunchingMachineUp -> PunchingMachineUp [phototransistorInputOutput == false &&
                                            phototransistorPunchingMachine == true &&
                                            punchingMachineTopPos == false &&
                                            punchingMachineBottomPos == false] / {
      moveConveyorForward = false;
      moveConveyorBackward = false;
      movePunchingMachineDown = false;
      movePunchingMachineUp = true;
    };

    PunchingMachineUp -> GoodsAtPunchingMachine [phototransistorInputOutput == false &&
                                                 phototransistorPunchingMachine == true &&
                                                 punchingMachineTopPos == true &&
                                                 punchingMachineBottomPos == false] / {
      moveConveyorForward = false;
      moveConveyorBackward = false;
      movePunchingMachineDown = false;
      movePunchingMachineUp = false;
    };

    GoodsAtPunchingMachine -> ConveyorBack [phototransistorInputOutput == false &&
                                            phototransistorPunchingMachine == true &&
                                            punchingMachineTopPos == true &&
                                            punchingMachineBottomPos == false] / {
      moveConveyorForward = false;
      moveConveyorBackward = true;
      movePunchingMachineDown = false;
      movePunchingMachineUp = false;
    };

    ConveyorBack -> ConveyorBack [phototransistorInputOutput == false &&
                                  phototransistorPunchingMachine == false &&
                                  punchingMachineTopPos == true &&
                                  punchingMachineBottomPos == false] / {
      moveConveyorForward = false;
      moveConveyorBackward = true;
      movePunchingMachineDown = false;
      movePunchingMachineUp = false;
    };

    ConveyorBack -> GoodsAtInputOutput [phototransistorInputOutput == true &&
                                        phototransistorPunchingMachine == false &&
                                        punchingMachineTopPos == true &&
                                        punchingMachineBottomPos == false] / {
      moveConveyorForward = false;
      moveConveyorBackward = false;
      movePunchingMachineDown = false;
      movePunchingMachineUp = false;
    };

    GoodsAtInputOutput -> GoodsAtInputOutput [phototransistorInputOutput == true &&
                                              phototransistorPunchingMachine == false &&
                                              punchingMachineTopPos == true &&
                                              punchingMachineBottomPos == false] / {
      moveConveyorForward = false;
      moveConveyorBackward = false;
      movePunchingMachineDown = false;
      movePunchingMachineUp = false;
    };

    GoodsAtInputOutput -> S [phototransistorInputOutput == false &&
                             phototransistorPunchingMachine == false &&
                             punchingMachineTopPos == true &&
                             punchingMachineBottomPos == false] / {
      moveConveyorForward = false;
      moveConveyorBackward = false;
      movePunchingMachineDown = false;
      movePunchingMachineUp = false;
    };