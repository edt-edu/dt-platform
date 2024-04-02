import classes.Control
import classes.WafflePiState
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

private val logger = KotlinLogging.logger {}

class WafflePiDTGateway : AbstractGateway(
    propertyIDs = setOf(
        "batteryState", "wafflePiMovement", "errors", "wheel_state", "position", "wafflePiState", "controlWafflePi", "limitDistance"
    ), gatewayID = "WafflePiDTGateway"
) {
    //private val url = "http://localhost:8989/"
    private val url = "http://host.docker.internal:8989/"
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

    private fun sendControlToWafflePiDT(data: Control, url: String) {
        logger.info("Send to WafflePiDT...$data")
        val jsonObject = JSONObject()
        try {
            jsonObject.put("direction", data.direction)
            jsonObject.put("speed", data.speed)
            jsonObject.put("continuous", data.continuous)

            val mediaType = "application/json".toMediaType()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            instance.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    val error = ErrorEvent(source = "WafflePi")
                    error.message = e.message!!
                    sendError(error)
                }

                override fun onResponse(call: Call, response: Response) {
                    logger.info("Moving WafflePi Response:" + response.code)
                    response.body?.close()

                }
            })
        } catch (e: Exception) {
            logger.error { e.printStackTrace() }
        }
    }

    private fun sendDistanceLimitToWafflePiDT(data: Double, url: String) {
        logger.info("Send to WafflePiDT...$data")
        val jsonObject = JSONObject()
        try {
            jsonObject.put("distanceLimit", data)

            val mediaType = "application/json".toMediaType()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            instance.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    val error = ErrorEvent(source = "WafflePi")
                    error.message = e.message!!
                    sendError(error)
                }

                override fun onResponse(call: Call, response: Response) {
                    logger.info("WafflePi DT Response:" + response.code)
                    response.body?.close()

                }
            })
        } catch (e: Exception) {
            logger.error { e.printStackTrace() }
        }
    }

    override fun getDataFromSource() {
        logger.info { "Get data from source..." }
        try {
            val propertyUrl = url + "wafflePiState"
            val request = Request.Builder()
                .url(propertyUrl)
                .build()

            instance.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    val error = ErrorEvent(source = "WafflePiDT")
                    error.message = e.message!!
                    sendError(error)
                }

                override fun onResponse(call: Call, response: Response) {
                    val data = response.body!!.string()
                    val gson = Gson()
                    val state = gson.fromJson(data, WafflePiState::class.java)

                    //Only send new datapoint if its a new one
                    dataMap["wafflePiState"] = state

                    sendNewDataPointEvent()

                }
            })
        } catch (e: Exception) {
            val error = ErrorEvent(source = "Turtlebot")
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
        dataMap[property] = value
        when (property) {
            "controlWafflePi" -> {
                sendControlToWafflePiDT(value as Control, url + "moveTurtleBot")
            }
            "limitDistance" -> {
                sendDistanceLimitToWafflePiDT(value as Double, url + "setDistanceLimit")
            }
        }
    }

    override fun getLastUpdate(property: String): String? {
        return null
    }

    override val lastPropertyUpdateMap: Map<String, Long>
        get() = TODO("Not yet implemented")
}