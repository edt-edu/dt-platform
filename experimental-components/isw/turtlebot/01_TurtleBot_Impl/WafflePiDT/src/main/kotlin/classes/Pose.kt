package classes

data class Pose(
    val covariance: List<Double>,
    val position: Position,
    val orientation: Orientation,
    val pose: Pose?
)