package classes

data class Output(
    var accelerations: List<Any>,
    var effort: List<Any>,
    var positions: List<Any>,
    var time_from_start: TimeFromStart,
    var velocities: List<Any>
)