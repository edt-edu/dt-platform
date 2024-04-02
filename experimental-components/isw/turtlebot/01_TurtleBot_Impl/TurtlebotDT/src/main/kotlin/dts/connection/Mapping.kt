package dts.connection

import dts.events.NewDataPointEvent
import dts.events.observer.IMappingObserver
import mu.KotlinLogging
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

/**
 *This class is the representation of the connection between the AbstractGateway and the DT, their properties and when they should be updated.
 * @param synchronizeInterval the interval at which the connection should be synchronized
 * @param propertyIDTranslationMap the map of the propertyIDs of the DT and the gateway. The keys are the propertyIDs of the DT and the values are the propertyIDs of the gateway
 * @param synchronisationDirection the direction in which the connection should be synchronized. This can be either GATEWAY_TO_DT, DT_TO_GATEWAY or BOTH
 * @param connectionID the ID of the connection. used for searching the connection in the engine and for logging
 */
class Mapping(
    private val synchronizeInterval: Long,
    private val propertyIDTranslationMap: Map<String, String>,
    val synchronisationDirection: SynchronizationDirection,
    val connectionID: String,
    var live: Boolean = true,
    private val listeners: MutableList<IMappingObserver> = mutableListOf()
) {

    /**
     * This thread is responsible for creating timed sync events at the given interval
     */
    private val thread = Thread(Runnable {
        while (true) {
            createNewSyncEvent()
            Thread.sleep(synchronizeInterval)
        }

    })

    init {
        thread.start()
        logger.info { "Mapping: " + this.connectionID + " started" }
    }

    /**
     * This function creates a new sync event and notifies the listeners
     */
    private fun createNewSyncEvent() {
        logger.debug { this.connectionID + ": create new sync event" }
        for (wfListener: IMappingObserver in listeners) {
            val event = NewDataPointEvent(this)
            event.sourceID = connectionID
            event.timestamp = LocalDateTime.now()
            event.synchronizationDirection = synchronisationDirection
            wfListener.handleTimedEvent(event)
        }
    }

    /**
     * This function returns the property names of the both sides of the connection.
     */
    fun getPropertyIDs(): Map<String, String> {
        return propertyIDTranslationMap
    }

    /**
     * This function returns the property names of the gateway side of the connection.
     */
    fun getGatewayPropertyIDs(): Set<String> {
        return propertyIDTranslationMap.values.toSet()
    }

    /**
     * This function returns the property names of the DT side of the connection.
     */
    fun getDTPropertyIDs(): Set<String> {
        return propertyIDTranslationMap.keys.toSet()
    }

    fun addObserver(mappingObserver: IMappingObserver) {
        listeners.add(mappingObserver)
        logger.info { "Observer $mappingObserver added to $this" }
    }

    fun removeObserver(mappingObserver: IMappingObserver) {
        listeners.remove(mappingObserver)
        logger.info { "Observer $mappingObserver removed from $this" }
    }
}
