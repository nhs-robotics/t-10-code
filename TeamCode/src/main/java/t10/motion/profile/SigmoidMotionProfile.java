package t10.motion.profile;

import t10.geometry.MovementVector;

public class SigmoidMotionProfile implements IMotionProfile {
	// TODO: ensure that all MovementVectors have same angle unit, currently the could mix (bad)
	@Override
	public MovementVector calculate(MovementVector initialVelocity, MovementVector maxVelocity, MovementVector endVelocity, MovementVector maxAcceleration, double distanceTraveled, double totalDistance) {
		final double slowdownCoefficient = 0.5 * Math.E;  // This will really start pumping the brakes at about 10in out from destination
		double remainingDistance = totalDistance - distanceTraveled;

		double mvx = maxVelocity.getHorizontal();
		double mvy = maxVelocity.getVertical();
		double mvr = maxVelocity.getRotation();

		double vx = (2 * mvx) / (1 + Math.pow(slowdownCoefficient, -remainingDistance)) - mvx + endVelocity.getHorizontal();
		double vy = (2 * mvy) / (1 + Math.pow(slowdownCoefficient, -remainingDistance)) - mvy + endVelocity.getVertical();
		double vh = (2 * mvr) / (1 + Math.pow(slowdownCoefficient, -remainingDistance)) - mvr + endVelocity.getRotation();

		return new MovementVector(vx, vy, vh, maxVelocity.getAngleUnit());
	}
}
