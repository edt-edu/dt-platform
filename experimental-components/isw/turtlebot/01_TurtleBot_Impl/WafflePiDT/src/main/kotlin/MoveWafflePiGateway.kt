import classes.Angular
import classes.CmdVel
import classes.Linear
import classes.ObjectWithTime
import dts.events.ErrorEvent
import dts.events.observer.IGatewayObserver
import dts.gateway.AbstractGateway
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class MoveWafflePiGateway : AbstractGateway(
    propertyIDs = setOf(
        "moveCmdVel",
    ), gatewayID = "MoveWafflePiGateway"
) {
    private val url = "http://192.168.137.28:1112/"
    //private val url = "http://192.168.178.35:1112/"
    //private val url = "http://host.docker.internal:1112/"
    private val instance = OkHttpClient().newBuilder().connectTimeout(2, TimeUnit.SECONDS).build()


    private var speedLimit: Double = 1.0
    private val dataMap: MutableMap<String, Any?> = mutableMapOf()

    private var stopped = false
    private val stopTime: Long = 8000


    override fun listen() {
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

    fun limitSpeed(limit: Double) {
        logInfo("Set Speed Limit to: $limit")
        speedLimit = limit
    }

    fun stop() {
        stopped = true
        moveWafflePi(CmdVel(Angular(x = 0.0, y = 0.0, z = 0.0), Linear(x = 0.0, y = 0.0, z = 0.0), null))
    }

    private fun moveWafflePi(cmdVel: CmdVel) {
        logInfo("Move WafflePi base..." + cmdVel.toString())
        val jsonObject = JSONObject()
        try {

            //Set speed Limit
            if (abs(cmdVel.angular!!.x) > speedLimit) {
                if (cmdVel.angular!!.x < 0) cmdVel.angular!!.x = speedLimit * -1
                else cmdVel.angular!!.x = speedLimit
            }
            if (cmdVel.angular!!.y > speedLimit) cmdVel.angular!!.y = speedLimit
            if (cmdVel.angular!!.z > speedLimit) cmdVel.angular!!.z = speedLimit
            if (abs(cmdVel.linear!!.x) > speedLimit) {
                if (cmdVel.linear!!.x < 0) cmdVel.linear!!.x = speedLimit * -1
                else cmdVel.linear!!.x = speedLimit
            }
            if (cmdVel.linear!!.y > speedLimit) cmdVel.linear!!.y = speedLimit
            if (cmdVel.linear!!.z > speedLimit) cmdVel.linear!!.z = speedLimit
            jsonObject.put("angularx", cmdVel.angular!!.x)
            jsonObject.put("angulary", cmdVel.angular!!.y)
            jsonObject.put("angularz", cmdVel.angular!!.z)
            jsonObject.put("linearx", cmdVel.linear!!.x)
            jsonObject.put("lineary", cmdVel.linear!!.y)
            jsonObject.put("linearz", cmdVel.linear!!.z)

            val mediaType = "application/json".toMediaType()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)
            val url = url + "cmdVel"
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
                    logInfo("Moving WafflePi Response:" + response.code)
                    response.body?.close()

                }
            })
        } catch (e: Exception) {
            logger.error { e.printStackTrace() }
        } finally {
            if (stopped) {
                Thread(Runnable {
                    logInfo("Suspend WafflePi movement for $stopTime s...")
                    Thread.sleep(stopTime)
                    stopped = false
                }).start()


            }
        }
    }

    override fun getDataFromSource() {
        //Do Nothing
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
        dataMap[property] = (value as ObjectWithTime).data
        if (property == "moveCmdVel") {
            try {
                if (stopped) return
                moveWafflePi(dataMap["moveCmdVel"] as CmdVel)
            } catch (e: Exception) {
                logger.error { e.printStackTrace() }
            }

        }
    }

    override fun getLastUpdate(property: String): String? {
        return null
    }

    override val lastPropertyUpdateMap: Map<String, Long>
        get() = TODO("Not yet implemented")
}