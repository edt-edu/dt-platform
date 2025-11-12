package tmp;

component VacuumGripperController {

  // Goal Position
  port sync in float verticalPosGoal,
       sync in float horizontalPosGoal,
       sync in float rotationPosGoal;

  // Current Position
  port sync in float verticalPos,
       sync in float horizontalPos,
       sync in float rotationPos;

  port sync out boolean verticalUp,
       sync out boolean verticalDown,
       sync out boolean horizontalForward,
       sync out boolean horizontalBack,
       sync out boolean rotationClockwise,
       sync out boolean rotationCounterclockwise;

  automaton {
    initial state S;
    state Vertical;
    state Horizontal;
    state Rotation;

    S -> S [verticalPos == verticalPosGoal && horizontalPos == horizontalPosGoal && rotationPos == rotationPosGoal] / { }

    S -> Vertical [verticalPos != verticalPosGoal] / {
      if (verticalPos < verticalPosGoal) {
        verticalUp = true;
        verticalDown = false;
      } else {
        verticalUp = false;
        verticalDown = true;
      }
    }

    Vertical -> Vertical [verticalPos != verticalPosGoal] / {
      if (verticalPos < verticalPosGoal) {
        verticalUp = true;
        verticalDown = false;
      } else {
        verticalUp = false;
        verticalDown = true;
      }
    }

    Vertical -> S [verticalPos == verticalPosGoal && horizontalPos == horizontalPosGoal && rotationPos == rotationPosGoal] / { }

    Vertical -> Horizontal [verticalPos == verticalPosGoal && horizontalPos != horizontalPosGoal] / {
      if (horizontalPos < horizontalPosGoal) {
        horizontalForward = true;
        horizontalBack = false;
      } else {
        horizontalForward = false;
        horizontalBack = true;
      }
    }

    Vertical -> Rotation [verticalPos == verticalPosGoal && rotationPos != rotationPosGoal] / {
      if (rotationPos < rotationPosGoal) {
        rotationClockwise = true;
        rotationCounterclockwise = false;
      } else {
        rotationClockwise = false;
        rotationCounterclockwise = true;
      }
    }

    S -> Horizontal [horizontalPos != horizontalPosGoal] / {
      if (horizontalPos < horizontalPosGoal) {
        horizontalForward = true;
        horizontalBack = false;
      } else {
        horizontalForward = false;
        horizontalBack = true;
      }
    }

    Horizontal -> Horizontal [horizontalPos != horizontalPosGoal] / {
      if (horizontalPos < horizontalPosGoal) {
        horizontalForward = true;
        horizontalBack = false;
      } else {
        horizontalForward = false;
        horizontalBack = true;
      }
    }

    Horizontal -> S [verticalPos == verticalPosGoal && horizontalPos == horizontalPosGoal && rotationPos == rotationPosGoal] / { }

    Horizontal -> Vertical [horizontalPos == horizontalPosGoal && verticalPos != verticalPosGoal] / {
      if (verticalPos < verticalPosGoal) {
        verticalUp = true;
        verticalDown = false;
      } else {
        verticalUp = false;
        verticalDown = true;
      }
    }

    Horizontal -> Rotation [horizontalPos == horizontalPosGoal && rotationPos != rotationPosGoal] / {
      if (rotationPos < rotationPosGoal) {
        rotationClockwise = true;
        rotationCounterclockwise = false;
      } else {
        rotationClockwise = false;
        rotationCounterclockwise = true;
      }
    }

    S -> Rotation [rotationPos != rotationPosGoal] / {
      if (rotationPos < rotationPosGoal) {
        rotationClockwise = true;
        rotationCounterclockwise = false;
      } else {
        rotationClockwise = false;
        rotationCounterclockwise = true;
      }
    }

    Rotation -> Rotation [rotationPos != rotationPosGoal] / {
      if (rotationPos < rotationPosGoal) {
        rotationClockwise = true;
        rotationCounterclockwise = false;
      } else {
        rotationClockwise = false;
        rotationCounterclockwise = true;
      }
    }

    Rotation -> S [verticalPos == verticalPosGoal && horizontalPos == horizontalPosGoal && rotationPos == rotationPosGoal] / { }

    Rotation -> Vertical [rotationPos == rotationPosGoal && verticalPos != verticalPosGoal] / {
      if (verticalPos < verticalPosGoal) {
        verticalUp = true;
        verticalDown = false;
      } else {
        verticalUp = false;
        verticalDown = true;
      }
    }

    Rotation -> Horizontal [rotationPos == rotationPosGoal && horizontalPos != horizontalPosGoal] / {
      if (horizontalPos < horizontalPosGoal) {
        horizontalForward = true;
        horizontalBack = false;
      } else {
        horizontalForward = false;
        horizontalBack = true;
      }
    }
  }
}