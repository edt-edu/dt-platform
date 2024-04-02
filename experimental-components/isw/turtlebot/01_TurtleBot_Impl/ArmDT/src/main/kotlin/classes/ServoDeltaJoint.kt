package classes

data class ServoDeltaJoint(
    var displacements: List<Any>,
    var duration: Double,
    var header: Header,
    var joint_names: MutableList<String>,
    var topic: String,
    var velocities: MutableList<Double>
)