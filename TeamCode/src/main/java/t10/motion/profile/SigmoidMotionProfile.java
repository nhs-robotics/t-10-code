package t10.motion.profile;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;


import t10.geometry.*;
import t10.utils.MathUtils;

public class SigmoidMotionProfile implements IMotionProfile {
	// TODO: ensure that all MovementVectors have same angle unit, currently the could mix (bad)
	@Override
	public MovementVector calculate(MovementVector initialVelocity, MovementVector maxVelocity, MovementVector endVelocity, MovementVector maxAcceleration, Pose initialPose, Pose currentPose, MovementVector currentVelocity, Pose finalPose, double lookAhead) {
		final double slowdownCoefficient = 0.1 * Math.E;
		double deltaX = finalPose.getX() - currentPose.getX();
		double deltaY = finalPose.getY() - currentPose.getY();
		double deltaH = MathUtils.angleDifference(
				currentPose.getHeading(AngleUnit.RADIANS),
				finalPose.getHeading(AngleUnit.RADIANS),
				AngleUnit.RADIANS);

		double remainingDistance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

		double mvx = maxVelocity.getHorizontal();
		double mvy = maxVelocity.getVertical();
		double mvh = maxVelocity.getRotation();

		double vx = (mvx) / (1 + Math.pow(slowdownCoefficient, deltaX)) - (mvx / 2);
		double vy = (mvy) / (1 + Math.pow(slowdownCoefficient, deltaY)) - (mvy / 2);
		double vh = (mvh) / (1 + Math.pow(slowdownCoefficient, deltaH)) - (mvh / 2);

		return new MovementVector(vy, vx, vh, maxVelocity.getAngleUnit());
	}
}
