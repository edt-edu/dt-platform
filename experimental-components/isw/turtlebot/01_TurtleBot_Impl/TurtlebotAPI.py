#!/usr/bin/env python
from flask import Flask, jsonify, request
from flask_cors import CORS
import threading
from shared_memory_dict import SharedMemoryDict
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



smd_config = SharedMemoryDict(name='data', size=1024*100)
smd_action = SharedMemoryDict(name='action', size=1024*100)
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

topicData = {}

def initTopicData():
    for topic in topics:
        topicData.update({topic["topic"]: ""})

def msgToObject(msg, msgType):
    return deserialize_cdr(msg, msgType)

#Topics are used as keys
def getData():
    prev = ''
    while True:
        for topic in topics:
            shared_memory_key = topic["topic"]
            try:
                if shared_memory_key in smd_config.keys():
                    msg = smd_config[shared_memory_key]
                    if msg != prev:
                        topicData[shared_memory_key] = msg
                        prev = msg
            except:
                print("Error in: " + shared_memory_key)

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
@app.route('/cmdVel', methods = ['GET','POST'])
def cmdVel():
    if request.method == 'POST':
        data = request.get_json()
        twist = Twist()
        twist.angular.x = float(data['angularx'])
        twist.angular.y = float(data['angulary'])
        twist.angular.z = float(data['angularz'])
        twist.linear.x = float(data['linearx'])
        twist.linear.y = float(data['lineary'])
        twist.linear.z = float(data['linearz'])
        smd_action['cmd_vel'] = twist
        return jsonify({"Success": "true"})
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
    data = topicData[topic]
    if data == "":
        return json.dumps({})
    d = convert.message_to_ordereddict(data)
    d.update({'topic': topic})
    return json.dumps(d, indent=4)

# driver function
if __name__ == '__main__':
    initTopicData()

    
    thread = threading.Thread(target=getData)

    thread.start()
    app.run(debug = True, port=1111, host='0.0.0.0')