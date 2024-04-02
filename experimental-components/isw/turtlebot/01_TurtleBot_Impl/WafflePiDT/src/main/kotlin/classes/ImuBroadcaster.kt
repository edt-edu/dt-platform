package classes

data class ImuBroadcaster(
    val angular_velocity: AngularVelocity,
    val angular_velocity_covariance: List<Double>?,
    val header: Header?,
    val linear_acceleration: LinearAcceleration,
    val linear_acceleration_covariance: List<Double>?,
    val orientation: Orientation,
    val orientation_covariance: List<Double>?,
    val topic: String?
)