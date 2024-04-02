package dts.modelmanager

import dts.IDataSource
import dts.connection.SynchronizationDirection
import dts.events.ErrorEvent
import dts.events.observer.IModelObserver
import dts.events.observer.ServiceObserver
import mu.KotlinLogging
import java.time.LocalDateTime

/**
 * Abstract class for the modelManagers, which are responsible for the holing all information about the models and how to interact with them
 * @param
 *
 *
 */
abstract class AbstractModelManager<T>(
    override val propertyIDs: Set<String>,
    val modelManagerID: String,
    val modelPropertiesWithReplacement: Set<String> = emptySet(),
    val observer: MutableList<IModelObserver> = mutableListOf()
) : IDataSource {

    val logger = KotlinLogging.logger {}

    /**
     * This method should be used to make modifications to the model
     */
    protected abstract fun writeToModel(property: String, value: T)

    /**
     * This method retrieves desired information from the model
     */
    protected abstract fun readFromModel(property: String, options: Any?) : Any

    /**
     * This model returns the values of the model at the given time stamp
     */
    abstract fun getValueAtIndex(index : String, property: String) : Any

    /**
     * This method returns the model, either being a way to connect to the model itself or the entirety of the values the model holds
     */
    abstract fun getModel() : Any

    /**
     * This adds a model listener to the ModelManager
     */
    fun addObserver(modelObserver: IModelObserver) {
        observer.add(modelObserver)
        logger.info { "Observer $modelObserver added to $this"  }
    }

    /**
     * Same as setValue but also start synchronization. Should be used by services to manually write set values in model
     */
    abstract fun manualSetValue(id: String, value: Any, serviceName: String)

    /**
     * This removes a model listener form the ModelManager
     */
    fun removeObserver(modelObserver: IModelObserver) {
        observer.remove(modelObserver)
        logger.info { "Observer $modelObserver removed from $this"  }
    }

    fun sendError(error: ErrorEvent, syncDirection: SynchronizationDirection) {
        for (modelListener in observer) {
            val event = ErrorEvent(this)
            event.sourceID = error.sourceID
            event.timestamp = LocalDateTime.now()
            event.message = error.message
            event.synchronizationDirection = syncDirection
            modelListener.handleError(event)
        }
    }

}