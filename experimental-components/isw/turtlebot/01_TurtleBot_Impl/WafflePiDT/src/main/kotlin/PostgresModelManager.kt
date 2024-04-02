import classes.*
import com.google.gson.Gson
import dts.connection.SynchronizationDirection
import dts.events.NewDataPointEvent
import dts.events.observer.IModelObserver
import dts.modelmanager.AbstractModelManager
import mu.KotlinLogging
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

class PostgresModelManager : AbstractModelManager<String>(
    modelManagerID = "PostgresModelManager",
    propertyIDs = setOf(
        "dynamicJointState", "cmdVel", "diffDriveTES", "imuBroadcaster",
        "imuBroadcasterTES", "jointStateBroadcasterTES", "jointStates", "odom", "parameterEvent",
        "tf", "tfStatic", "rosout", "scan", "turtlebot_movement", "batteryState", "moveCmdVel", "cmd_vel", "imuState", "rosState", "wheelState", "distance", "distanceLimit"
    )
) {
    private val user = "postgres"
    private val password = "1234"
    //val jdbcUrl = "jdbc:postgresql://localhost:5432/"
    val jdbcUrl = "jdbc:postgresql://postgres:5432/"
    var connection: Connection

    val helperMethods = ModelManagerHelperMethods()

    var localCmdVel: CmdVel? = CmdVel(Angular(x = 0.0, y = 0.0, z = 0.0), Linear(x = 0.0, y = 0.0, z = 0.0), null)

    var distanceLimit = 0.1


    init {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(jdbcUrl, user, password)
        logger.info { "Check if Connection to Postgres valid: " + connection.isValid(0) }

        initWafflePiDatabase(connection)

        connection.close()

        connection = DriverManager.getConnection(jdbcUrl + "waffle_pi", user, password)

        createBatteryStateTable(connection)

        createImuTable(connection)

        createCmdVelTable(connection)

        createWheelsTable(connection)

        createOdomTable(connection)

        createParameterEventTable(connection)

        createWheelTransformTable(connection)

        createStaticTransformTable(connection)

        createRosoutTable(connection)

        createScanTable(connection)

        createServiceChangesTable(connection)

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

    private fun initWafflePiDatabase(connection: Connection) {
        val createDatabase = """
            CREATE DATABASE waffle_pi
            WITH 
                ALLOW_CONNECTIONS = true""".trimIndent()
        try {
            connection.prepareStatement(createDatabase)?.execute()
        } catch (e: SQLException) {
            logger.error { e.message }
        }

    }

    private fun createBatteryStateTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS battery_state (
                voltage NUMERIC(5,2),
                percentage NUMERIC(5,2),
                design_capacity NUMERIC(5,2),
                present NUMERIC(5,2),
                time varchar(50)
             )
        """.trimIndent()
        connection.prepareStatement(createTable).execute()
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

    private fun createImuTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS imu (
                orientation_x NUMERIC(24,19),
                orientation_y NUMERIC(24,19),
                orientation_z NUMERIC(24,19),
                orientation_w NUMERIC(24,19),
                angular_velocity_x NUMERIC(5,2),
                angular_velocity_y NUMERIC(5,2),
                angular_velocity_z NUMERIC(5,2),
                linear_acceleration_x NUMERIC(24,19),
                linear_acceleration_y NUMERIC(24,19),
                linear_acceleration_z NUMERIC(24,19),
                time varchar(50)
             )
        """.trimIndent()
        connection.prepareStatement(createTable).execute()
    }

    private fun createCmdVelTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS cmd_vel (      
                linear_x NUMERIC(5,2),
                linear_y NUMERIC(5,2),
                linear_z NUMERIC(5,2),
                angular_x NUMERIC(5,2),
                angular_y NUMERIC(5,2),
                angular_z NUMERIC(5,2),
                time varchar(50)
             )
        """.trimIndent()
        connection.prepareStatement(createTable).execute()
    }

    private fun createWheelsTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS wheels (      
                right_wheel_position NUMERIC(24,19),
                left_wheel_position NUMERIC(24,19),
                right_wheel_velocity NUMERIC(5,2),
                left_wheel_velocity NUMERIC(5,2),
                right_wheel_effort NUMERIC(24,19),
                left_wheel_effort NUMERIC(24,19),
                time varchar(50)
             )
        """.trimIndent()
        connection.prepareStatement(createTable).execute()
    }

    private fun createOdomTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS odom (      
                pose_position_x NUMERIC(24,19),
                pose_position_y NUMERIC(24,19),
                pose_position_z NUMERIC(24,19),
                pose_orientation_x NUMERIC(24,19),
                pose_orientation_y NUMERIC(24,19),
                pose_orientation_z NUMERIC(24,19),
                pose_orientation_w NUMERIC(5,2),
                twist_linear_x NUMERIC(24,19),
                twist_linear_y NUMERIC(24,19),
                twist_linear_z NUMERIC(24,19),
                twist_angular_x NUMERIC(24,19),
                twist_angular_y NUMERIC(24,19),
                twist_angular_z NUMERIC(24,19),
                child_frame_id varchar(100),
                time varchar(50)
             )
        """.trimIndent()
        connection.prepareStatement(createTable).execute()
    }

    //TODO Investigate Data more
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

    private fun createWheelTransformTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS wheel_transform (      
                left_wheel_translation_x NUMERIC(6,3),
                left_wheel_translation_y NUMERIC(6,3),
                left_wheel_translation_z NUMERIC(6,3),
                right_wheel_translation_x NUMERIC(6,3),
                right_wheel_translation_y NUMERIC(6,3),
                right_wheel_translation_z NUMERIC(6,3),
                left_wheel_rotation_x NUMERIC(24,19),
                left_wheel_rotation_y NUMERIC(24,19),
                left_wheel_rotation_z NUMERIC(24,19),
                left_wheel_rotation_w NUMERIC(24,19),
                right_wheel_rotation_x NUMERIC(24,19),
                right_wheel_rotation_y NUMERIC(24,19),
                right_wheel_rotation_z NUMERIC(24,19),
                right_wheel_rotation_w NUMERIC(24,19),
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

    private fun createRosoutTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS rosout (
                level NUMERIC(2),
                name varchar(200),
                msg varchar(500),
                file varchar(200),
                function varchar(200),
                time varchar(50)
             )
        """.trimIndent()
        connection.prepareStatement(createTable).execute()
    }

    private fun createScanTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS scan (
                scan_time NUMERIC(24,19),
                angle_min NUMERIC(24,19),
                angle_max NUMERIC(24,19),
                angle_increment NUMERIC(24,19),
                range_min NUMERIC(24,19),
                range_max NUMERIC(24,19),
                time_increment NUMERIC(24,19),
                lowest_dist NUMERIC(24,19),
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
        if (options != null) return connection.prepareStatement("""SELECT * FROM ${property} ORDER BY time DESC ${options}""")
            .executeQuery()
        else return connection.prepareStatement("""SELECT * FROM ${property} ORDER BY time DESC""").executeQuery()
    }

    override fun getValueAtIndex(index: String, property: String): Any {
        TODO("Not yet implemented")
    }

    override fun getModel(): Any {
        TODO("Not yet implemented")
    }

    override fun getValue(id: String): Any {
        when (id) {
            "moveCmdVel" -> {
                return ObjectWithTime(data = localCmdVel, time = "")
            }

            "imuState" -> {
                val result = readFromModel("imu", "LIMIT 1")
                result.next()
                val imu = ImuBroadcaster(
                    AngularVelocity(
                        result.getDouble("angular_velocity_x"),
                        result.getDouble("angular_velocity_y"),
                        result.getDouble("angular_velocity_z")
                    ), emptyList(), null, LinearAcceleration(
                        result.getDouble("linear_acceleration_x"),
                        result.getDouble("linear_acceleration_y"),
                        result.getDouble("linear_acceleration_z")
                    ), emptyList(), Orientation(
                        result.getDouble("orientation_x"),
                        result.getDouble("orientation_y"),
                        result.getDouble("orientation_z"),
                        result.getDouble("orientation_w")
                    ), emptyList(), null
                )
                return ObjectWithTime(data = imu, result.getString("time"))
            }

            "cmdVel" -> {
                val result = readFromModel("cmd_vel", "LIMIT 1")
                result.next()
                val cmd = CmdVel(
                    Angular(
                        result.getDouble("angular_x"),
                        result.getDouble("angular_y"),
                        result.getDouble("angular_z")
                    ), Linear(
                        result.getDouble("linear_x"),
                        result.getDouble("linear_y"),
                        result.getDouble("linear_z")
                    ), null
                )
                return ObjectWithTime(cmd, result.getString("time"))
            }

            "batteryState" -> {
                val result = readFromModel("battery_state", "LIMIT 1")
                result.next()
                val bat = BatteryState(
                    result.getDouble("voltage"),
                    result.getDouble("percentage"),
                    result.getDouble("design_capacity"),
                    result.getDouble("present")
                )
                return ObjectWithTime(bat, result.getString("time"))
            }
            "wheelState" -> {
                val result = readFromModel("wheels", "LIMIT 1")
                result.next()
                val bat = WheelState(
                    result.getDouble("right_wheel_position"),
                    result.getDouble("left_wheel_position"),
                    result.getDouble("right_wheel_velocity"),
                    result.getDouble("left_wheel_velocity"),
                    result.getDouble("right_wheel_effort"),
                    result.getDouble("left_wheel_effort"),
                )
                return ObjectWithTime(bat, result.getString("time"))
            }
            "rosState" -> {
                val result = readFromModel("rosout", "LIMIT 1")
                result.next()
                val ros = result.getString("msg")
                return ObjectWithTime(ros, result.getString("time"))
            }

            "distance" -> {
                val result = readFromModel("scan", "LIMIT 1")
                result.next()
                return result.getDouble("lowest_dist")
            }

            "distanceLimit" -> {
                return distanceLimit
            }

            else -> return {}
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
        if (id == "dynamicJointState") {
            val djs = gson.fromJson(value as String, DynamicJointState::class.java)
            val values = helperMethods.getBatteryValues(djs)
            if (values != null) writeToModel("battery_state", values)
        }
        if (id == "turtlebot_movement") {
            localCmdVel = value as CmdVel
            sendNewDataPointEvent()
            val input = helperMethods.createTableInsertString(listOf(id, "TurtlebotDT", value.toString()))
            writeToModel("service_changes", input)
        }
        if (id == "cmdVel") {
            val cmd = gson.fromJson(value as String, CmdVel::class.java)
            val values = helperMethods.getCmdVelValues(cmd)
            if (values != null) writeToModel("cmd_vel", values)
        }
        if (id == "diffDriveTES") {
            return
        }
        if (id == "imuBroadcasterTES") {
            return
        }
        if (id == "jointStateBroadcasterTES") {
            return
        }
        if (id == "imuBroadcaster") {
            val ib = gson.fromJson(value as String, ImuBroadcaster::class.java)
            val values = helperMethods.getImuValues(ib)
            writeToModel("imu", values)
        }
        if (id == "jointStates") {
            val js = gson.fromJson(value as String, JointStates::class.java)
            val values = helperMethods.getWheelsValues(js)
            if (values != null) writeToModel("wheels", values)
        }
        if (id == "odom") {
            val o = gson.fromJson(value as String, Odom::class.java)
            val values = helperMethods.getOdomValues(o)
            writeToModel("odom", values)
        }
        if (id == "parameterEvent") {
            val pe = gson.fromJson(value as String, ParameterEvent::class.java)
            val values = helperMethods.getParameterEventsValues(pe)
            writeToModel("parameter_event", values)
        }
        if (id == "tf") {
            val tf = gson.fromJson(value as String, TF::class.java)
            val values = helperMethods.getWheelTransformValues(tf)
            if (values != null) writeToModel("wheel_transform", values)
        }
        if (id == "tfStatic") {
            val tfs = gson.fromJson(value as String, TFStatic::class.java)
            val values = helperMethods.getStaticFrameTransformations(tfs)
            values.forEach { writeToModel("static_transform", it) }
        }
        if (id == "rosout") {
            val ro = gson.fromJson(value as String, Rosout::class.java)
            val values = helperMethods.getRosoutValues(ro)
            //writeToModel("rosout", values)
        }
        if (id == "scan") {
            val s = gson.fromJson(value as String, Scan::class.java)
            val values = helperMethods.getScanValues(s)
            writeToModel("scan", values)
        }

        if (id == "distanceLimit") {
            distanceLimit = value as Double
        }

    }

    override val lastPropertyUpdateMap: Map<String, Long>
        get() = TODO("Not yet implemented")

}