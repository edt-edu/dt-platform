package classes

data class MultiDofFeedback(
    var accelerations: List<Any>,
    var time_from_start: TimeFromStart,
    var transforms: List<Any>,
    var velocities: List<Any>
)