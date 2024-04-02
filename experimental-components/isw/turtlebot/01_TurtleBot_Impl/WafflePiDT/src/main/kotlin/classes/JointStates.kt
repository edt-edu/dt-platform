package classes

data class JointStates(
    val effort: List<Double>,
    val header: Header,
    val name: List<String>,
    val position: List<Double>,
    val topic: String,
    val velocity: List<Double>
)