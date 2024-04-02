import dts.DigitalTwinEngine
import dts.connection.Mapping
import dts.connection.SynchronizationDirection
import dts.events.observer.GatewayObserver
import dts.events.observer.MappingObserver
import dts.events.observer.ModelObserver

class MainKt {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val engine = DigitalTwinEngine()

            //create the gateway
            val armGateway = GetArmDataGateway()
            armGateway.addObserver(GatewayObserver(engine))

            val wafflePiDTGateway = WafflePiDTGateway()
            wafflePiDTGateway.addObserver(GatewayObserver(engine))

            val moveArmGateway = MoveArmGateway()
            moveArmGateway.addObserver(GatewayObserver(engine))

            val turtlebotDTGateway = TurtlebotDTGateway()
            turtlebotDTGateway.addObserver(GatewayObserver(engine))


            val postgresModelManager = PostgresModelManager()
            postgresModelManager.addObserver(ModelObserver(engine))

            val postgresMapping = Mapping(
                3000,
                mapOf(
                    "dynamicJointState" to "dynamicJointState",
                    "armControllerState" to "armControllerState",
                    "armJointTrajectory" to "armJointTrajectory",
                    "armState" to "armState",
                    "armTES" to "armTES",
                    "gripperTES" to "gripperTES",
                    "jointStates" to "jointStates",
                    "servoCollisionVelScale" to "servoCollisionVelScale",
                    "servoDeltaJoint" to "servoDeltaJoint",
                    "servoDeltaTwist" to "servoDeltaTwist",
                    "servoStatus" to "servoStatus",
                    "parameterEvent" to "parameterEvent",
                    "tf" to "tf",
                    "tfStatic" to "tfStatic",
                    "armDeltaJoint" to "armDeltaJoint",
                    "armDeltaTwist" to "armDeltaTwist"
                ),
                SynchronizationDirection.GATEWAY_TO_DT,
                "postgresMapping"
            )

            val moveMapping = Mapping(
                3000000,
                mapOf(
                    "moveArmDeltaJoint" to "moveArmDeltaJoint",
                    "moveArmDeltaTwist" to "moveArmDeltaTwist"
                ),
                SynchronizationDirection.DT_TO_GATEWAY,
                "moveMapping"
            )

            val stateMapping = Mapping(
                100,
                mapOf(
                    "joint_states" to "joint_states"
                ),
                SynchronizationDirection.DT_TO_GATEWAY,
                "stateMapping"
            )


            postgresMapping.addObserver(MappingObserver(engine))
            moveMapping.addObserver(MappingObserver(engine))
            stateMapping.addObserver(MappingObserver(engine))

            //configure the engine
            engine.configure(
                setOf(postgresModelManager),
                setOf(armGateway, wafflePiDTGateway, turtlebotDTGateway, moveArmGateway),
                setOf(postgresMapping, moveMapping,stateMapping))
        }
    }
}