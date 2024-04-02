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
# limitations under the License.  ubuntu@192.168.137.26

import rclpy
from rclpy.node import Node
from shared_memory_dict import SharedMemoryDict
import threading

from std_msgs.msg import String
from geometry_msgs.msg import Twist, TwistStamped
from control_msgs.msg import JointJog
from flask import Flask, jsonify, request
from flask_cors import CORS

data = {}
class MinimalPublisher(Node):

    def __init__(self):
        super().__init__('minimal_publisher')
        self.publisher_cmdVel = self.create_publisher(Twist, 'cmd_vel', 10)
        self.publisher_servoDeltaTwist = self.create_publisher(TwistStamped, 'servo_node/delta_twist_cmds', 10)
        self.publisher_servoDeltaJoint = self.create_publisher(JointJog, 'servo_node/delta_joint_cmds', 10)
        timer_period = 0.1  # seconds
        data['cmd_vel'] = None
        data['servoDeltaTwist'] = None
        data['servoDeltaJoint'] = None
        data['jointJog'] = JointJog()
        self.timer = self.create_timer(timer_period, self.timer_callback)

    def timer_callback(self):
        data["stamp"] = self.get_clock().now().to_msg()
        if data['cmd_vel'] is not None:
            self.publisher_cmdVel.publish(data['cmd_vel'])
            self.get_logger().info('Publishing: "%s"' % data['cmd_vel'])
            data['cmd_vel'] = None
        if data['servoDeltaTwist'] is not None:
            self.publisher_servoDeltaTwist.publish(data['servoDeltaTwist'])
            self.get_logger().info('Publishing: "%s"' % data['servoDeltaTwist'])
            data['servoDeltaTwist'] = None
        if data['servoDeltaJoint'] is not None:
            self.publisher_servoDeltaJoint.publish(data['servoDeltaJoint'])
            self.get_logger().info('Publishing: "%s"' % data['servoDeltaJoint'])
            data['servoDeltaJoint'] = None

# creating a Flask app
app = Flask(__name__)
CORS(app)

#curl http://127.0.0.1:1112/cmdVel -d '{"angularx": 2.0,"angulary": 1.0, "angularz": 1.0, "linearx": 2.0, "lineary": 1.0, "linearz": 1.0}' -H 'Content-Type: application/json'
@app.route('/cmdVel', methods = ['POST'])
def cmdVel():
    if request.method == 'POST':
        json = request.get_json()
        twist = Twist()
        twist.angular.x = float(json['angularx'])
        twist.angular.y = float(json['angulary'])
        twist.angular.z = float(json['angularz'])
        twist.linear.x = float(json['linearx'])
        twist.linear.y = float(json['lineary'])
        twist.linear.z = float(json['linearz'])
        data['cmd_vel'] = twist
        return jsonify({"Success": "true"})
#curl http://127.0.0.1:1112/servoDeltaTwist -d '{"angularx": 2.0,"angulary": 1.0, "angularz": 1.0, "linearx": 2.0, "lineary": 1.0, "linearz": 1.0}' -H 'Content-Type: application/json'   
@app.route('/servoDeltaTwist', methods = ['POST'])
def servoDeltaTwist():
    if request.method == 'POST':
        json = request.get_json()
        twistStamped = TwistStamped()
        twistStamped.header.stamp = data["stamp"]
        twistStamped.header.frame_id = "link2"
        twistStamped.twist.angular.x = float(json['angularx'])
        twistStamped.twist.angular.y = float(json['angulary'])
        twistStamped.twist.angular.z = float(json['angularz'])
        twistStamped.twist.linear.x = float(json['linearx'])
        twistStamped.twist.linear.y = float(json['lineary'])
        twistStamped.twist.linear.z = float(json['linearz'])
        data['servoDeltaTwist'] = twistStamped
        return jsonify({"Success": "true"})

#curl http://127.0.0.1:1112/servoDeltaJoint -d '{"joint": "joint2", "velocity": -1.0}' -H 'Content-Type: application/json' 
@app.route('/servoDeltaJoint', methods = ['POST'])
def servoDeltaJoint():
    if request.method == 'POST':
        json = request.get_json()
        data['jointJog'].header.stamp = data['stamp']
        data['jointJog'].header.frame_id = "link2"
        data['jointJog'].joint_names.append(str(json['joint']))
        data['jointJog'].velocities.append(float(json['velocity']))
        data['servoDeltaJoint'] = data['jointJog']
        return jsonify({"Success": "true"})

def startFlask():
    app.run(port=1112, host='0.0.0.0')

def main(args=None):
    thread = threading.Thread(target=startFlask)

    thread.start()
    rclpy.init(args=args)

    minimal_publisher = MinimalPublisher()

    rclpy.spin(minimal_publisher)

    # Destroy the node explicitly
    # (optional - otherwise it will be done automatically
    # when the garbage collector destroys the node object)
    minimal_publisher.destroy_node()
    rclpy.shutdown()


if __name__ == '__main__':
    main()