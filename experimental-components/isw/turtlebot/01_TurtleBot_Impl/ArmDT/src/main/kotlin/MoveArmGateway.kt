import classes.ObjectWithTime
import classes.ServoDeltaJoint
import classes.ServoDeltaTwist
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

class MoveArmGateway : AbstractGateway(
    propertyIDs = setOf(
        "moveArmDeltaJoint", "moveArmDeltaTwist"
    ), gatewayID = "MoveArmGateway"
) {
    private val url = "http://192.168.137.28:1112/"
    //private val url = "http://192.168.178.35:1112/"
    //private val url = "http://host.docker.internal:1112/"
    private val instance = OkHttpClient().newBuilder().connectTimeout(2, TimeUnit.SECONDS).build()

    private val dataMap: MutableMap<String, Any?> = mutableMapOf()


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

    private fun moveArmDeltaJoint(jointJog: ServoDeltaJoint) {
        logInfo("Move Arm..." + jointJog.toString())
        val jsonObject = JSONObject()
        try {

            jsonObject.put("joint", jointJog.joint_names[0])
            jsonObject.put("velocity", jointJog.velocities[0])

            val mediaType = "application/json".toMediaType()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)
            val url = url + "servoDeltaJoint"
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
                    logInfo("Moving Arm Response:" + response.code)
                    response.body?.close()

                }
            })
        } catch (e: Exception) {
            logger.error { e.printStackTrace() }
        }
    }

    private fun moveArmDeltaTwist(deltaTwist: ServoDeltaTwist) {
        logInfo("Move Arm..." + deltaTwist.toString())
        val jsonObject = JSONObject()
        try {

            jsonObject.put("angularx", deltaTwist.twist.angular.x)
            jsonObject.put("angulary", deltaTwist.twist.angular.y)
            jsonObject.put("angularz", deltaTwist.twist.angular.z)
            jsonObject.put("linearx", deltaTwist.twist.linear.x)
            jsonObject.put("lineary", deltaTwist.twist.linear.y)
            jsonObject.put("linearz", deltaTwist.twist.linear.z)

            val mediaType = "application/json".toMediaType()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)
            val url = url + "servoDeltaTwist"
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
                    logInfo("Moving Arm Response:" + response.code)
                    response.body?.close()

                }
            })
        } catch (e: Exception) {
            logger.error { e.printStackTrace() }
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
        dataMap[property] = value
        if (property == "moveArmDeltaJoint") {
            try {
                moveArmDeltaJoint(dataMap["moveArmDeltaJoint"] as ServoDeltaJoint)
            } catch (e: Exception) {
                logger.error { e.printStackTrace() }
            }
        }
        if (property == "moveArmDeltaTwist") {
            try {
                moveArmDeltaTwist(dataMap["moveArmDeltaTwist"] as ServoDeltaTwist)
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