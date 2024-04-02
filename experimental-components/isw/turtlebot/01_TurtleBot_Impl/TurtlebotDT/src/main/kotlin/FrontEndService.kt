import classes.ArmState
import classes.Control
import classes.WafflePiState
import com.google.gson.Gson
import dts.DigitalTwinEngine
import dts.connection.SynchronizationDirection
import dts.events.ServiceRequestEvent
import dts.services.AbstractServiceConnection
import dts.services.IRestService
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import mu.KotlinLogging
import org.json.JSONObject
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

class FrontEndService : AbstractServiceConnection(true, "FrontEndService"),
    IRestService {

    private var direction: String? = null
    private var armDirection: String? = null
    private var speed: Int? = null
    private var continuous = false
    private var control: Control = Control(false, null, 0)
    private var wafflePiState: WafflePiState? = null
    private var armState: ArmState? = null
    private val requestInterval: Long = 500
    private var distanceLimit = 0.1

    private val stateThread = Thread {
        while (true) {
            createNewServiceEvent("getWafflePiState")
            createNewServiceEvent("getArmState")
            Thread.sleep(requestInterval)
        }
    }
    private val thread = Thread {
        val port = 7000

        val server = embeddedServer(Netty, port) {
            setup()
        }
        server.start()
    }

    private fun Application.setup() {
        install(CORS) {
            header(HttpHeaders.AccessControlAllowOrigin)
            header(HttpHeaders.ContentType)
            anyHost()
        }
        routing {
            post("/direction") {
                try {
                    val postJson = JSONObject(call.receive<String>())
                    direction = postJson.getString("direction")
                    createNewServiceEvent("setDirection")
                    call.respond<String>("""{"Success":"true"}""")
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond<String>("""{"Success":"false", "Reason": ${e.message}}""")
                }
            }
            post("/speed") {
                try {
                    val postJson = JSONObject(call.receive<String>())
                    speed = postJson.getInt("speed")
                    createNewServiceEvent("setSpeed")
                    call.respond<String>("""{"Success":"true"}""")
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond<String>("""{"Success":"false", "Reason": ${e.message}}""")
                }
            }
            post("/distance") {
                try {
                    val postJson = JSONObject(call.receive<String>())
                    distanceLimit = postJson.getDouble("distance")
                    createNewServiceEvent("setDistanceLimit")
                    call.respond<String>("""{"Success":"true"}""")
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond<String>("""{"Success":"false", "Reason": ${e.message}}""")
                }
            }
            post("/continuous") {
                try {
                    val postJson = JSONObject(call.receive<String>())
                    continuous = postJson.getBoolean("continuous")
                    createNewServiceEvent("setContinuous")
                    call.respond<String>("""{"Success":"true"}""")
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond<String>("""{"Success":"false", "Reason": ${e.message}}""")
                }
            }
            post("/moveArm") {
                try {
                    val postJson = JSONObject(call.receive<String>())
                    armDirection = postJson.getString("armDirection")
                    createNewServiceEvent("moveArm")
                    call.respond<String>("""{"Success":"true"}""")
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond<String>("""{"Success":"false", "Reason": ${e.message}}""")
                }
            }
            get("/turtleBotState") {
                try {
                    val gson = Gson()
                    val wafflePiResult = JSONObject(gson.toJson(wafflePiState).toString())
                    val armResult = JSONObject(gson.toJson(armState).toString())
                    val result = JSONObject()
                    result.put("wafflePiState", wafflePiResult)
                    result.put("armState", armResult)
                    call.respondText(result.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond<String>("""{"Success":"false", "Reason": ${e.message}}""")
                }
            }
        }
    }

    override fun listen() {
        thread.start()
        stateThread.start()
        logger.info { "$serviceID started" }
    }

    init {
        listen()
    }

    override fun executeTask(taskToExecute: String, digitalTwinEngine: DigitalTwinEngine) {
        logger.info { "Executing task $taskToExecute" }
        when (taskToExecute) {
            "setDirection" -> {
                val modelManager =
                    digitalTwinEngine.modelManagerSet.find { abstractModelManager ->
                        abstractModelManager.responsibleForID(
                            "wafflePiMovement"
                        )
                    }

                control.direction = direction
                modelManager?.manualSetValue("control", control, serviceID)
            }

            "setSpeed" -> {
                val modelManager =
                    digitalTwinEngine.modelManagerSet.find { abstractModelManager ->
                        abstractModelManager.responsibleForID(
                            "wafflePiMovement"
                        )
                    }

                control.speed = speed
                modelManager?.manualSetValue("control", control, serviceID)
            }

            "setDistanceLimit" -> {
                val modelManager =
                    digitalTwinEngine.modelManagerSet.find { abstractModelManager ->
                        abstractModelManager.responsibleForID(
                            "wafflePiMovement"
                        )
                    }
                modelManager?.manualSetValue("distanceLimit", distanceLimit, serviceID)
            }

            "setContinuous" -> {
                val modelManager =
                    digitalTwinEngine.modelManagerSet.find { abstractModelManager ->
                        abstractModelManager.responsibleForID(
                            "wafflePiMovement"
                        )
                    }

                control.continuous = continuous
                modelManager?.manualSetValue("control", control, serviceID)
            }

            "moveArm" -> {
                val modelManager =
                    digitalTwinEngine.modelManagerSet.find { abstractModelManager ->
                        abstractModelManager.responsibleForID(
                            "controlArm"
                        )
                    }
                modelManager?.manualSetValue("armControl", armDirection!!, serviceID)
            }

            "getWafflePiState" -> {
                val modelManager =
                    digitalTwinEngine.modelManagerSet.find { abstractModelManager ->
                        abstractModelManager.responsibleForID(
                            "wafflePiState"
                        )
                    }

                wafflePiState = modelManager?.getValue("wafflePiState") as WafflePiState
            }
            "getArmState" -> {
                val modelManager =
                    digitalTwinEngine.modelManagerSet.find { abstractModelManager ->
                        abstractModelManager.responsibleForID(
                            "armState"
                        )
                    }

                armState = modelManager?.getValue("armState") as ArmState
            }
        }



    }

    override fun serviceUpdate(digitalTwinEngine: DigitalTwinEngine, observedProperty: String) {
        TODO("Not yet implemented")
    }

    private fun createNewServiceEvent(task: String) {
        for (serviceListener in observer) {
            val serviceRequestEvent = ServiceRequestEvent(this)
            serviceRequestEvent.sourceID = serviceID
            serviceRequestEvent.timestamp = LocalDateTime.now()
            serviceRequestEvent.synchronizationDirection = SynchronizationDirection.GATEWAY_TO_DT
            serviceRequestEvent.message = task
            serviceListener.handleModelGet(serviceRequestEvent)
        }
    }
}