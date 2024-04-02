import dts.DigitalTwinEngine
import dts.connection.Mapping
import dts.connection.SynchronizationDirection
import dts.events.observer.GatewayObserver
import dts.events.observer.MappingObserver
import dts.events.observer.ModelObserver
import dts.events.observer.ServiceObserver

class MainKt {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val engine = DigitalTwinEngine()

            //create the gateway
            val wafflePiDTGateway = WafflePiDTGateway()
            wafflePiDTGateway.addObserver(GatewayObserver(engine))

            val armDTGateway = ArmDTGateway()
            armDTGateway.addObserver(GatewayObserver(engine))


            val postgresModelManager = PostgresModelManager()
            postgresModelManager.addObserver(ModelObserver(engine))

            val frontEndService = FrontEndService()
            frontEndService.addObserver(ServiceObserver(engine))

            val postgresMapping = Mapping(
                300000,
                mapOf(
                    "battery_state" to "batteryState", "wafflePiMovement" to "wafflePiMovement", "errors" to "errors",
                    "wafflePiState" to "wafflePiState", "armState" to "armState"
                ),
                SynchronizationDirection.GATEWAY_TO_DT,
                "postgresMapping"
            )

            val controlMapping = Mapping(
                300000,
                mapOf(
                    "controlWafflePi" to "controlWafflePi",
                    "controlArm" to "controlArm",
                    "limitDistance" to "limitDistance"
                ),
                SynchronizationDirection.DT_TO_GATEWAY,
                "controlMapping"
            )


            postgresMapping.addObserver(MappingObserver(engine))
            controlMapping.addObserver(MappingObserver(engine))

            //configure the engine
            engine.configure(
                setOf(postgresModelManager),
                setOf(wafflePiDTGateway, armDTGateway),
                setOf(postgresMapping, controlMapping),
                setOf(frontEndService)
            )


        }
    }
}