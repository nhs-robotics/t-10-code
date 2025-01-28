package t10.utils;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.geometry.MovementVector;
import t10.geometry.Pose;

public class OdometryUtils {
	public static MovementVector changeToRobotCenteredVelocity(MovementVector absoluteVelocity, Pose pose) {
		double theta = pose.getHeading(AngleUnit.RADIANS);
		double forwardRelative = (absoluteVelocity.getVertical() * Math.cos(theta) + absoluteVelocity.getHorizontal() * Math.sin(theta));
		double rightwardRelative = -absoluteVelocity.getVertical() * Math.sin(theta) + absoluteVelocity.getHorizontal() * Math.cos(theta);
		return new MovementVector(forwardRelative, rightwardRelative, absoluteVelocity.getRotation(), absoluteVelocity.getAngleUnit());
	}

	// TODO: integrate rotation?
	public static MovementVector changeToRobotCenteredVelocity(double lateral, double horizontal, Pose pose) {
		return changeToRobotCenteredVelocity(new MovementVector(lateral, horizontal, 0, AngleUnit.DEGREES), pose);
	}
}
