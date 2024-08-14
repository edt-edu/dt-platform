package tmp;

component Controller {

  // Goal Position
  port in float verticalPosGoal,
       in float horizontalPosGoal,
       in float rotationPosGoal;

  // Current Position
  port in float verticalPos,
       in float horizontalPos,
       in float rotationPos;

  port out boolean verticalUp,
       out boolean verticalDown,
       out boolean horizontalForward,
       out boolean horizontalBack,
       out boolean rotationClockwise,
       out boolean rotationCounterclockwise;

  automaton {
    initial state S;
    state Vertical {
      initial state V;
      state verticalDown;
      state verticalUp;
    };
    state Horizontal {
      initial state H;
      state horizontalForward;
      state horizontalBack;
    };
    state Rotation {
      initial state R;
      state rotationClockwise;
      state rotationCounterclockwise;
    };

    S -> S [verticalPos == verticalPosGoal && horizontalPos == horizontalPosGoal && rotationPos == rotationPosGoal];

    S -> Vertical [verticalPos != verticalPosGoal] / { };

    V -> verticalUp [verticalPos < verticalPosGoal] / {
      verticalUp = true;
      verticalDown = false;
    };

    V -> verticalDown [verticalPos > verticalPosGoal] / {
      verticalDown = true;
      verticalUp = false;
    };

    Vertical -> S [verticalPos == verticalPosGoal] / { };

    S -> Horizontal [horizontalPos != horizontalPosGoal] / { };

    H -> horizontalForward [horizontalPos < horizontalPosGoal] / {
      horizontalForward = true;
      horizontalBack = false;
    };

    H -> horizontalBack [horizontalPos > horizontalPosGoal] / {
      horizontalBack = true;
      horizontalForward = false;
    };

    Horizontal -> S [horizontalPos == horizontalPosGoal] / { };

    S -> Rotation [rotationPos != rotationPosGoal] / { };

    R -> rotationClockwise [rotationPos < rotationPosGoal] / {
      rotationClockwise = true;
      rotationCounterclockwise = false;
    };

    R -> rotationCounterclockwise [rotationPos > rotationPosGoal] / {
      rotationCounterclockwise = true;
      rotationClockwise = false;
    };

    Rotation -> S [rotationPos == rotationPosGoal] / { };
  }

}