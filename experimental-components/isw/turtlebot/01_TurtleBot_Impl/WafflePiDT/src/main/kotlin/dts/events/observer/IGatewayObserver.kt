package dts.events.observer

import dts.events.DTEvent
import dts.events.ErrorEvent
import java.util.*

interface IGatewayObserver : EventListener {
    fun handleDatapointEvent(dtEvent: DTEvent)

    fun handleError(error: ErrorEvent)
}