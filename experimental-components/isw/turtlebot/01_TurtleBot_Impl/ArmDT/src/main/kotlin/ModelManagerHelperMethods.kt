import classes.*

class ModelManagerHelperMethods {


    fun getJointsValues(jointStates: JointStates, joint: String): String? {
        val index = jointStates.name.indexOf(joint)
        if (index == -1) {
            return null
        }
        val position = jointStates.position[index]
        val velocity = jointStates.velocity[index]
        val effort = jointStates.effort[index]

        return createTableInsertString(
            listOf(
                joint,position,velocity,effort
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

    fun getArmControllerStateValues(armControllerState: ArmControllerState, joint: Int): String {
        val reference = armControllerState.reference
        val feedback = armControllerState.feedback
        val error = armControllerState.error
        val output = armControllerState.output
        val desired = armControllerState.desired
        val actual = armControllerState.actual
        val j = "joint" + (joint + 1)

        return createTableInsertString(
            listOf(
                j,
                fillDoubleListIfEmpty(reference.positions)[joint] ,
                fillDoubleListIfEmpty(reference.velocities)[joint] ,
                fillDoubleListIfEmpty(reference.effort)[joint] , fillDoubleListIfEmpty(reference.accelerations)[joint] ,
                fillDoubleListIfEmpty(feedback.positions)[joint] ,
                fillDoubleListIfEmpty(feedback.velocities)[joint],
                fillDoubleListIfEmpty(feedback.effort)[joint],fillDoubleListIfEmpty(feedback.accelerations)[joint],
                fillDoubleListIfEmpty(error.positions)[joint],
                fillDoubleListIfEmpty(error.velocities)[joint],
                fillDoubleListIfEmpty(error.effort)[joint],fillDoubleListIfEmpty(error.accelerations)[joint],
                fillDoubleListIfEmpty(output.positions)[joint],
                fillDoubleListIfEmpty(output.velocities)[joint],
                fillDoubleListIfEmpty(output.effort)[joint], fillDoubleListIfEmpty(output.accelerations)[joint],
                fillDoubleListIfEmpty(desired.positions)[joint],
                fillDoubleListIfEmpty(desired.velocities)[joint],
                fillDoubleListIfEmpty(desired.effort)[joint], fillDoubleListIfEmpty(desired.accelerations)[joint],
                fillDoubleListIfEmpty(actual.positions)[joint],
                fillDoubleListIfEmpty(actual.velocities)[joint],
                fillDoubleListIfEmpty(actual.effort)[joint], fillDoubleListIfEmpty(actual.accelerations)[joint],
            )

        )
    }

    private fun fillDoubleListIfEmpty(d: List<Any>): List<Any> {
        if (d.isEmpty()) {
            return listOf(0.0,0.0,0.0,0.0)
        }
        return d
    }

    fun getArmStateValues(armState: ArmState, joint: Int): String {
        val reference = armState.reference
        val feedback = armState.feedback
        val error = armState.error
        val output = armState.output
        val desired = armState.desired
        val actual = armState.actual
        val j = "joint" + (joint + 1)

        return createTableInsertString(
            listOf(
                j,
                fillDoubleListIfEmpty(reference.positions)[joint] ,
                fillDoubleListIfEmpty(reference.velocities)[joint] ,
                fillDoubleListIfEmpty(reference.effort)[joint] , fillDoubleListIfEmpty(reference.accelerations)[joint] ,
                fillDoubleListIfEmpty(feedback.positions)[joint] ,
                fillDoubleListIfEmpty(feedback.velocities)[joint],
                fillDoubleListIfEmpty(feedback.effort)[joint],fillDoubleListIfEmpty(feedback.accelerations)[joint],
                fillDoubleListIfEmpty(error.positions)[joint],
                fillDoubleListIfEmpty(error.velocities)[joint],
                fillDoubleListIfEmpty(error.effort)[joint],fillDoubleListIfEmpty(error.accelerations)[joint],
                fillDoubleListIfEmpty(output.positions)[joint],
                fillDoubleListIfEmpty(output.velocities)[joint],
                fillDoubleListIfEmpty(output.effort)[joint], fillDoubleListIfEmpty(output.accelerations)[joint],
                fillDoubleListIfEmpty(desired.positions)[joint],
                fillDoubleListIfEmpty(desired.velocities)[joint],
                fillDoubleListIfEmpty(desired.effort)[joint], fillDoubleListIfEmpty(desired.accelerations)[joint],
                fillDoubleListIfEmpty(actual.positions)[joint],
                fillDoubleListIfEmpty(actual.velocities)[joint],
                fillDoubleListIfEmpty(actual.effort)[joint], fillDoubleListIfEmpty(actual.accelerations)[joint],
            )

        )
    }


    fun getArmJointTrajectoryValues(armJointTrajectory: ArmJointTrajectory, joint: Int): List<String> {
        val points = armJointTrajectory.points
        val result: MutableList<String> = mutableListOf()
        val j = "joint" + (joint + 1)
        points.forEach { point ->
            result.add(createTableInsertString(
                listOf(
                    j,
                    fillDoubleListIfEmpty(point.positions)[joint],
                    fillDoubleListIfEmpty(point.velocities)[joint],
                    fillDoubleListIfEmpty(point.accelerations)[joint],
                    fillDoubleListIfEmpty(point.effort)[joint])))
        }
        return result
    }

    fun getServoDeltaTwistValues(servoDeltaTwist: ServoDeltaTwist): String {
        val id = servoDeltaTwist.header.frame_id
        val linear = servoDeltaTwist.twist.linear
        val angular = servoDeltaTwist.twist.angular
        return createTableInsertString(listOf(
            id,
            linear.x,
            linear.y,
            linear.z,
            angular.x,
            angular.y,
            angular.z
        ))
    }

    fun getServoDeltaJointValues(servoDeltaJoint: ServoDeltaJoint): String {
        val jointStates = servoDeltaJoint.joint_names
        val velocities = servoDeltaJoint.velocities
        return createTableInsertString(listOf(
                jointStates.last(),
                velocities.last()
        ))
    }

    fun getLinkTransformValues(tf: Transformation, joint: String): String? {
        var link: Transform? = null
        tf.transforms.forEach {
            if (it.child_frame_id == joint) link = it
        }
        if (link == null) {
            return null
        }
        val linkTransform = link!!.transform

        return createTableInsertString(
            listOf(joint) +
                transformToList(linkTransform)

        )
    }

    private fun transformToList(tf: Transform?): List<Any> {
        return listOf(
            tf!!.translation!!.x,
            tf.translation!!.y,
            tf.translation!!.z,
            tf.rotation!!.x,
            tf.rotation!!.y,
            tf.rotation!!.z,
            tf.rotation!!.w,
        )
    }

    fun getStaticFrameTransformations(tfStatic: TFStatic): List<String> {
        val result: MutableList<String> = mutableListOf()
        tfStatic.transforms.forEach {
            val childFrameId = it.child_frame_id
            val translation = it.transform!!.translation
            val rotation = it.transform!!.rotation
            result.add(
                createTableInsertString(
                    listOf(
                        childFrameId, translation!!.x, translation.y, translation.z,
                        rotation!!.x, rotation.y, rotation.z, rotation.w
                    )
                )
            )
        }
        return result
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