package classes

data class TES(
    var goal_state: GoalState,
    var start_state: StartState,
    var timestamp: Int,
    var topic: String,
    var transition: Transition
)