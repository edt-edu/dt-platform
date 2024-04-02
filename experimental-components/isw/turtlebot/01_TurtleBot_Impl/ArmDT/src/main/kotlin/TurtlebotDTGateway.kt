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
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime

class TurtlebotDTGateway : AbstractGateway(
    propertyIDs = setOf(
        "armDeltaJoint","armDeltaTwist", "joint_states"
    ), gatewayID = "TurtlebotDTGateway"
) {

    private var newDataJoint = false
    private var newDataTwist = false


    private val jointJog = ServoDeltaJoint(emptyList(), 0.0, Header("link2", Stamp(0,0)), mutableListOf(), "", mutableListOf())
    private val deltaTwist = ServoDeltaTwist(Header("link2", Stamp(0,0)),"", Twist(Angular(x = 0.0, y = 0.0, z = 0.0), Linear(x = 0.0, y = 0.0, z = 0.0)))

    private var jointStates: List<JointStateRow>? = null

    private val thread = Thread(Runnable {
        getDataFromSource()
    })

    override fun listen() {
        thread.start()
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
        val port = 7878;

        val server = embeddedServer(Netty, port) {
            routing {
                post("/moveArm") {
                    try {

                        val postJson = JSONObject(call.receive<String>())
                        logger.info { postJson }
                        val direction = postJson.getString("direction")
                        moveArm(direction)
                        call.respondText { "Success" }
                    } catch (e: Exception) {
                        call.respondText { "Error" }
                    }

                }

                get("/armState") {
                    try {
                        val result = JSONObject()
                        val jointStatesResult = JSONArray()
                        jointStates?.forEach { js ->
                            val re = JSONObject()
                            re.put("joint", js.joint)
                            re.put("jointPosition", checkNaN(js.jointPosition))
                            re.put("jointEffort", checkNaN(js.jointEffort))
                            re.put("jointVelocity", checkNaN(js.jointVelocity))
                            jointStatesResult.put(re)
                        }

                        result.put("jointStates", jointStatesResult)

                        call.respondText(result.toString())

                    } catch (e: Exception) {
                        logger.error { e.printStackTrace() }
                    }
                }
            }
        }
        server.start()
    }

    private fun checkNaN(value: Double?): Double {
        if (value == null || value.isNaN()) return 0.0
        return value
    }

    private fun moveArm(direction: String) {
        val delta = 0.2
        var vel = 1.0
        when (direction) {
            "UP" -> {
                (deltaTwist.twist).linear.z = delta
                newDataTwist = true
            }

            "DOWN" -> {
                (deltaTwist.twist).linear.z = delta * -1
                newDataTwist = true
            }

            "FRONT" -> {
                (deltaTwist.twist).linear.x = delta
                newDataTwist = true
            }

            "BACK" -> {
                (deltaTwist.twist).linear.x = delta * -1
                newDataTwist = true
            }
            "RIGHT" -> {
                jointJog.joint_names = mutableListOf("joint1")
                jointJog.velocities = mutableListOf(vel * -1)
                newDataJoint = true
            }
            "LEFT" -> {
                jointJog.joint_names = mutableListOf("joint1")
                jointJog.velocities = mutableListOf(vel)
                newDataJoint = true
            }
            "GRIPPER_UP" -> {
                jointJog.joint_names = mutableListOf("joint4")
                jointJog.velocities = mutableListOf(vel * -1)
                newDataJoint = true
            }
            "GRIPPER_DOWN" -> {
                jointJog.joint_names = mutableListOf("joint4")
                jointJog.velocities = mutableListOf(vel)
                newDataJoint = true
            }
        }
        sendNewDataPointEvent()
    }

    override fun getValue(property: String): Any? {
        if (property == "armDeltaJoint" && newDataJoint) {
            newDataJoint = false
            return jointJog
        }
        if (property == "armDeltaTwist" && newDataTwist) {
            newDataTwist = false
            return deltaTwist
        }
        return null
    }

    override fun getAllValues(): Map<String, Any?> {
        return emptyMap()
    }

    override fun setValue(property: String, value: Any?) {
        if (value == null) {
            return
        }
        when (property) {
            "joint_states" -> {
                jointStates = value as List<JointStateRow>
            }
        }

    }

    override fun getLastUpdate(property: String): String? {
        return null
    }

    override val lastPropertyUpdateMap: Map<String, Long>
        get() = TODO("Not yet implemented")
}