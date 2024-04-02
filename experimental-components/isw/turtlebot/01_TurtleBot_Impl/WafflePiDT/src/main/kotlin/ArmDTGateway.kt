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

class ArmDTGateway : AbstractGateway(
    propertyIDs = setOf(
        "armBatteryState", "errors"
    ), gatewayID = "ArmDTGateway"
) {

    private var batteryState: ObjectWithTime = ObjectWithTime(data = BatteryState(0.0, 0.0, 0.0, 0.0), time = "null");

    private val dataMap: MutableMap<String, Any?> = mutableMapOf()


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
        val port = 6565;

        val server = embeddedServer(Netty, port) {
            routing {

                get("/batteryState") {
                    try {
                        val battery = batteryState.data as BatteryState

                        val result = JSONObject()

                        val batteryResult = JSONObject()
                        batteryResult.put("percentage", battery.percentage)
                        batteryResult.put("time", batteryState.time)
                        result.put("batteryState", batteryResult)

                        call.respondText(result.toString())

                    } catch (e: Exception) {
                        logger.error { e.printStackTrace() }
                    }
                }
            }
        }
        server.start()
    }

    override fun getValue(property: String): Any? {
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
            "batteryState" -> {
                batteryState = value as ObjectWithTime
            }
        }

    }

    override fun getLastUpdate(property: String): String? {
        return null
    }

    override val lastPropertyUpdateMap: Map<String, Long>
        get() = TODO("Not yet implemented")
}