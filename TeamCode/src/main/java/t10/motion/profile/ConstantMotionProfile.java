package t10.motion.profile;

import t10.geometry.MovementVector;

public class ConstantMotionProfile implements IMotionProfile {
	@Override
	public MovementVector calculate(MovementVector initialVelocity, MovementVector maxVelocity, MovementVector endVelocity, MovementVector maxAcceleration, double distanceTraveled, double totalDistance) {
		return maxVelocity;
	}
}
