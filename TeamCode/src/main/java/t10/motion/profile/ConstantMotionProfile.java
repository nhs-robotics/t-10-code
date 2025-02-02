package t10.motion.profile;

import t10.geometry.MovementVector;
import t10.geometry.Pose;

public class ConstantMotionProfile implements IMotionProfile {
	@Override
	public MovementVector calculate(MovementVector initialVelocity, MovementVector maxVelocity, MovementVector endVelocity, MovementVector maxAcceleration, Pose initialPose, Pose currentPose, Pose finalPose, double lookAhead) {
		return maxVelocity;
	}
}
