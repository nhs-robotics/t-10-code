package t10.motion.mecanum;

import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.geometry.MovementVector;
import t10.geometry.Pose;
import t10.motion.hardware.Motor;

/**
 * Driving capabilities for mecanum robots.
 */
public class MecanumDriver {
	public final Motor frontLeft;
	public final Motor frontRight;
	public final Motor backLeft;
	public final Motor backRight;
	public final MecanumCoefficientMatrix omniDriveCoefficients;

	public MecanumDriver(
			Motor frontLeft,
			Motor frontRight,
			Motor backLeft,
			Motor backRight,
			MecanumCoefficientMatrix omniDriveCoefficients
	) {
		this.frontLeft = frontLeft;
		this.frontRight = frontRight;
		this.backLeft = backLeft;
		this.backRight = backRight;
		this.omniDriveCoefficients = omniDriveCoefficients;
	}

	public void setVelocity(MovementVector velocity) {
		MecanumCoefficientSet coefficientSet = this.omniDriveCoefficients.calculateCoefficientsWithPower(
				velocity.getVertical(),
				velocity.getHorizontal(),
				velocity.getRotation()
		);

		this.setVelocity(
				coefficientSet.frontLeft,
				coefficientSet.frontRight,
				coefficientSet.backLeft,
				coefficientSet.backRight
		);
	}

	public void setVelocity(double frontLeft, double frontRight, double backLeft, double backRight) {
		this.frontLeft.setVelocity(frontLeft);
		this.frontRight.setVelocity(frontRight);
		this.backLeft.setVelocity(backLeft);
		this.backRight.setVelocity(backRight);
	}

	public void setVelocityFieldCentric(Pose currentPose, MovementVector vector) {
		double theta = currentPose.getHeading(AngleUnit.RADIANS);
		double forwardRelative = vector.getVertical() * Math.cos(theta) + vector.getHorizontal() * Math.sin(theta);
		double rightwardRelative = -vector.getVertical() * Math.sin(theta) + vector.getHorizontal() * Math.cos(theta);

		this.setVelocity(
				new MovementVector(
						forwardRelative,
						rightwardRelative,
						vector.getRotation(),
						vector.getAngleUnit()
				)
		);
	}

	public void halt() {
		this.setVelocity(0, 0, 0, 0);
	}

	/**
	 * Call this method in loop in tele-op to allow the gamepad to be used for driving.
	 *
	 * @param gamepad        The gamepad that controls the robot's moveTo.
	 * @param powerMultipler Used for micro-moveTo, set to 0.5 for micro-moveTo.
	 */
	public void useGamepad(Gamepad gamepad, double powerMultipler) {
		MecanumCoefficientSet coefficientSet = this.omniDriveCoefficients.calculateCoefficientsWithPower(
				-gamepad.left_stick_y,
				gamepad.left_stick_x,
				gamepad.right_stick_x
		);

		this.frontLeft.motor.setPower(coefficientSet.frontLeft * powerMultipler);
		this.frontRight.motor.setPower(coefficientSet.frontRight * powerMultipler);
		this.backLeft.motor.setPower(coefficientSet.backLeft * powerMultipler);
		this.backRight.motor.setPower(coefficientSet.backRight * powerMultipler);
	}
}
