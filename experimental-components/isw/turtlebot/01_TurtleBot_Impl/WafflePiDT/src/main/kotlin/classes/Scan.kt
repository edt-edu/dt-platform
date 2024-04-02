package classes

data class Scan(
    val angle_increment: Double,
    val angle_max: Double,
    val angle_min: Double,
    val header: Header,
    val intensities: List<Any>,
    val range_max: Double,
    val range_min: Double,
    val ranges: List<Any>,
    val scan_time: Double,
    val time_increment: Double,
    val topic: String
)