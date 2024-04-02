package classes

data class Transform(
    val child_frame_id: String,
    val header: Header,
    val translation: Translation,
    val rotation: Rotation,
    val transform: Transform?
)