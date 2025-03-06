package t10.utils;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.geometry.MovementVector;
import t10.geometry.Pose;

public class OdometryUtils {
	public static MovementVector changeToRobotCenteredCoordinates(MovementVector absoluteVelocity, Pose currentPose) {
		return changeToRobotCenteredCoordinates(absoluteVelocity.getVertical(), absoluteVelocity.getHorizontal(), absoluteVelocity.getRotation(), currentPose.getHeading(AngleUnit.RADIANS));
	}

	public static MovementVector changeToRobotCenteredCoordinates(double lateral, double horizontal, double currentAngle) {
		double forwardRelative = (lateral * Math.cos(currentAngle) + horizontal * Math.sin(currentAngle));
		double rightwardRelative = -lateral * Math.sin(currentAngle) + horizontal * Math.cos(currentAngle);
		return new MovementVector(forwardRelative, rightwardRelative, 0, AngleUnit.DEGREES);
	}

	public static MovementVector changeToRobotCenteredCoordinates(double lateral, double horizontal, double rotational, double currentAngle) {
		double forwardRelative = (lateral * Math.cos(currentAngle) + horizontal * Math.sin(currentAngle));
		double rightwardRelative = -lateral * Math.sin(currentAngle) + horizontal * Math.cos(currentAngle);
		return new MovementVector(forwardRelative, rightwardRelative, rotational, AngleUnit.DEGREES);
	}

	/**
	 * Used to convert forward and rightward coordinates into field-relative vertical and horizontal coordinates.
	 *
	 * @param forward      The distance the robot drives in its forward direction.
	 * @param rightward    The distance the robot drives in its rightward direction.
	 * @param currentAngle IN RADIANS! Otherwise, it'll break! 0 radians is forward.
	 * @return The movement vector that has been adjusted to field centric coordinates.
	 */
	public static MovementVector changeToFieldCenteredCoordinates(double forward, double rightward, double currentAngle) {
		//converts x and y positions from robot-relative to field-relative
		double deltaX = forward * (Math.sin(currentAngle)) + rightward * Math.cos(currentAngle);
		double deltaY = forward * Math.cos(currentAngle) - rightward * Math.sin(currentAngle);
		return new MovementVector(deltaY, deltaX, 0, AngleUnit.DEGREES);
	}

	public static MovementVector changeToFieldCenteredCoordinates(MovementVector relativeVelocity, Pose currentPose) {
		return changeToFieldCenteredCoordinates(relativeVelocity.getVertical(), relativeVelocity.getHorizontal(), currentPose.getHeading(AngleUnit.RADIANS));
	}


	public static Pose convertFromFTCFieldToOurConventions(Pose pose) {
		return new Pose(-pose.getX(), pose.getY(), -pose.getHeading(AngleUnit.RADIANS) + Math.PI, AngleUnit.RADIANS);
	}

	//These two are the same because inversion undoes inversion, but they are left separate for clarity
	public static Pose convertFromOurConventionsToFTCField(Pose pose) {
		return new Pose(pose.getX(),-pose.getY(),-pose.getHeading(AngleUnit.RADIANS) + Math.PI,AngleUnit.RADIANS);
	}
}
