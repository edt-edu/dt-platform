import classes.*
import dts.connection.SynchronizationDirection
import dts.events.ErrorEvent
import dts.events.NewDataPointEvent
import dts.events.observer.IGatewayObserver
import dts.gateway.AbstractGateway
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.json.JSONObject
import java.time.LocalDateTime
import kotlin.math.abs

class TurtlebotDTGateway : AbstractGateway(
    propertyIDs = setOf(
        "cmd_vel", "turtlebot_movement", "batteryState", "imuState", "rosState", "wheelState", "distanceLimit", "distance"
    ), gatewayID = "TurtlebotDTGateway"
) {

    private var cmdVel: ObjectWithTime = ObjectWithTime(
        data = CmdVel(Angular(x = 0.0, y = 0.0, z = 0.0), Linear(x = 0.0, y = 0.0, z = 0.0), null),
        time = "null"
    )
    private var batteryState: ObjectWithTime = ObjectWithTime(data = BatteryState(0.0, 0.0, 0.0, 0.0), time = "null");
    private var imuState: ObjectWithTime? = null
    private var rosState: ObjectWithTime? = null
    private var wheelState: ObjectWithTime? = null
    private var newData = false
    private var continuous = false

    private var distanceLimit: Double? = null
    private var distance: Double? = null

    private val dataMap: MutableMap<String, Any?> = mutableMapOf()


    private val thread = Thread(Runnable {
        getDataFromSource()
    })

    private var continuousThread  = Thread(Runnable {
        while (true) {
            if (continuous) {
                newData = true
                sendNewDataPointEvent()
            }
            Thread.sleep(100)
        }
    })

    override fun listen() {
        thread.start()
        continuousThread.start()
    }

    init {
        listen()
    }


    private fun sendNewDataPointEvent() {
        logInfo("Send new data point event")
        for (igw: IGatewayObserver in observer) {
            // setup the event
            val ex = NewDataPointEvent(this)
            ex.sourceID = gatewayID
            ex.timestamp = LocalDateTime.now()
            ex.synchronizationDirection = SynchronizationDirection.GATEWAY_TO_DT

            // let the listener handle it
            igw.handleDatapointEvent(ex)
        }
    }


    override fun sendError(error: ErrorEvent) {
        for (igw: IGatewayObserver in observer) {
            val ex = ErrorEvent(this)
            ex.sourceID = gatewayID
            ex.timestamp = LocalDateTime.now()
            ex.message = error.message
            igw.handleError(ex)
        }
    }

    override fun getDataFromSource() {
        val port = 8989;

        val server = embeddedServer(Netty, port) {
            routing {
                post("/moveTurtleBot") {
                    try {

                        val postJson = JSONObject(call.receive<String>())
                        logger.info { postJson }
                        val direction = postJson.getString("direction")
                        val speed = postJson.getInt("speed")
                        continuous = postJson.getBoolean("continuous")
                        moveTurtleBot(direction, speed)
                        call.respondText { "Success" }
                    } catch (e: Exception) {
                        call.respondText { "Error" }
                    }

                }
                post("/setDistanceLimit") {
                    try {

                        val postJson = JSONObject(call.receive<String>())
                        logger.info { postJson }
                        distanceLimit = postJson.getDouble("distanceLimit")
                        sendNewDataPointEvent()
                        call.respondText { "Success" }
                    } catch (e: Exception) {
                        call.respondText { "Error" }
                    }

                }
                get("/wafflePiState") {
                    try {
                        val battery = batteryState.data as BatteryState
                        val cmd = cmdVel.data as CmdVel

                        var orientation = Orientation(0.0,0.0,0.0,0.0)
                        var orientationTime = ""
                        if (imuState != null) {
                            orientation = (imuState!!.data as ImuBroadcaster).orientation
                            orientationTime = imuState!!.time
                        }
                        var ros = ""
                        var rosTime = ""
                        if (rosState != null) {
                            ros = rosState!!.data as String
                            rosTime = rosState!!.time
                        }

                        var rightWheelPosition = 0.0
                        var leftWheelPosition = 0.0
                        var rightWheelVelocity = 0.0
                        var leftWheelVelocity = 0.0
                        var wheelStateTime = ""
                        if (wheelState != null) {
                            val wheels = wheelState!!.data as WheelState
                            rightWheelPosition = wheels.rightWheelPosition
                            leftWheelPosition = wheels.leftWheelPosition
                            rightWheelVelocity = wheels.rightWheelVelocity
                            leftWheelVelocity = wheels.leftWheelVelocity
                            wheelStateTime = wheelState!!.time
                        }

                        val result = JSONObject()

                        val wheelResult = JSONObject()
                        wheelResult.put("rightWheelPosition", rightWheelPosition)
                        wheelResult.put("leftWheelPosition", leftWheelPosition)
                        wheelResult.put("rightWheelVelocity", rightWheelVelocity)
                        wheelResult.put("leftWheelVelocity", leftWheelVelocity)
                        wheelResult.put("time", wheelStateTime)
                        result.put("wheelState", wheelResult)

                        val rosResult = JSONObject()
                        rosResult.put("msg", ros)
                        rosResult.put("time", rosTime)
                        result.put("ros", rosResult)

                        val orientationResult = JSONObject()
                        orientationResult.put("x", orientation.x)
                        orientationResult.put("y", orientation.y)
                        orientationResult.put("z", orientation.z)
                        orientationResult.put("w", orientation.w)
                        orientationResult.put("time", orientationTime)
                        result.put("orientation", orientationResult)

                        val batteryResult = JSONObject()
                        batteryResult.put("percentage", battery.percentage)
                        batteryResult.put("time", batteryState.time)
                        result.put("batteryState", batteryResult)

                        val cmdResult = JSONObject()
                        var linear = if (cmd.linear!!.x > 0) "Forwards" else "Backwards"
                        var angular = if (cmd.angular!!.z > 0) "Left" else "Right"
                        val speed = if (abs(cmd.linear!!.x) > abs(cmd.angular!!.z)) cmd.linear!!.x else cmd.angular!!.z
                        if (cmd.linear!!.x.equals(0.0)) {
                            linear = "Stop"
                        }
                        if (cmd.angular!!.z.equals(0.0)) {
                            angular = "Stop"
                        }
                        cmdResult.put("linear", linear)
                        cmdResult.put("angular", angular)
                        cmdResult.put("speed", abs(speed))
                        cmdResult.put("time", cmdVel.time)
                        result.put("movement", cmdResult)

                        result.put("distance", distance)


                        call.respondText(result.toString())

                    } catch (e: Exception) {
                        logger.error { e.printStackTrace() }
                    }
                }
            }
        }
        server.start()
    }

    private fun moveTurtleBot(direction: String, speed: Int) {
        var delta = 0.1
        if (speed > 0) {
            delta = speed * 0.1
        }
        when (direction) {
            "UP" -> {
                (cmdVel.data as CmdVel).linear!!.x = delta
            }

            "DOWN" -> {
                (cmdVel.data as CmdVel).linear!!.x = delta * -1
            }

            "LEFT" -> {
                (cmdVel.data as CmdVel).angular!!.z = delta
            }

            "RIGHT" -> {
                (cmdVel.data as CmdVel).angular!!.z = delta * -1
            }

            "STOP" -> {
                cmdVel.data = CmdVel(Angular(x = 0.0, y = 0.0, z = 0.0), Linear(x = 0.0, y = 0.0, z = 0.0), topic = "")
            }
        }
        newData = true
        sendNewDataPointEvent()
    }

    override fun getValue(property: String): Any? {
        if (property == "turtlebot_movement" && newData) {
            newData = false
            return cmdVel.data
        }
        if (property == "distanceLimit" && distanceLimit != null) {
            return distanceLimit
        }
        return null
    }

    override fun getAllValues(): Map<String, Any?> {
        return dataMap
    }

    override fun setValue(property: String, value: Any?) {
        if (value == null) {
            return
        }
        when (property) {
            "cmd_vel" -> {
                if (!continuous) cmdVel = value as ObjectWithTime
            }
            "batteryState" -> {
                batteryState = value as ObjectWithTime
            }
            "imuState" -> {
                imuState = value as ObjectWithTime
            }
            "rosState" -> {
                rosState = value as ObjectWithTime
            }
            "wheelState" -> {
                wheelState = value as  ObjectWithTime
            }
            "distance" -> {
                distance = value as Double
            }
        }

    }

    override fun getLastUpdate(property: String): String? {
        return null
    }

    override val lastPropertyUpdateMap: Map<String, Long>
        get() = TODO("Not yet implemented")
}