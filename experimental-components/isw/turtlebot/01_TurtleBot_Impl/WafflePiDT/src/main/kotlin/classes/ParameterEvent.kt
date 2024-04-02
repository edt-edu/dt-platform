package classes

data class ParameterEvent(
    val changed_parameters: List<Any>,
    val deleted_parameters: List<Any>,
    val new_parameters: List<NewParameter>,
    val node: String,
    val stamp: Stamp,
    val topic: String
)