package classes

data class Reference(
    var accelerations: List<Double>,
    var effort: List<Any>,
    var positions: List<Double>,
    var time_from_start: TimeFromStart,
    var velocities: List<Double>
)