package classes

data class Odom(
    val child_frame_id: String,
    val header: Header,
    val pose: Pose,
    val topic: String,
    val twist: Twist
)