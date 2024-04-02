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
            val wafflePiGateway = GetWafflePiDataGateway()
            wafflePiGateway.addObserver(GatewayObserver(engine))

            val moveWafflePiGateway = MoveWafflePiGateway()
            moveWafflePiGateway.addObserver(GatewayObserver(engine))

            val turtlebotGateway = TurtlebotDTGateway()
            turtlebotGateway.addObserver(GatewayObserver(engine))

            val armDTGateway = ArmDTGateway()
            armDTGateway.addObserver(GatewayObserver(engine))

            val postgresModelManager = PostgresModelManager()
            postgresModelManager.addObserver(ModelObserver(engine))


            val batteryService = CheckBatteryService()
            batteryService.addObserver(ServiceObserver(engine))

            val checkDistanceService = CheckDistanceService()
            checkDistanceService.addObserver(ServiceObserver(engine))

            val postgresMapping = Mapping(
                3000000,
                mapOf(
                    "dynamicJointState" to "dynamicJointState",
                    "cmdVel" to "cmdVel",
                    "diffDriveTES" to "diffDriveTES",
                    "imuBroadcaster" to "imuBroadcaster",
                    "imuBroadcasterTES" to "imuBroadcasterTES",
                    "jointStateBroadcasterTES" to "jointStateBroadcasterTES",
                    "jointStates" to "jointStates",
                    "odom" to "odom",
                    "parameterEvent" to "parameterEvent",
                    "tf" to "tf",
                    "tfStatic" to "tfStatic",
                    "rosout" to "rosout",
                    "scan" to "scan",
                    "turtlebot_movement" to "turtlebot_movement",
                    "distanceLimit" to "distanceLimit"
                ),
                SynchronizationDirection.GATEWAY_TO_DT,
                "postgresMapping"
            )

            val moveMapping = Mapping(
                3000000,
                mapOf(
                    "moveCmdVel" to "moveCmdVel"
                ),
                SynchronizationDirection.DT_TO_GATEWAY,
                "moveMapping"
            )

            val stateMapping = Mapping(
                100,
                mapOf(
                    "cmdVel" to "cmd_vel",
                    "batteryState" to "batteryState",
                    "imuState" to "imuState",
                    "rosState" to "rosState",
                    "wheelState" to "wheelState",
                    "distance" to "distance"
                ),
                SynchronizationDirection.DT_TO_GATEWAY,
                "stateMapping"
            )

            postgresMapping.addObserver(MappingObserver(engine))
            stateMapping.addObserver(MappingObserver(engine))
            moveMapping.addObserver(MappingObserver(engine))

            //configure the engine
            engine.configure(
                setOf(postgresModelManager),
                setOf(wafflePiGateway, moveWafflePiGateway, turtlebotGateway, armDTGateway),
                setOf(postgresMapping, stateMapping, moveMapping),
                setOf(batteryService, checkDistanceService)
            )

        }
    }
}