# Copyright 2016 Open Source Robotics Foundation, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import rclpy
from rclpy.node import Node
from rclpy.qos import qos_profile_sensor_data
from shared_memory_dict import SharedMemoryDict

from flask import Flask, jsonify, request
from flask_cors import CORS
import threading
from rosbags.serde import deserialize_cdr
from rosidl_runtime_py import convert

from std_msgs.msg import String, Float64, Int8
from control_msgs.msg import DynamicJointState, JointTrajectoryControllerState, JointJog
from moveit_msgs.msg import PlanningScene
from geometry_msgs.msg import TwistStamped, Twist
from lifecycle_msgs.msg import TransitionEvent
from nav_msgs.msg import Odometry
from tf2_msgs.msg import TFMessage
from sensor_msgs.msg import Imu, LaserScan, JointState
from rcl_interfaces.msg import Log, ParameterEvent
from trajectory_msgs.msg import JointTrajectory
import json
import yaml

data = {}

def msg2Json(msg):
    y = yaml.load(str(msg).encode("ascii", "ignore"))
    return json.dumps(y, indent=4)


class MinimalSubscriber(Node):

    def __init__(self):
        super().__init__('minimal_subscriber')
        self.DynamicJointStateSub = self.create_subscription(
            DynamicJointState,
            'dynamic_joint_states',
            self.dynamicJointState_callback,
            10)
        self.ServoDeltaTwistSub = self.create_subscription(
            TwistStamped,
            'servo_node/delta_twist_cmds',
            self.servoDeltaTwist_callback,
            10
        )
        self.DiffDriveTESub = self.create_subscription(
            TransitionEvent,
            'diff_drive_controller/transition_event',
            self.diffDriveTES_callback,
            10
        )
        self.GripperTESub = self.create_subscription(
            TransitionEvent,
            'gripper_controller/transition_event',
            self.gripperTES_callback,
            10
        )
        self.OdomSub = self.create_subscription(
            Odometry,
            'odom',
            self.odom_callback,
            10
        )
        self.TFStaticSub = self.create_subscription(
            TFMessage,
            'tf_static',
            self.tfStatic_callback,
            10
        )
        self.ArmControllerStateSub = self.create_subscription(
            JointTrajectoryControllerState,
            'arm_controller/controller_state',
            self.armControllerState_callback,
            10
        )
        self.RobotDescriptionSub = self.create_subscription(
            String,
            'robot_description',
            self.robotDescription_callback,
            10
        )
        self.ImuBroadcasterSub = self.create_subscription(
            Imu,
            'imu_broadcaster/imu',
            self.imuBroadcaster_callback,
            10
        )
        self.TFSub = self.create_subscription(
            TFMessage,
            'tf',
            self.tf_callback,
            10
        )
        self.RosoutSub = self.create_subscription(
            Log,
            'rosout',
            self.rosout_callback,
            10
        )
        self.ImuBroadcasterTESub = self.create_subscription(
            TransitionEvent,
            'imu_broadcaster/transition_event',
            self.imuBroadcasterTES_callback,
            10
        )
        self.CmdVelSub = self.create_subscription(
            Twist,
            'cmd_vel',
            self.cmdVel_callback,
            10
        )
        self.ParameterEventSub = self.create_subscription(
            ParameterEvent,
            'parameter_events',
            self.parameterEvent_callback,
            10
        )
        self.ServoCollisionVelScaleSub = self.create_subscription(
            Float64,
            'servo_node/collision_velocity_scale',
            self.servoCollisionVelScale_callback,
            10
        )
        self.JointStateBroadcasterTESub = self.create_subscription(
            TransitionEvent,
            'joint_state_broadcaster/transition_event',
            self.jointStateBroadcasterTES_callback,
            10
        )
        self.ServoPubPlanningSceneSub = self.create_subscription(
            PlanningScene,
            'servo_node/publish_planning_scene',
            self.servoPubPlanningScene_callback,
            10
        )
        self.ServoStatusSub = self.create_subscription(
            Int8,
            'servo_node/status',
            self.servoStatus_callback,
            10
        )
        self.ScanSub = self.create_subscription(
            LaserScan,
            'scan',
            self.scan_callback,
            qos_profile_sensor_data
        )
        self.JointStatesSub = self.create_subscription(
            JointState,
            'joint_states',
            self.jointStates_callback,
            10
        )
        self.ArmJointTrajectorySub = self.create_subscription(
            JointTrajectory,
            'arm_controller/joint_trajectory',
            self.armJointTrajectory_callback,
            10
        )
        self.ArmTESub = self.create_subscription(
            TransitionEvent,
            'arm_controller/transition_event',
            self.armTES_callback,
            10
        )
        self.ServoDeltaJointSub = self.create_subscription(
            JointJog,
            'servo_node/delta_joint_cmds',
            self.servoDeltaJoint_callback,
            10
        )
        self.ArmStateSub = self.create_subscription(
            JointTrajectoryControllerState,
            'arm_controller/state',
            self.armState_callback,
            10
        )
        self.DynamicJointStateSub  # prevent unused variable warning
        self.ArmControllerStateSub
        self.ArmJointTrajectorySub
        self.ArmStateSub
        self.ArmTESub
        self.CmdVelSub
        self.DiffDriveTESub
        self.GripperTESub
        self.ImuBroadcasterSub
        self.ImuBroadcasterTESub
        self.JointStateBroadcasterTESub
        self.JointStatesSub
        self.OdomSub
        self.ParameterEventSub
        self.ServoCollisionVelScaleSub
        self.ServoDeltaJointSub
        self.ServoDeltaTwistSub
        self.ServoPubPlanningSceneSub
        self.ServoStatusSub
        self.TFStaticSub
        self.TFSub
        self.RobotDescriptionSub
        self.RosoutSub
        self.ScanSub
    
    def dynamicJointState_callback(self, msg):
        data["dynamicJointState"] = msg

    def armControllerState_callback(self, msg):
        data["armControllerState"] = msg

    def armJointTrajectory_callback(self, msg):
        data["armJointTrajectory"] = msg

    def armState_callback(self, msg):
        data["armState"] = msg
    
    def armTES_callback(self, msg):
        data["armTES"] = msg

    def cmdVel_callback(self, msg):
        data["cmdVel"] = msg

    def diffDriveTES_callback(self, msg):
        data["diffDriveTES"] = msg
    
    def gripperTES_callback(self, msg):
        data["gripperTES"] = msg
    
    def imuBroadcaster_callback(self, msg):
        data["imuBroadcaster"] = msg

    def imuBroadcasterTES_callback(self, msg):
        data["imuBroadcasterTES"] = msg

    def jointStateBroadcasterTES_callback(self, msg):
        data["jointStateBroadcasterTES"] = msg

    def jointStates_callback(self, msg):
        data["jointStates"] = msg

    def odom_callback(self, msg):
        data["odom"] = msg
    
    def parameterEvent_callback(self, msg):
        data["parameterEvent"] = msg

    def servoCollisionVelScale_callback(self, msg):
        data["servoCollisionVelScale"] = msg

    def servoDeltaJoint_callback(self, msg):
        data["servoDeltaJoint"] = msg

    def servoDeltaTwist_callback(self, msg):
        data["servoDeltaTwist"] = msg

    def servoPubPlanningScene_callback(self, msg):
        data["servoPubPlanningScene"] = msg

    def servoStatus_callback(self, msg):
        data["servoStatus"] = msg

    def robotDescription_callback(self, msg):
        data["robotDescription"] = msg
    
    def tf_callback(self, msg):
        data["tf"] = msg
    
    def tfStatic_callback(self, msg):
        data["tfStatic"] = msg

    def rosout_callback(self, msg):
        data["rosout"] = msg
    
    def scan_callback(self, msg):
        data["scan"] = msg

# creating a Flask app
app = Flask(__name__)
CORS(app)

topics = [
    {"topic":'dynamicJointState', "msgType": DynamicJointState},
    {"topic":'armControllerState', "msgType": JointTrajectoryControllerState},
    {"topic":'armJointTrajectory', "msgType": JointTrajectory},
    {"topic":'armState', "msgType": JointTrajectoryControllerState},
    {"topic":'armTES', "msgType": TransitionEvent},
    {"topic":'cmdVel', "msgType": Twist},
    {"topic":'diffDriveTES', "msgType": TransitionEvent},
    {"topic":'gripperTES', "msgType": TransitionEvent},
    {"topic":'imuBroadcaster', "msgType": Imu},
    {"topic":'imuBroadcasterTES', "msgType": TransitionEvent},
    {"topic":'jointStateBroadcasterTES', "msgType": TransitionEvent},
    {"topic":'jointStates', "msgType": JointState},
    {"topic":'odom', "msgType": Odometry},
    {"topic":'parameterEvent', "msgType": ParameterEvent},
    {"topic":'servoCollisionVelScale', "msgType": Float64},
    {"topic":'servoDeltaJoint', "msgType": JointJog},
    {"topic":'servoDeltaTwist', "msgType": TwistStamped},
    {"topic":'servoPubPlanningScene', "msgType": PlanningScene},
    {"topic":'servoStatus', "msgType": Int8},
    {"topic":'robotDescription', "msgType": String},
    {"topic":'tf', "msgType": TFMessage},
    {"topic":'tfStatic', "msgType": TFMessage},
    {"topic":'rosout', "msgType": Log},
    {"topic":'scan', "msgType": LaserScan},
]

def initTopicData():
    for topic in topics:
        data[topic["topic"]] = ""

def msgToObject(msg, msgType):
    return deserialize_cdr(msg, msgType)

@app.route('/', methods = ['GET'])
def home():
    t = []
    for topic in topics:
        t.append(topic["topic"])

    return jsonify({"topics": t})

    

@app.route('/scan', methods = ['GET'])
def scan():
    return messageJson("scan")

@app.route('/dynamicJointState', methods = ['GET'])
def dynamicJointState():
    return messageJson("dynamicJointState")

@app.route('/armControllerState', methods = ['GET'])
def armControllerState():
    return messageJson("armControllerState")

@app.route('/armJointTrajectory', methods = ['GET'])
def armJointTrajectory():
    return messageJson("armJointTrajectory")

@app.route('/armState', methods = ['GET'])
def armState():
    return messageJson("armState")

@app.route('/armTES', methods = ['GET'])
def armTES():
    return messageJson("armTES")


#curl http://127.0.0.1:1111/cmdVel -d '{"angularx": 2.0,"angulary": 1.0, "angularz": 1.0, "linearx": 2.0, "lineary": 1.0, "linearz": 1.0}' -H 'Content-Type: application/json'
@app.route('/cmdVel', methods = ['GET'])
def cmdVel():
    if request.method == 'GET':
        return messageJson("cmdVel")

@app.route('/diffDriveTES', methods = ['GET'])
def diffDriveTES():
    return messageJson("diffDriveTES")

@app.route('/gripperTES', methods = ['GET'])
def gripperTES():
    return messageJson("gripperTES")

@app.route('/imuBroadcaster', methods = ['GET'])
def imuBroadcaster():
    return messageJson("imuBroadcaster")

@app.route('/imuBroadcasterTES', methods = ['GET'])
def imuBroadcasterTES():
    return messageJson("imuBroadcasterTES")

@app.route('/jointStateBroadcasterTES', methods = ['GET'])
def jointStateBroadcasterTES():
    return messageJson("jointStateBroadcasterTES")

@app.route('/jointStates', methods = ['GET'])
def jointStates():
    return messageJson("jointStates")

@app.route('/odom', methods = ['GET'])
def odom():
    return messageJson("odom")

@app.route('/parameterEvent', methods = ['GET'])
def parameterEvent():
    return messageJson("parameterEvent")

@app.route('/servoCollisionVelScale', methods = ['GET'])
def servoCollisionVelScale():
    return messageJson("servoCollisionVelScale")

@app.route('/servoDeltaJoint', methods = ['GET'])
def servoDeltaJoint():
    return messageJson("servoDeltaJoint")

@app.route('/servoDeltaTwist', methods = ['GET'])
def servoDeltaTwist():
    return messageJson("servoDeltaTwist")

@app.route('/servoPubPlanningScene', methods = ['GET'])
def servoPubPlanningScene():
    return messageJson("servoPubPlanningScene")

@app.route('/servoStatus', methods = ['GET'])
def servoStatus():
    return messageJson("servoStatus")

@app.route('/robotDescription', methods = ['GET'])
def robotDescription():
    return messageJson("robotDescription")

@app.route('/tf', methods = ['GET'])
def tf():
    return messageJson("tf")

@app.route('/tfStatic', methods = ['GET'])
def tfStatic():
    return messageJson("tfStatic")

@app.route('/rosout', methods = ['GET'])
def rosout():
    return messageJson("rosout")


def messageJson(topic):
    if data[topic] == "":
        return json.dumps({})
    d = convert.message_to_ordereddict(data[topic])
    d.update({'topic': topic})
    return json.dumps(d, indent=4)


def startFlask():
    initTopicData()
    app.run(port=1111, host='0.0.0.0')

def main(args=None):
    thread = threading.Thread(target=startFlask)

    thread.start()
    rclpy.init(args=args)

    minimal_subscriber = MinimalSubscriber()

    rclpy.spin(minimal_subscriber)

    

    # Destroy the node explicitly
    # (optional - otherwise it will be done automatically
    # when the garbage collector destroys the node object)
    minimal_subscriber.destroy_node()
    rclpy.shutdown()


if __name__ == '__main__':
    main()
