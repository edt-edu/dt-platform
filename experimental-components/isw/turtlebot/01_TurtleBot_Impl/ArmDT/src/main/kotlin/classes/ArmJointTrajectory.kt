package classes

data class ArmJointTrajectory(
    var header: Header,
    var joint_names: List<String>,
    var points: List<Point>,
    var topic: String
)