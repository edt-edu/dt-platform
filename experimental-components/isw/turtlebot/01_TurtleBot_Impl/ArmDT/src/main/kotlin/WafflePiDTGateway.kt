import classes.BatteryState
import com.google.gson.Gson
import dts.connection.SynchronizationDirection
import dts.events.ErrorEvent
import dts.events.NewDataPointEvent
import dts.events.observer.IGatewayObserver
import dts.gateway.AbstractGateway
import mu.KotlinLogging
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class WafflePiDTGateway : AbstractGateway(
    propertyIDs = setOf(
        "batteryState", "errors"
    ), gatewayID = "WafflePiDTGateway"
) {
    //private val url = "http://localhost:8989/"
    private val url = "http://host.docker.internal:6565/"
    private val requestInterval: Long = 500
    private val instance = OkHttpClient().newBuilder().connectTimeout(2, TimeUnit.SECONDS).build()


    private val dataMap: MutableMap<String, Any?> = mutableMapOf()


    private val thread = Thread(Runnable {
        while (true) {
            getDataFromSource()
            Thread.sleep(requestInterval)
        }

    })

    override fun listen() {
        thread.start()
    }

    init {
        listen()
    }


    private fun sendNewDataPointEvent() {
        logger.info { "Send new data point event" }
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
        logger.info { "Get data from WafflePiDT..." }
        try {
            val propertyUrl = url + "batteryState"
            val request = Request.Builder()
                .url(propertyUrl)
                .build()

            instance.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    val error = ErrorEvent(source = "Arm")
                    error.message = e.message!!
                    sendError(error)
                }

                override fun onResponse(call: Call, response: Response) {
                    val data = response.body!!.string()
                    val gson = Gson()
                    val state = gson.fromJson(data, BatteryState::class.java)

                    //Only send new datapoint if its a new one
                    dataMap["batteryState"] = state

                    sendNewDataPointEvent()

                }
            })
        } catch (e: Exception) {
            val error = ErrorEvent(source = "Arm")
            error.message = e.message!!
            sendError(error)
            logger.error { e.printStackTrace() }
        }


    }

    override fun getValue(property: String): Any? {
        return dataMap[property]
    }

    override fun getAllValues(): Map<String, Any?> {
        return dataMap
    }

    override fun setValue(property: String, value: Any?) {
        if (value == null) {
            return
        }
    }

    override fun getLastUpdate(property: String): String? {
        return null
    }

    override val lastPropertyUpdateMap: Map<String, Long>
        get() = TODO("Not yet implemented")
}