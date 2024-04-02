package classes

data class Twist(
    val covariance: List<Double>,
    val linear: Linear,
    val angular: Angular,
    val twist: Twist?
)