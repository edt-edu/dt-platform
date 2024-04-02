package classes

data class JointStates(
    var effort: List<String>,
    var header: Header,
    var name: List<String>,
    var position: List<Double>,
    var topic: String,
    var velocity: List<Double>
)