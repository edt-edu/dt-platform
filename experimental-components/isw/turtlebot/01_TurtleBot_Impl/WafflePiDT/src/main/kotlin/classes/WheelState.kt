package classes

data class WheelState(
    var rightWheelPosition: Double,
    var leftWheelPosition: Double,
    var rightWheelVelocity: Double,
    var leftWheelVelocity: Double,
    var rightWheelEffort: Double,
    var leftWheelEffort: Double
)
