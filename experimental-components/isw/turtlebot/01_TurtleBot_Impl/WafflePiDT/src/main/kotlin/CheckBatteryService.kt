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

class CheckBatteryService : AbstractServiceConnection(true, "CheckBatteryService"),
    IRestService {

    private val thread = Thread(Runnable {
        while (true) {
            Thread.sleep(6000)
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

        if (modelManager?.getValue("battery_state") == null) {
            return
        }
        val batteryState = (modelManager.getValue("batteryState") as ObjectWithTime).data as BatteryState
        logger.info { "Battery is currently at: ${batteryState.percentage}%" }
        if (batteryState.percentage < 40) {
            val gateway =
                digitalTwinEngine.gatewaySet.find { abstractGateway -> abstractGateway.gatewayID == "MoveWafflePiGateway" }
            if (gateway is MoveWafflePiGateway) {
                gateway.limitSpeed(0.1)
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

    private fun getTaskToExecute(): String {
        return "checkBattery"
    }

}