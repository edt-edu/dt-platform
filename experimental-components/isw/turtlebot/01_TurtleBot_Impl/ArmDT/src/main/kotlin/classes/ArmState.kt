package classes

data class ArmState(
    var actual: Actual,
    var desired: Desired,
    var error: Error,
    var feedback: Feedback,
    var header: Header,
    var joint_names: List<String>,
    var multi_dof_actual: MultiDofActual,
    var multi_dof_desired: MultiDofDesired,
    var multi_dof_error: MultiDofError,
    var multi_dof_feedback: MultiDofFeedback,
    var multi_dof_joint_names: List<Any>,
    var multi_dof_output: MultiDofOutput,
    var multi_dof_reference: MultiDofReference,
    var output: Output,
    var reference: Reference,
    var topic: String
)