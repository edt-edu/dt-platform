package classes

data class BatteryState(
    val voltage: Double,
    val percentage: Double,
    val design_capacity: Double,
    val present: Double
)