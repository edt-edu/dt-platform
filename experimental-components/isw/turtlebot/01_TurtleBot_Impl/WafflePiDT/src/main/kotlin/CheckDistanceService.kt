import classes.BatteryState
import classes.ObjectWithTime
import dts.DigitalTwinEngine
import dts.connection.SynchronizationDirection
import dts.events.ServiceRequestEvent
import dts.services.AbstractServiceConnection
import dts.services.IRestService
import mu.KotlinLogging
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

class CheckDistanceService : AbstractServiceConnection(true, "CheckDistanceService"),
    IRestService {


        private var stopped = false


    private val thread = Thread(Runnable {
        while (true) {
            Thread.sleep(500)
            val task = getTaskToExecute()
            createNewServiceEvent(task)
        }

    })

    override fun listen() {
        thread.start()
        logger.info { "$serviceID started" }
    }

    init {
        listen()
    }

    override fun executeTask(taskToExecute: String, digitalTwinEngine: DigitalTwinEngine) {
        logger.info { "Executing task $taskToExecute" }
        val modelManager =
            digitalTwinEngine.modelManagerSet.find { abstractModelManager -> abstractModelManager.responsibleForID("dynamicJointState") }

        val distance = modelManager?.getValue("distance") as Double
        val distanceLimit = modelManager.getValue("distanceLimit") as Double

        logger.info { "Distance is currently: ${distance}" }
        if (distance < distanceLimit) {
            logger.error { "TURTLEBOT IS TOO CLOSE TO OBSTACLE!" }
            val gateway =
                digitalTwinEngine.gatewaySet.find { abstractGateway -> abstractGateway.gatewayID == "MoveWafflePiGateway" }
            if (gateway is MoveWafflePiGateway && !stopped) {
                gateway.stop()
                stopped = true
            }

        } else {
            stopped = false
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

    private fun getTaskToExecute(): String {
        return "checkDistance"
    }

}