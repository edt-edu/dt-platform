package classes

data class Transform(
    var child_frame_id: String,
    var header: Header,
    var transform: Transform?,
    var translation: Translation?,
    var rotation: Rotation?

)