package classes

data class DynamicJointState(
    val header: Header,
    val interface_values: List<InterfaceValue>,
    val joint_names: List<String>?,
    val topic: String
)