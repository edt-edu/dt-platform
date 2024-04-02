import classes.*
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
    propertyIDs = setOf(
        "battery_state", "wafflePiMovement", "errors", "controlWafflePi", "wafflePiState", "controlArm", "limitDistance", "armState"
    )
) {
    private val user = "postgres"
    private val password = "1234"
    //val jdbcUrl = "jdbc:postgresql://localhost:5434/"
    val jdbcUrl = "jdbc:postgresql://postgres:5434/"
    var connection: Connection

    val helperMethods = ModelManagerHelperMethods()

    private var controlCache: Control? = null

    private var controlArm: String? = null

    private var distanceLimit: Double? = null


    init {
        Class.forName("org.postgresql.Driver")
        connection = DriverManager.getConnection(jdbcUrl, user, password)
        logger.info { "Check if Connection to Postgres valid: " + connection.isValid(0) }

        initTurtlebotDatabase(connection)

        connection.close()

        connection = DriverManager.getConnection(jdbcUrl + "turtlebot", user, password)

        createControlTable(connection)

        createUserInputTable(connection)

        createWafflePiStateTable(connection)

        createArmStateTable(connection)

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

    private fun initTurtlebotDatabase(connection: Connection) {
        val createDatabase = """
            CREATE DATABASE turtlebot
            WITH 
                ALLOW_CONNECTIONS = true""".trimIndent()
        try {
            connection.prepareStatement(createDatabase)?.execute()
        } catch (e: SQLException) {
            logger.error { e.message }
        }

    }

    private fun createUserInputTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS user_input (
                property varchar(100),
                changes varchar(400),
                time varchar(50)
             )
        """.trimIndent()
        connection.prepareStatement(createTable).execute()
    }

    private fun createControlTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS control (
                direction varchar(10),
                speed smallint,
                continuous varchar(10),
                time varchar(50)
             )
        """.trimIndent()
        connection.prepareStatement(createTable).execute()
    }

    private fun createWafflePiStateTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS waffle_pi_state (
                linear varchar(10),
                angular varchar(10),
                speed Numeric(5,2),
                last_movement_time varchar(50),
                battery_percentage Numeric(5,2),
                last_battery_time varchar(50),
                orientation_x NUMERIC(24,19),
                orientation_y NUMERIC(24,19),
                orientation_z NUMERIC(24,19),
                orientation_w NUMERIC(24,19),
                last_orientation_time varchar(50),
                ros_msg varchar(500),
                last_ros_time varchar(50),
                right_wheel_position NUMERIC(24,19),
                left_wheel_position NUMERIC(24,19),
                right_wheel_velocity NUMERIC(5,2),
                left_wheel_velocity NUMERIC(5,2),
                last_wheel_time varchar(50),
                distance NUMERIC(24,19),
                time varchar(50)
             )
        """.trimIndent()
        connection.prepareStatement(createTable).execute()
    }

    private fun createArmStateTable(connection: Connection) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS arm_state (
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
        try {
            writeToTable(property, value)
        } catch(e: Exception) {
            logger.error { e.printStackTrace() }
        }
        
    }

    override fun readFromModel(property: String, options: Any?): ResultSet {
        logger.debug { "Read ${property} from model with options: ${options}" }
        return if (options != null) connection.prepareStatement("""SELECT * FROM ${property} ORDER BY time DESC ${options}""")
            .executeQuery()
        else connection.prepareStatement("""SELECT * FROM ${property} ORDER BY time DESC""").executeQuery()
    }

    override fun getValueAtIndex(index: String, property: String): Any {
        TODO("Not yet implemented")
    }

    override fun getModel(): Any {
        TODO("Not yet implemented")
    }

    override fun getValue(id: String): Any? {
        try {
            when (id) {
                "controlWafflePi" -> {
                    val temp = controlCache
                    controlCache = null
                    return temp
                }
    
                "controlArm" -> {
                    val temp = controlArm
                    controlArm = null
                    return temp
                }
    
                "wafflePiState" -> {
                    val result = readFromModel("waffle_pi_state", "LIMIT 1")
                    result.next()
                    return WafflePiState(
                        BatteryState(
                            result.getDouble("battery_percentage"),
                            result.getString("last_battery_time")
                        ), Movement(
                            result.getString("angular"),
                            result.getString("linear"),
                            result.getDouble("speed"),
                            result.getString("last_movement_time")
                        ),
                        Orientation(
                            result.getString("last_movement_time"),
                            result.getDouble("orientation_x"),
                            result.getDouble("orientation_y"),
                            result.getDouble("orientation_z"),
                            result.getDouble("orientation_w")
                        ),
                        Ros(
                            result.getString("ros_msg"),
                            result.getString("last_ros_time")
                        ),
                        WheelState(
                            result.getDouble("left_wheel_velocity"),
                            result.getDouble("right_wheel_velocity"),
                            result.getDouble("left_wheel_position"),
                            result.getDouble("right_wheel_position"),
                            result.getString("last_wheel_time")
                        ),
                        result.getDouble("distance")
                    )
                }
                "armState" -> {
                    val result = readFromModel("arm_state", "LIMIT 6")
                    val jointStates: MutableList<JointState> = mutableListOf()
                    while (result.next()) {
                        val jointState = JointState(
                            result.getString("joint"),
                            result.getDouble("joint_effort"),
                            result.getDouble("joint_position"),
                            result.getDouble("joint_velocity")
                        )
                        jointStates.add(jointState)
                    }
                    return ArmState(jointStates)
                }
    
                "limitDistance" -> {
                    return distanceLimit
                }
    
                else -> return null
            } 
        }catch(e:Exception) {
            logger.error { e.printStackTrace()}
            return null
        }
    }
    
        override fun getAllValues(): Map<String, Any> {
            return emptyMap()
        }
    
    
        override fun manualSetValue(id: String, value: Any, serviceName: String) {
            setValue(id, value)
            sendNewDataPointEvent()
            val input = helperMethods.createTableInsertString(listOf(id, value.toString()))
            writeToModel("user_input", input)
        }
    
        override fun setValue(id: String, value: Any?) {
            if (value == "{}" || value == null) {
                return
            }
            when (id) {
                "control" -> {
                    if (value is Control) {
                        controlCache = value
                        val values = helperMethods.getControlValues(value)
                        writeToModel("control", values)
                    }
                }
    
                "wafflePiState" -> {
                    val values = helperMethods.getWafflePiStateValues(value as WafflePiState)
                    writeToModel("waffle_pi_state", values)
                }
                "armState" -> {
                    val values = helperMethods.getArmStateValues(value as ArmState)
                    values.forEach { writeToModel("arm_state", it) }
                }
                "armControl" -> {
                    controlArm = value as String
                }
    
                "distanceLimit" -> {
                    distanceLimit = value as Double
                }
            }
        }

    override val lastPropertyUpdateMap: Map<String, Long>
        get() = TODO("Not yet implemented")

}