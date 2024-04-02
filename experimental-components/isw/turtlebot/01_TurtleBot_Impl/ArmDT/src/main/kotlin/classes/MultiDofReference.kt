package classes

data class MultiDofReference(
    var accelerations: List<Any>,
    var time_from_start: TimeFromStart,
    var transforms: List<Any>,
    var velocities: List<Any>
)