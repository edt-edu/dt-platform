package dts.connection

import dts.DigitalTwinEngine
import dts.IDataSource
import dts.gateway.AbstractGateway
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * This class is responsible for synchronizing the properties between the ModelManagers and the Gateways
 *
 */
class Synchronizer {
    var mappingSet = mutableSetOf<Mapping>()
    private lateinit var dtEngineInstance: DigitalTwinEngine

    /**
     * Configures the Synchronizer with the mappings and the DigitalTwinEngine
     * @param mappingSet: contains every mappings of the DTS
     * @param engine: The instance of the DigitalTwinEngine
     */
    fun configure(mappingSet: Set<Mapping> = emptySet(), engine: DigitalTwinEngine){
        this.mappingSet.addAll(mappingSet)
        this.dtEngineInstance = engine
    }

    fun addMapping(mapping: Mapping) {
        this.mappingSet.add(mapping)
    }

    fun removeMapping(mapping: Mapping) {
        this.mappingSet.remove(mapping)
    }

    /**
     * Synchronizes properties between the DT and the AbstractGateway
     * depending on the synchronisationDirection of the mapping
     * @param mapping: The mapping with the properties which are supposed to be updated
     */
    fun synchronize(mapping: Mapping) {
        if (!mapping.live) return
        logger.info{ "Synchronize with Direction: " + mapping.synchronisationDirection}
        when (mapping.synchronisationDirection){
            SynchronizationDirection.GATEWAY_TO_DT -> {
                updateDT(mapping)
            }
            SynchronizationDirection.DT_TO_GATEWAY -> {
                updateGateway(mapping)
            }
            SynchronizationDirection.BOTH -> {
                updateSystem(mapping)
            }
        }
    }

    /**
     * Updates the DT with the values from the AbstractGateway.
     * After getting the properties which are supposed to have an update it gets the corresponding values from
     * the AbstractGateway and then writes the date to the models
     * @param mapping: The mapping with the properties which are supposed to be updated
     */
    private fun updateDT(mapping: Mapping) {
        try {
            val propertiesToUpdate = mapping.getGatewayPropertyIDs()
            val updateValuesFromGateway: Map<String, Any?> = extractValuesFromDatasource(propertiesToUpdate, dtEngineInstance.gatewaySet)
            mapping.getPropertyIDs().forEach { (dtProperty, gatewayProperty) ->
                val newValue = updateValuesFromGateway[gatewayProperty]
                updateDtProperty(dtProperty, newValue)
            }
        } catch(e: Exception) {
            logger.error { e.printStackTrace() }
        }

    }

    /**
     * Updates the AbstractGateway with the values from the DT.
     * After getting the properties which are supposed to have an update it gets the corresponding values from
     * the modelManagers
     * @param mapping: The mapping with the properties which are supposed to be updated
     */
    private fun updateGateway(mapping: Mapping) {
        try {
            val propertiesToUpdate = mapping.getDTPropertyIDs()
            val updateValuesFromDT = extractValuesFromDatasource(propertiesToUpdate, dtEngineInstance.modelManagerSet)
            if (updateValuesFromDT.isEmpty()) return
            mapping.getPropertyIDs().forEach { (dtProperty, gatewayProperty) ->
                val newValue = updateValuesFromDT[dtProperty]!!
                updateGatewayProperty(gatewayProperty, newValue)
            }
        } catch(e: Exception) {
            dts.logger.error { e.message }
        }
    }

    /**
     * Updates either the DT or the AbstractGateway, depending on which value is newer
     *@param mapping: The mapping with the properties which are supposed to be updated
     */
    private fun updateSystem(mapping: Mapping) {
        val propertyTranslationMap = mapping.getPropertyIDs()
        propertyTranslationMap.forEach { (dtProperty, cpsProperty) ->
            val gateway = dtEngineInstance.getResponsibleGateway(cpsProperty)
            val lastGatewayUpdate = gateway.getLastUpdate(cpsProperty)
            val modelManager = dtEngineInstance.getResponsibleModelManager(dtProperty)
            val lastDTUpdate = modelManager.getLastUpdate(dtProperty)
            if (lastDTUpdate != null || lastGatewayUpdate != null) {
                if (lastGatewayUpdate == null) {
                    val newValue = modelManager.getValue(dtProperty)
                    if (newValue != null) updateGatewayProperty(cpsProperty, newValue)
                } else if (lastDTUpdate == null || lastDTUpdate >= lastGatewayUpdate) {
                    val newValue = modelManager.getValue(dtProperty)
                    if (newValue != null) updateGatewayProperty(cpsProperty, newValue)
                } else {
                    val newValue = gateway.getValue(cpsProperty)
                    if (newValue != null) updateDtProperty(dtProperty, newValue)
                }
            }
        }
    }

    /**
     * Updates the property of a ModelManager with the new value, without explicitly knowing the required ModelManager
     * @param dtProperty: The property which is supposed to be updated
     * @param newValue: The new value of the property
     * @throws Exception if no ModelManager is responsible for the property
     */
    private  fun updateDtProperty(dtProperty: String, newValue: Any?) {
        try {
            dtEngineInstance.getResponsibleModelManager(dtProperty).setValue(dtProperty, newValue)
            dtEngineInstance.newEngineEvent(dtProperty, newValue, SynchronizationDirection.GATEWAY_TO_DT)
        } catch (e: Exception) {
            logger.error { e.printStackTrace() }
        }

    }

    /**
     * Updates the property of a Gateway with the new value, without explicitly knowing the required Gateway
     * @param gatewayProperty: The property which is supposed to be updated
     * @param newValue: The new value of the property
     * @throws Exception if no gateway is responsible for the property
     */
    private fun updateGatewayProperty(gatewayProperty: String, newValue: Any) {
        try {
            dtEngineInstance.getResponsibleGateway(gatewayProperty).setValue(gatewayProperty, newValue)
            dtEngineInstance.newEngineEvent(gatewayProperty, newValue, SynchronizationDirection.DT_TO_GATEWAY)
        } catch (e: Exception) {
            dts.logger.error { e.message }
        }
    }

    /**
     * Extracts values from either all ModelManagers or all Gateways available
     * This method allows to extract values from multiple data sources at once
     * @param properties: The properties which are supposed to be extracted
     * @param dataSources: The data sources from which the values are supposed to be extracted
     * @return A map containing the properties as keys and the corresponding values as values
     */
    private fun extractValuesFromDatasource(properties: Set<String>, dataSources: Set<IDataSource>): Map<String, Any?> {
        val returnMap = mutableMapOf<String, Any?>()
        properties.forEach { property ->
            var returnValue: Any? = "No Value"
            dataSources.forEach {
                if (it.responsibleForID(property)) {
                    returnValue = it.getValue(property)
                }
            }
            if (returnValue == "No Value")
                throw Exception("No datasource is responsible for the property $property.")
            returnMap[property] = returnValue
        }
        return returnMap
    }
}