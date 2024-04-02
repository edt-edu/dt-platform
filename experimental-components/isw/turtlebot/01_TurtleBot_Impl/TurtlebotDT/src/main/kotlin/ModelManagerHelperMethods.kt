import classes.ArmState
import classes.Control
import classes.WafflePiState

class ModelManagerHelperMethods {


    fun getControlValues(control: Control): String {
        return createTableInsertString(
            listOf(
                control.direction, control.speed, control.continuous
            )
        )
    }

    fun getWafflePiStateValues(wafflePiState: WafflePiState): String {
        val movement = wafflePiState.movement
        val batteryState = wafflePiState.batteryState
        val orientation = wafflePiState.orientation
        val ros = wafflePiState.ros
        val wheelState = wafflePiState.wheelState
        return createTableInsertString(
            listOf(
                movement!!.linear, movement.angular,movement.speed, movement.time,
                batteryState!!.percentage, batteryState.time,
                orientation!!.x, orientation.y, orientation.z, orientation.w, orientation.time,
                ros!!.msg, ros.time,
                wheelState!!.rightWheelPosition, wheelState.leftWheelPosition, wheelState.rightWheelVelocity, wheelState.leftWheelVelocity, wheelState.time,
                wafflePiState.distance
            )
        )
    }

    fun getArmStateValues(armState: ArmState): List<String> {
        val jointStates = armState.jointStates
        val result: MutableList<String> = mutableListOf()
        jointStates?.forEach { js ->
            result.add(createTableInsertString(listOf(
                js?.joint,js?.jointPosition, js?.jointVelocity, js?.jointEffort
            )))
        }
        return result

    }

    fun createTableInsertString(values: List<Any?>): String {
        var s = ""
        values.forEach {
            s += "'${it}',"
        }
        val currentTimestamp = System.currentTimeMillis()
        s += "'${currentTimestamp}'"
        return s
    }
}