package classes

data class ParameterEvent(
    var changed_parameters: List<Any>,
    var deleted_parameters: List<Any>,
    var new_parameters: List<NewParameter>,
    var node: String,
    var stamp: Stamp,
    var topic: String
)