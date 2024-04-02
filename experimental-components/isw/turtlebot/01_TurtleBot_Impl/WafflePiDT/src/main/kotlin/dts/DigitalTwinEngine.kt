package dts

import dts.connection.Mapping
import dts.connection.SynchronizationDirection
import dts.connection.Synchronizer
import dts.events.*
import dts.events.observer.EngineWFObserver
import dts.events.observer.ServiceObserver
import dts.gateway.AbstractGateway
import dts.modelmanager.AbstractModelManager
import dts.services.AbstractServiceConnection
import dts.services.EngineServiceAPI
import mu.KotlinLogging
import java.time.LocalDateTime
import java.util.concurrent.locks.ReentrantLock

val logger = KotlinLogging.logger {}

/**
 * The DigitalTwinEngine is the main class of the DTS. It is responsible for configuring the DTS and synchronizing the
 * properties between the DT and the AbstractGateway.
 * @param serviceConnectionSet: Set of all services that are connected to the DTS
 * @param gatewaySet: Set of all gateways that are connected to the DTS
 * @param modelManagerSet: Set of all modelManagers that are connected to the DTS
 * @param mappingSet: Set of all mappings that are connected to the DTS
 * @param remoteServices: Set of all services that are connected to the DTS via the EngineServiceAPI
 * @param observer: List of all observers that are notified when an event within the DTE occurs
 * @param synchronizer: The synchronizer is responsible for synchronizing the properties between the DT and the AbstractGateway
 * @param serviceRequestLock: This Lock is used to make sure that the threads don't try to access the same lists at the same time
 * @param timedSyncLock: This Lock is used to make sure that the threads don't try to access the same lists at the same time
 * @param triggeredSyncLock: This Lock is used to make sure that the threads don't try to access the same lists at the same time
 * @param serviceEventList: This List stores all the events that are to be executed by the corresponding threads
 * @param timedSyncEventList: This List stores all the events that are to be executed by the corresponding threads
 * @param triggeredSyncEventList: This List stores all the events that are to be executed by the corresponding threads
 * @param serviceRequestThread: This thread is responsible for executing service requests
 * @param timedSyncsThread: This thread is responsible for executing timed syncs
 * @param triggeredSyncsThread: This thread is responsible for executing triggered syncs
 * @param engineServiceAPI: This is the API that is used to connect services to the DTS during runtime
 */
class DigitalTwinEngine(
    val serviceConnectionSet: MutableSet<AbstractServiceConnection> = mutableSetOf(),
    val gatewaySet: MutableSet<AbstractGateway> = mutableSetOf(),
    var modelManagerSet: MutableSet<AbstractModelManager<*>> = mutableSetOf(),
    mappingSet: MutableSet<Mapping> = mutableSetOf(),
    private var remoteServices: MutableSet<String> = mutableSetOf()
) {

    private val observer = mutableListOf<EngineWFObserver>()
    val synchronizer = Synchronizer()
    private val engineServiceAPI = EngineServiceAPI(ServiceObserver(this))
    private var triggeredSyncEventListBlocker = false
    private var triggeredSyncEventListBuffer = mutableListOf<DTEvent>()

    /**
     * These Locks are used to make sure that the threads don't try to access the same lists at the same time
     */
    private val serviceRequestLock = ReentrantLock()
    private val timedSyncLock = ReentrantLock()
    private val triggeredSyncLock = ReentrantLock()

    /**
     * These Lists store all the events that are to be executed by the corresponding threads
     */
    private val serviceEventList = mutableListOf<DTEvent>()
    private val timedSyncEventList = mutableListOf<DTEvent>()
    private val triggeredSyncEventList = mutableListOf<DTEvent>()

    //Store Errors in memory for easy retrieval
    private val errors = mutableListOf<ErrorEvent>()


    private val serviceRequestThread = Thread(Runnable {
        while (true) {
            Thread.sleep(10)
            if (serviceEventList.isNotEmpty() && serviceRequestLock.tryLock()) {
                executeServiceRequest()
            }
        }
    })

    private val timedSyncsThread = Thread(Runnable {
        while (true) {
            Thread.sleep(10)
            if (timedSyncEventList.isNotEmpty() && timedSyncLock.tryLock()) {
                executeTimedSync()
            }
        }
    })

    private val triggeredSyncsThread = Thread(Runnable {
        while (true) {
            Thread.sleep(10)
            if (triggeredSyncEventList.isNotEmpty() && triggeredSyncLock.tryLock()) {
                executeTriggeredSync()
            }
        }
    })

    init {
        timedSyncsThread.start()
        serviceRequestThread.start()
        triggeredSyncsThread.start()
        this.synchronizer.mappingSet = mappingSet
        engineServiceAPI.start()
        logger.info { "Engine started." }
    }

    /**
     * Configures all the components of the DTS
     */
    fun configure(
        modelManagerSet: Set<AbstractModelManager<*>> = emptySet(),
        gateways: Set<AbstractGateway> = emptySet(),
        mappingSet: Set<Mapping> = emptySet(),
        serviceConnection: Set<AbstractServiceConnection> = emptySet()
    ) {
        modelManagerSet.forEach { this.modelManagerSet.add(it) }
        this.gatewaySet.addAll(gateways)
        this.synchronizer.configure(mappingSet, this)
        this.serviceConnectionSet.addAll(serviceConnection)
        logger.info { "Engine configured with:" }
        logger.info { "Gateways: " + this.gatewaySet.size }
        logger.info { "Services: " + this.serviceConnectionSet.size }
        logger.info { "Modelmanagers: " + this.modelManagerSet.size }
    }

    /**
     * Adds a new data point event to the list of events that are to be executed
     */
    fun registerTriggeredSyncEvent(dataPointEvent: NewDataPointEvent) {
        logger.info { "Trigger sync event received" }
        if (triggeredSyncLock.tryLock()) {
            try {
                if (triggeredSyncEventListBlocker) {
                    triggeredSyncEventListBuffer.add(dataPointEvent)
                } else {
                    triggeredSyncEventList.add(dataPointEvent)
                    triggeredSyncEventList.addAll(triggeredSyncEventListBuffer)
                    triggeredSyncEventListBuffer.clear()
                }

            } finally {
                triggeredSyncLock.unlock()
            }
        }
    }

    /**
     * Adds a new data point event to the list of events that are to be executed
     */
    fun registerTimedSyncEvent(dataPointEvent: NewDataPointEvent) {
        logger.info { "Timed sync event received" }
        if (timedSyncLock.tryLock()) {
            try {
                timedSyncEventList.add(dataPointEvent)
            } finally {
                timedSyncLock.unlock()
            }
        }
    }

    /**
     * This method executes a service task like retrieving data from a model or gateway
     */
    fun registerServiceEvent(dtEvent: DTEvent) {
        if (dtEvent is NewDataPointEvent) {
            println("NewDataPointEvent")
        }
        if (dtEvent is ServiceRequestEvent && serviceRequestLock.tryLock()) {
            try {
                serviceEventList.add(dtEvent)
            } finally {
                serviceRequestLock.unlock()
            }
        }
    }

    fun newEngineEvent(observedProperty: String, newValue: Any?, syncDirection: SynchronizationDirection) {
        observer.forEach { listener ->
            val event = NewDataPointEvent(this)
            event.sourceID = "engine"
            event.timestamp = LocalDateTime.now()
            event.synchronizationDirection = syncDirection
            event.message = newValue.toString()
            listener.handleEngineEvent(event, observedProperty)
        }
    }

    /**
     * This method is called when an error occurs. It is supposed to either log the error and forward it to the UI.
     */
    fun handleError(error: ErrorEvent) {
        logger.error { "'${error.message}' from ${error.sourceID}" }
        errors.add(error)
    }

    fun handleModelUpdateError(error: ModelUpdateErrorEvent) {
        logger.error { "'${error.message}' from ${error.sourceID}" }
        errors.add(error)
        //Retry or forget
    }

    /**
     * This method checks which gateway is responsible for a given property
     * @param property: The property that is checked
     * @return the gateway that is responsible for the property
     * @throws Exception if no gateway is responsible for the property
     */
    fun getResponsibleGateway(property: String): AbstractGateway {
        var responsibleGateway: AbstractGateway? = null
        gatewaySet.forEach {
            if (it.responsibleForID(property)) {
                responsibleGateway = it
            }
        }
        if (responsibleGateway != null)
            return responsibleGateway!!
        else
            throw Exception("No gateway is responsible for the property $property.")
    }

    /**
     * This method checks which modelManager is responsible for a given property
     * @param property: The property that is checked
     * @return the modelManager that is responsible for the property
     * @throws Exception if no modelManager is responsible for the property
     */
    fun getResponsibleModelManager(property: String): AbstractModelManager<*> {
        var responsibleModelManager: AbstractModelManager<*>? = null
        modelManagerSet.forEach {
            if (it.responsibleForID(property)) {
                responsibleModelManager = it
            }
        }
        if (responsibleModelManager != null)
            return responsibleModelManager!!
        else
            throw Exception("No model is responsible for the property $property.")
    }

    /**
     * This method executes a triggered sync triggered from a gateway or model
     */
    private fun executeTriggeredSync() {
        triggeredSyncEventListBlocker = true
        try {
            for (event in triggeredSyncEventList) {
                if (event.synchronizationDirection == SynchronizationDirection.GATEWAY_TO_DT) {
                    val gateway = event.source as AbstractGateway
                    synchronizer.mappingSet.forEach { mapping ->
                        if (checkForPropertiesInMapping(mapping, gateway.propertyIDs)) {
                            logger.debug { "Triggered Sync triggered from:" + event.sourceID }
                            synchronizer.synchronize(mapping)
                        }
                    }
                } else if (event.synchronizationDirection == SynchronizationDirection.DT_TO_GATEWAY) {
                    val modelManager = event.source as AbstractModelManager<*>
                    synchronizer.mappingSet.forEach { mapping ->
                        if (checkForPropertiesInMapping(mapping, modelManager.propertyIDs)) {
                            logger.debug { "Triggered Sync triggered from:" + event.sourceID }
                            synchronizer.synchronize(mapping)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logger.error { e.printStackTrace() }
        } finally {
            triggeredSyncEventList.clear()
            triggeredSyncLock.unlock()
            triggeredSyncEventListBlocker = false
        }
    }

    /**
     * This method executes a timed sync triggered from a mapping
     */
    private fun executeTimedSync() {
        try {
            timedSyncEventList.forEach { event ->
                var mapping: Mapping? = null
                synchronizer.mappingSet.forEach { if (it.connectionID == event.sourceID) mapping = it }
                if (mapping != null && mapping!!.live) {
                    logger.debug { "Timed Sync triggered from:" + event.sourceID }
                    synchronizer.synchronize(mapping!!)
                }
            }
        } finally {
            timedSyncEventList.clear()
            timedSyncLock.unlock()
        }
    }

    /**
     * This method executes a service request like retrieving data from a model or gateway
     */
    private fun executeServiceRequest() {
        try {
            serviceEventList.forEach { event ->
                serviceConnectionSet.forEach { service ->
                    if (service == event.source) {
                        logger.debug { "Execute Service Request from:" + event.sourceID }
                        service.executeTask(event.message, this)
                    }
                }
            }
        } finally {
            serviceEventList.clear()
            serviceRequestLock.unlock()
        }
    }

    /**
     * This method checks if the propertyTranslationMAp of a given mapping contains one or more elements of the propertySet
     * @param mapping: The mapping that is checked
     * @param propertySet: The set of properties that is checked
     * @return true if the mapping contains one or more elements of the propertySet
     */
    private fun checkForPropertiesInMapping(mapping: Mapping, propertySet: Set<String>): Boolean {
        var found = false
        propertySet.forEach { property ->
            if (mapping.getGatewayPropertyIDs().contains(property)) {
                found = true
            }
        }
        return found
    }

    fun addObserver(dteObserver: EngineWFObserver) {
        observer.add(dteObserver)
        logger.info { "Observer $dteObserver added to $this" }
    }

    fun removeObserver(dteObserver: EngineWFObserver) {
        observer.remove(dteObserver)
        logger.info { "Observer $dteObserver removed from $this" }
    }

    fun serviceJoin(dtEvent: DTEvent) {
        this.addRemoteService(dtEvent.message)
    }

    fun addGateway(gatewayListener: AbstractGateway) {
        this.gatewaySet.add(gatewayListener)
        logger.info { "Gateway added: " + gatewayListener.gatewayID }
    }

    fun removeGateway(gatewayListener: AbstractGateway) {
        this.gatewaySet.remove(gatewayListener)
        logger.info { "Gateway removed: " + gatewayListener.gatewayID }
    }

    fun addModelManager(modelManagerListener: AbstractModelManager<*>) {
        this.modelManagerSet.add(modelManagerListener)
        logger.info { "Modelmanager added: " + modelManagerListener.modelManagerID }
    }

    fun removeModelManager(modelManagerListener: AbstractModelManager<*>) {
        this.modelManagerSet.remove(modelManagerListener)
        logger.info { "Modelmanager removed: " + modelManagerListener.modelManagerID }
    }

    fun addMapping(mappingListener: Mapping) {
        this.synchronizer.addMapping(mappingListener)
        logger.info { "Mapping added: " + mappingListener.connectionID }
    }

    fun removeMapping(mappingListener: Mapping) {
        this.synchronizer.removeMapping(mappingListener)
        logger.info { "Mapping removed: " + mappingListener.connectionID }
    }

    fun addService(serviceListener: AbstractServiceConnection) {
        this.serviceConnectionSet.add(serviceListener)
        logger.info { "Service added: " + serviceListener.serviceID }
    }

    fun addRemoteService(serviceId: String) {
        this.remoteServices.add(serviceId)
        logger.info { "Remote service added: $serviceId" }
    }

    fun removeService(serviceListener: AbstractServiceConnection) {
        this.serviceConnectionSet.remove(serviceListener)
        logger.info { "Service removed: " + serviceListener.serviceID }
    }

    fun removeRemoteService(serviceId: String) {
        this.remoteServices.remove(serviceId)
        logger.info { "Remote service removed: $serviceId" }
    }
}