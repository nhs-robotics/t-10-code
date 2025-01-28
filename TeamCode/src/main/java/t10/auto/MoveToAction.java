package t10.auto;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.geometry.MovementVector;
import t10.geometry.Pose;
import t10.localizer.Localizer;
import t10.motion.mecanum.MecanumDriver;
import t10.utils.MathUtils;
import t10.utils.OdometryUtils;

public class MoveToAction implements AutoAction {
	/**
	 * The maximum amount of error allowed in distance in inches. Current recommended is 0.5 inches.
	 */
	private static final double MAX_ERROR_DISTANCE = 0.5;

	/**
	 * The maximum amount of error allowed in rotation in degrees. Current recommended is 2 degrees.
	 */
	private static final double MAX_ERROR_ROTATION = 2;
	private final Localizer localizer;
	private final MecanumDriver driver;
	private final Pose destinationPose;
	private final double speed;

	/**
	 * Moves the robot from its current pose to a new `destinationPose`.
	 */
	public MoveToAction(Localizer localizer, MecanumDriver driver, Pose destinationPose, double speed) {
		this.localizer = localizer;
		this.driver = driver;
		this.destinationPose = destinationPose;
		this.speed = speed;
	}

	@Override
	public void init() {
	}

	@Override
	public void loop() {
		Pose difference = this.destinationPose.subtract(this.localizer.getFieldCentricPose());

		MovementVector desiredMovement = new MovementVector(
				MathUtils.clamp(difference.getY(), -this.speed, this.speed),
				MathUtils.clamp(difference.getX(), -this.speed, this.speed),
				MathUtils.clamp(difference.getHeading(AngleUnit.DEGREES), -50, 50),
				AngleUnit.DEGREES
		);

		MovementVector robotCentricMovementVector = OdometryUtils.changeToRobotCenteredVelocity(
				desiredMovement,
				this.localizer.getFieldCentricPose()
		);

		this.driver.setVelocity(robotCentricMovementVector);
	}

	@Override
	public boolean isComplete() {
		Pose fieldCentricPose = this.localizer.getFieldCentricPose();

		boolean isDistanceComplete = fieldCentricPose.distanceTo(this.destinationPose) < MAX_ERROR_DISTANCE;
		boolean isRotationComplete = Math.abs(this.destinationPose.getHeading(AngleUnit.DEGREES) - fieldCentricPose.getHeading(AngleUnit.DEGREES)) < MAX_ERROR_ROTATION;

		return isDistanceComplete && isRotationComplete;
	}
}
