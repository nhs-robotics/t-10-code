package t10.motion.profile;

import t10.geometry.MovementVector;

@FunctionalInterface
public interface IMotionProfile {
	MovementVector calculate(
			MovementVector initialVelocity,
			MovementVector maxVelocity,
			MovementVector endVelocity,
			MovementVector maxAcceleration,
			double distanceTraveled,
			double totalDistance
	);
}
