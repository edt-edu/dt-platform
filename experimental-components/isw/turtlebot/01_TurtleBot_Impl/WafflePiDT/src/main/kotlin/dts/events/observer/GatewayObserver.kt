package dts.events.observer

import dts.DigitalTwinEngine
import dts.events.DTEvent
import dts.events.ErrorEvent
import dts.events.NewDataPointEvent

class GatewayObserver(private val dtEngine: DigitalTwinEngine) : IGatewayObserver {

    /**
     * receive a new datapoint from CPS -> trigger a synchronization event
     * @param dtEvent the event that occurred, most likely a new datapoint
     */
    override fun handleDatapointEvent(dtEvent: DTEvent) {
        if (dtEvent is NewDataPointEvent)
            dtEngine.registerTriggeredSyncEvent(dtEvent)

    }

    override fun handleError(error: ErrorEvent) {
        dtEngine.handleError(error)
    }
}