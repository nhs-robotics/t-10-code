package t10.motion.profile;

import t10.geometry.MovementVector;
import t10.geometry.Pose;

@FunctionalInterface
public interface IMotionProfile {
	MovementVector calculate(
			MovementVector initialVelocity,
			MovementVector maxVelocity,
			MovementVector endVelocity,
			MovementVector maxAcceleration,
			Pose initialPose,
			Pose currentPose,
			MovementVector currentVelocity,
			Pose finalPose,
			double lookahead
	);
}
