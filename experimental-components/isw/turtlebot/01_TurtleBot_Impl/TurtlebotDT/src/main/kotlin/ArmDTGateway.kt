import classes.ArmState
import classes.Control
import classes.WafflePiState
import com.google.gson.Gson
import dts.connection.SynchronizationDirection
import dts.events.ErrorEvent
import dts.events.NewDataPointEvent
import dts.events.observer.IGatewayObserver
import dts.gateway.AbstractGateway
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class ArmDTGateway : AbstractGateway(
    propertyIDs = setOf(
        "errors", "armState", "controlArm"
    ), gatewayID = "ArmDTGateway"
) {
    private val url = "http://host.docker.internal:7878/"
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

    private fun sendControlToArmDT(data: String, url: String) {
        logger.info("Send to Arm...$data")
        val jsonObject = JSONObject()
        try {
            jsonObject.put("direction", data)

            val mediaType = "application/json".toMediaType()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            instance.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    val error = ErrorEvent(source = "Arm")
                    error.message = e.message!!
                    sendError(error)
                }

                override fun onResponse(call: Call, response: Response) {
                    logger.info("Moving Arm Response:" + response.code)
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
            val propertyUrl = url + "armState"
            val request = Request.Builder()
                .url(propertyUrl)
                .build()

            instance.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    val error = ErrorEvent(source = "ArmDT")
                    error.message = e.message!!
                    sendError(error)
                }

                override fun onResponse(call: Call, response: Response) {
                    val data = response.body!!.string()
                    val gson = Gson()
                    val state = gson.fromJson(data, ArmState::class.java)

                    //Only send new datapoint if its a new one
                    dataMap["armState"] = state

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
        dataMap[property] = value
        when (property) {
            "controlArm" -> {
                sendControlToArmDT(value as String, url + "moveArm")
            }
        }
    }

    override fun getLastUpdate(property: String): String? {
        return null
    }

    override val lastPropertyUpdateMap: Map<String, Long>
        get() = TODO("Not yet implemented")
}