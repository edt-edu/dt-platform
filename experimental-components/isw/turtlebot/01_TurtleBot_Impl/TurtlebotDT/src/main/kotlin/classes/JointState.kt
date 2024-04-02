package classes

data class JointState(
    var joint: String?,
    var jointEffort: Double?,
    var jointPosition: Double?,
    var jointVelocity: Double?
)