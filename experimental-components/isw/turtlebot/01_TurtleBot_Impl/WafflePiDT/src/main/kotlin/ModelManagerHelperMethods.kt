import classes.*

class ModelManagerHelperMethods {

    fun getBatteryValues(dynamicJointState: DynamicJointState): String? {
        if (dynamicJointState.joint_names == null) {
            return null
        }
        val batteryIndex = dynamicJointState.joint_names.indexOf("battery")
        if (batteryIndex == -1) {
            return null
        }
        val batteryValues = dynamicJointState.interface_values[batteryIndex].values
        val voltage = batteryValues[0].toString()
        val percentage = batteryValues[1].toString()
        val design_capacity = batteryValues[2].toString()
        val present = batteryValues[3].toString()
        return createTableInsertString(listOf(voltage, percentage, design_capacity, present))
    }

    fun getImuValues(imu: ImuBroadcaster): String {
        val orientation = imu.orientation
        val angularVelocity = imu.angular_velocity
        val linearAcceleration = imu.linear_acceleration
        return createTableInsertString(
            listOf(
                orientation.x, orientation.y, orientation.z, orientation.w,
                angularVelocity.x, angularVelocity.y, angularVelocity.z,
                linearAcceleration.x, linearAcceleration.y, linearAcceleration.z
            )
        )
    }

    fun getCmdVelValues(cmdVel: CmdVel): String? {
        val linear = cmdVel.linear
        val angular = cmdVel.angular
        if (linear == null || angular == null) {
            return null
        }
        return createTableInsertString(
            listOf(
                linear.x, linear.y, linear.z, angular.x, angular.y, angular.z
            )
        )
    }

    fun getWheelsValues(jointStates: JointStates): String? {
        val rightWheelIndex = jointStates.name.indexOf("wheel_right_joint")
        val leftWheelIndex = jointStates.name.indexOf("wheel_left_joint")
        if (rightWheelIndex == -1 && leftWheelIndex == -1) {
            return null
        }
        val rightWheelPosition = jointStates.position[rightWheelIndex]
        val rightWheelVelocity = jointStates.velocity[rightWheelIndex]
        val rightWheelEffort = jointStates.effort[rightWheelIndex]
        val leftWheelPosition = jointStates.position[leftWheelIndex]
        val leftWheelVelocity = jointStates.velocity[leftWheelIndex]
        val leftWheelEffort = jointStates.effort[leftWheelIndex]
        return createTableInsertString(
            listOf(
                rightWheelPosition, leftWheelPosition, rightWheelVelocity,
                leftWheelVelocity, rightWheelEffort, leftWheelEffort
            )
        )
    }

    fun getOdomValues(odom: Odom): String {
        val pose = odom.pose.pose
        val twist = odom.twist.twist
        val childFrameId = odom.child_frame_id
        return createTableInsertString(
            listOf(
                pose!!.position.x, pose.position.y, pose.position.z,
                pose.orientation.x, pose.orientation.y, pose.orientation.z, pose.orientation.w,
                twist!!.linear.x, twist.linear.y, twist.linear.z,
                twist.angular.x, twist.angular.y, twist.angular.z,
                childFrameId
            )
        )
    }

    fun getParameterEventsValues(parameterEvent: ParameterEvent): String {
        val node = parameterEvent.node
        val newParameters = parameterEvent.new_parameters
        val changedParameters = parameterEvent.changed_parameters
        val deletedParameters = parameterEvent.deleted_parameters

        return createTableInsertString(
            listOf(
                node, newParameters.toString(),
                changedParameters.toString(), deletedParameters.toString()
            )
        )
    }

    fun getWheelTransformValues(tf: TF): String? {
        var rightWheelLink: Transform? = null
        var leftWheelLink: Transform? = null
        tf.transforms.forEach {
            if (it.child_frame_id == "wheel_right_link") rightWheelLink = it
            if (it.child_frame_id == "wheel_left_link") leftWheelLink = it
        }
        if (rightWheelLink == null && leftWheelLink == null) {
            return null
        }
        val leftWheelTransform = leftWheelLink!!.transform
        val rightWheelTransform = rightWheelLink!!.transform
        return createTableInsertString(
            listOf(
                leftWheelTransform!!.translation.x,
                leftWheelTransform.translation.y,
                leftWheelTransform.translation.z,
                rightWheelTransform!!.translation.x,
                rightWheelTransform.translation.y,
                rightWheelTransform.translation.z,
                leftWheelTransform.rotation.x,
                leftWheelTransform.rotation.y,
                leftWheelTransform.rotation.z,
                leftWheelTransform.rotation.w,
                rightWheelTransform.rotation.x,
                rightWheelTransform.rotation.y,
                rightWheelTransform.rotation.z,
                rightWheelTransform.rotation.w
            )
        )
    }

    fun getStaticFrameTransformations(tfStatic: TFStatic): List<String> {
        val result: MutableList<String> = mutableListOf()
        tfStatic.transforms.forEach {
            val childFrameId = it.child_frame_id
            val translation = it.transform!!.translation
            val rotation = it.transform.rotation
            result.add(
                createTableInsertString(
                    listOf(
                        childFrameId, translation.x, translation.y, translation.z,
                        rotation.x, rotation.y, rotation.z, rotation.w
                    )
                )
            )
        }
        return result
    }

    fun getRosoutValues(rosout: Rosout): String {
        val level = rosout.level
        val name = rosout.name
        val msg = rosout.msg
        val file = rosout.file
        val function = rosout.function
        return createTableInsertString(
            listOf(
                level, name, msg, file, function
            )
        )
    }

    fun getScanValues(scan: Scan): String {
        val scan_time = scan.scan_time
        val angle_min = scan.angle_min
        val angle_max = scan.angle_max
        val angle_increment = scan.angle_increment
        val range_min = scan.range_min
        val range_max = scan.range_max
        val time_increment = scan.time_increment
        val ranges: MutableList<Double> = mutableListOf()
        scan.ranges.forEach { range ->
        if (range is Double) {
            ranges.add(range)
        }
        }
        val lowestDist = ranges.min()
        return createTableInsertString(
            listOf(
                scan_time, angle_min, angle_max, angle_increment, range_min, range_max, time_increment, lowestDist
            )
        )
    }

    fun createTableInsertString(values: List<Any>): String {
        var s = ""
        values.forEach {
            s += "'${it}',"
        }
        val currentTimestamp = System.currentTimeMillis()
        s += "'${currentTimestamp}'"
        return s
    }
}