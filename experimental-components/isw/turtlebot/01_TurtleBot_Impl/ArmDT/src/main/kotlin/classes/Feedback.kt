package classes

data class Feedback(
    var accelerations: List<Any>,
    var effort: List<Any>,
    var positions: List<Double>,
    var time_from_start: TimeFromStart,
    var velocities: List<Double>
)