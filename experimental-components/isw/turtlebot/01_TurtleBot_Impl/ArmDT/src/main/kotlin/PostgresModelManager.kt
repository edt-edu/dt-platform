import classes.*
import com.google.gson.Gson
import dts.connection.SynchronizationDirection
import dts.events.NewDataPointEvent
import dts.events.observer.IModelObserver
import dts.modelmanager.AbstractModelManager
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDateTime


class PostgresModelManager : AbstractModelManager<String>(
    modelManagerID = "PostgresModelManager",
    propertyIDs = setOf("armControllerState","armJointTrajectory","armState","armTES","gripperTES","servoCollisionVelScale",
        "servoDeltaJoint","servoDeltaTwist","servoStatus",
        "dynamicJointState", "jointStates", "parameterEvent",
        "tf", "tfStatic", "armDeltaJoint", "armDeltaTwist", "moveArmDeltaJoint", "moveArmDeltaTwist", "joint_states"
    )
) {
    private val user = "postgres"
    private val password = "1234"
    val jdbcUrl = "jdbc:postgresql://postgres:5433/"
    var connection: Connection

    val helperMethods = ModelManagerHelperMethods()

    private var localJointJog: ServoDeltaJoint? = null
    private var localDeltaTwist: ServoDeltaTwist? = null



    init {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(jdbcUrl, user, password)
        logger.info { "Check if Connection to Postgres valid: " + connection.isValid(0) }

        initArmDatabase(connection)

        connection.close()

        connection = DriverManager.getConnection(jdbcUrl + "arm", user, password)


        createParameterEventTable(connection)

        createStaticTransformTable(connection)

        createServiceChangesTable(connection)

        createServoDeltaJointTable(connection)

        createArmControllerStateTable(connection)

        createArmControllerStateTable(connection)

        createArmStateTable(connection)

        createServoDeltaTwistTable(connection)

        createArmJointTrajectoryTable(connection)

        createJointStatesTable(connection)

        createLinkTransformTable(connection)

    }

    private fun sendNewDataPointEvent() {
        logger.info { "Send new data point event" }
        for (iml: IModelObserver in observer) {
            // setup the event
            val ex = NewDataPointEvent(this)
            ex.sourceID = modelManagerID
            ex.timestamp = LocalDateTime.now()
            ex.synchronizationDirection = SynchronizationDirection.DT_TO_GATEWAY

            // let the listener handle it
            iml.handleModelDatapointEvent(ex)
        }
    }

    private fun initArmDatabase(connection: Connection) {
        val createDatabase = """
            CREATE DATABASE arm
            WITH 
                ALLOW_CONNECTIONS = true""".trimIndent()
        try {
            connection.prepareStatement(createDatabase)?.execute()
        } catch (e: SQLException) {
            logger.error { e.message }
        }

    }

       private fun createServiceChangesTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS service_changes (
                table_name varchar(100),
                service_name varchar(100),
                changes varchar(400),
                time varchar(50)
             )
        """.trimIndent()
        connection.prepareStatement(createTable).execute()
    }

    private fun createServoDeltaJointTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS servo_delta_joint (
                joint varchar(100),
                velocity Numeric(5,2),
                time varchar(50)
             )
        """.trimIndent()
        connection.prepareStatement(createTable).execute()
    }


    private fun createParameterEventTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS parameter_event (      
                node varchar(100),
                new_parameters varchar(300),
                changed_parameters varchar(300),
                deleted_parameters varchar(300),
                time varchar(50)
             )
        """.trimIndent()
        connection.prepareStatement(createTable).execute()
    }


    private fun createStaticTransformTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS static_transform (
                child_frame_id varchar(100),
                translation_x NUMERIC(6,3),
                translation_y NUMERIC(6,3),
                translation_z NUMERIC(6,3),
                rotation_x NUMERIC(24,19),
                rotation_y NUMERIC(24,19),
                rotation_z NUMERIC(24,19),
                rotation_w NUMERIC(24,19),
                time varchar(50)
             )
        """.trimIndent()
        connection.prepareStatement(createTable).execute()
    }

    private fun createLinkTransformTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS link_transform (
                child_frame_id varchar(100),
                translation_x NUMERIC(6,3),
                translation_y NUMERIC(6,3),
                translation_z NUMERIC(6,3),
                rotation_x NUMERIC(24,19),
                rotation_y NUMERIC(24,19),
                rotation_z NUMERIC(24,19),
                rotation_w NUMERIC(24,19),
                time varchar(50)
             )
        """.trimIndent()
        connection.prepareStatement(createTable).execute()
    }

    private fun createArmControllerStateTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS arm_controller_state (
                joint varchar(10),
                reference_position Numeric(24,19),
                reference_velocity Numeric(24,19),
                reference_effort Numeric(24,19),
                reference_acceleration Numeric(24,19),
                feedback_position Numeric(24,19),
                feedback_velocity Numeric(24,19),
                feedback_effort Numeric(24,19),
                feedback_acceleration Numeric(24,19),
                error_position Numeric(24,19),
                error_velocity Numeric(24,19),
                error_effort Numeric(24,19),
                error_acceleration Numeric(24,19),
                output_position Numeric(24,19),
                output_velocity Numeric(24,19),
                output_effort Numeric(24,19),
                output_acceleration Numeric(24,19),
                desired_position Numeric(24,19),
                desired_velocity Numeric(24,19),
                desired_effort Numeric(24,19),
                desired_acceleration Numeric(24,19),
                actual_position Numeric(24,19),
                actual_velocity Numeric(24,19),
                actual_effort Numeric(24,19),
                actual_acceleration Numeric(24,19),
                time varchar(50)
             )
        """.trimIndent()
        connection.prepareStatement(createTable).execute()
    }

    private fun createArmStateTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS arm_state (
                joint varchar(10),
                reference_position Numeric(24,19),
                reference_velocity Numeric(24,19),
                reference_effort Numeric(24,19),
                reference_acceleration Numeric(24,19),
                feedback_position Numeric(24,19),
                feedback_velocity Numeric(24,19),
                feedback_effort Numeric(24,19),
                feedback_acceleration Numeric(24,19),
                error_position Numeric(24,19),
                error_velocity Numeric(24,19),
                error_effort Numeric(24,19),
                error_acceleration Numeric(24,19),
                output_position Numeric(24,19),
                output_velocity Numeric(24,19),
                output_effort Numeric(24,19),
                output_acceleration Numeric(24,19),
                desired_position Numeric(24,19),
                desired_velocity Numeric(24,19),
                desired_effort Numeric(24,19),
                desired_acceleration Numeric(24,19),
                actual_position Numeric(24,19),
                actual_velocity Numeric(24,19),
                actual_effort Numeric(24,19),
                actual_acceleration Numeric(24,19),
                time varchar(50)
             )
        """.trimIndent()
        connection.prepareStatement(createTable).execute()
    }

    private fun createArmJointTrajectoryTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS arm_joint_trajectory (
                joint varchar(10),
                point_position Numeric(24,19),
                point_velocity Numeric(24,19),
                point_acceleration Numeric(24,19),
                point_effort Numeric(24,19),
                time varchar(50)
             )
        """.trimIndent()
        connection.prepareStatement(createTable).execute()
    }

    private fun createServoDeltaTwistTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS servo_delta_twist (
                child_frame_id varchar(100),
                linear_x NUMERIC(6,3),
                linear_y NUMERIC(6,3),
                linear_z NUMERIC(6,3),
                angular_x NUMERIC(6,3),
                angular_y NUMERIC(6,3),
                angular_z NUMERIC(6,3),
                time varchar(50)
             )
        """.trimIndent()
        connection.prepareStatement(createTable).execute()
    }

    private fun createJointStatesTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS joint_states (
                joint varchar(100),
                joint_position NUMERIC(24,19),
                joint_velocity NUMERIC(24,19),
                joint_effort NUMERIC(24,19),
                time varchar(50)
             )
        """.trimIndent()
        connection.prepareStatement(createTable).execute()
    }


    private fun writeToTable(table: String, values: String) {
        logger.debug { "INSERT INTO ${table} VALUES (${values}) " }
        connection.prepareStatement("""INSERT INTO ${table} VALUES (${values} ) """)?.execute()
    }

    override fun writeToModel(property: String, value: String) {
        writeToTable(property, value)
    }

    override fun readFromModel(property: String, options: Any?): ResultSet {
        logger.debug { "Read ${property} from model with options: ${options}" }
        if (options != null) return connection.prepareStatement("""SELECT * FROM ${property} ORDER BY time DESC ${options}""").executeQuery()
        else return connection.prepareStatement("""SELECT * FROM ${property} ORDER BY time DESC""").executeQuery()
    }

    override fun getValueAtIndex(index: String, property: String): Any {
        TODO("Not yet implemented")
    }

    override fun getModel(): Any {
        TODO("Not yet implemented")
    }

    override fun getValue(id: String): Any? {
        when (id) {
            "moveArmDeltaJoint" -> {
                val result = localJointJog
                localJointJog = null
                logger.info{ result.toString()}
                return result
            }
            "moveArmDeltaTwist" -> {
                val result = localDeltaTwist
                localDeltaTwist = null
                logger.info{ result.toString()}
                return result
            }
            "joint_states" -> {
                val result = readFromModel("joint_states", "LIMIT 6")
                val jointStates: MutableList<JointStateRow> = mutableListOf()
                while (result.next()) {
                    val jointState = JointStateRow(
                        result.getString("joint"),
                        result.getDouble("joint_position"),
                        result.getDouble("joint_velocity"),
                        result.getDouble("joint_effort")
                    )
                    jointStates.add(jointState)
                }
                return jointStates
            }
            else -> {
                return null
            }
        }
    }

    override fun getAllValues(): Map<String, Any> {
        return emptyMap()
    }


    override fun manualSetValue(id: String, value: Any, serviceName: String) {
        sendNewDataPointEvent()
        val input = helperMethods.createTableInsertString(listOf(id, serviceName, value.toString()))
        writeToModel("service_changes", input)
    }
    override fun setValue(id: String, value: Any?) {
        if (value == "{}" || value == null) {
            return
        }
        val gson = Gson()
        if (id == "armControllerState") {
            val acs = gson.fromJson(value as String, ArmControllerState::class.java)
            var joint = 0
            while (joint < 4) {
                val values = helperMethods.getArmControllerStateValues(acs,joint)
                writeToModel("arm_controller_state", values)
                joint++
            }

        }
        if (id == "armJointTrajectory") {
            val ajt = gson.fromJson(value as String, ArmJointTrajectory::class.java)
            var joint = 0
            while (joint < 4) {
                val values = helperMethods.getArmJointTrajectoryValues(ajt, joint)
                values.forEach { value ->  writeToModel("arm_joint_trajectory", value) }
                joint++
            }

        }
        if (id == "armState") {
            val ars = gson.fromJson(value as String, ArmState::class.java)
            var joint = 0
            while (joint < 4) {
                val values = helperMethods.getArmStateValues(ars,joint)
                writeToModel("arm_state", values)
                joint++
            }

        }
        if (id == "armDeltaJoint") {
            localJointJog = value as ServoDeltaJoint
            logger.info { localJointJog.toString() }
            sendNewDataPointEvent()
            val input = helperMethods.createTableInsertString(listOf(id, "TurtlebotDT", value.toString()))
            writeToModel("service_changes", input)

        }
        if (id == "armDeltaTwist") {
            localDeltaTwist = value as ServoDeltaTwist
            logger.info { localDeltaTwist.toString() }
            sendNewDataPointEvent()
            val input = helperMethods.createTableInsertString(listOf(id, "TurtlebotDT", value.toString()))
            writeToModel("service_changes", input)

        }
        if (id == "servoCollisionVelScale") {
            return

        }
        if (id == "servoDeltaJoint") {
            val sdj = gson.fromJson(value as String, ServoDeltaJoint::class.java)
            val values = helperMethods.getServoDeltaJointValues(sdj)
            writeToModel("servo_delta_joint", values)

        }
        if (id == "servoDeltaTwist") {
            val sdt = gson.fromJson(value as String, ServoDeltaTwist::class.java)
            val values = helperMethods.getServoDeltaTwistValues(sdt)
            writeToModel("servo_delta_twist", values)

        }

        if (id == "servoStatus") {
            return

        }

        if (id == "jointStates") {
            val js = gson.fromJson(value as String, JointStates::class.java)
            val joints = listOf("joint1", "joint2", "joint3", "joint4", "gripper_left_joint", "gripper_right_joint")
            joints.forEach { joint ->
                val values = helperMethods.getJointsValues(js, joint)
                if (values != null) writeToModel("joint_states", values)
            }
        }

        if (id == "parameterEvent") {
            val pe = gson.fromJson(value as String, ParameterEvent::class.java)
            val values = helperMethods.getParameterEventsValues(pe)
            writeToModel("parameter_event", values)
        }
        if (id == "tf") {
            val tf = gson.fromJson(value as String, Transformation::class.java)
            val joints = listOf("link1", "link2", "link3", "link4", "link5", "gripper_left_link", "gripper_right_link")
            joints.forEach { joint ->
                val values = helperMethods.getLinkTransformValues(tf, joint)
                if (values != null) writeToModel("link_transform", values)
            }
        }
        if (id == "tfStatic") {
            val tfs = gson.fromJson(value as String, TFStatic::class.java)
            val values = helperMethods.getStaticFrameTransformations(tfs)
            values.forEach { writeToModel("static_transform", it) }
        }

    }

    override val lastPropertyUpdateMap: Map<String, Long>
        get() = TODO("Not yet implemented")

}