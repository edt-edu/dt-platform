package classes

data class JointStateRow(
    var joint: String?,
    var jointPosition: Double?,
    var jointVelocity: Double?,
    var jointEffort: Double?
)
