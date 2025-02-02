package t10.auto;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.geometry.MovementVector;
import t10.geometry.Pose;
import t10.localizer.Localizer;
import t10.motion.mecanum.MecanumDriver;
import t10.motion.profile.MotionProfile;
import t10.utils.MathUtils;

public class MoveToAction implements AutoAction {

	/**
	 * The maximum amount of error allowed in distance in inches. Current recommended is 0.5 inches.
	 */
	private final double maxRotationError;
	/**
	 * The maximum amount of error allowed in rotation in degrees. Current recommended is 1.5 degrees.
	 */
	private final double maxDistanceError;
	private final Localizer<Pose> localizer;
	private final MecanumDriver driver;
	private final Pose destinationPose;
	private final double movementSpeed;
	private final double rotationalSpeed;

	/**
	 * Moves the robot from its current pose to a new `destinationPose`.
	 */
	public MoveToAction(Localizer<Pose> localizer, MecanumDriver driver, Pose destinationPose) {
		this(localizer, driver, destinationPose, 2, 2, 40, 70);
	}

	/**
	 * Creates a MoveToAction.
	 *
	 * @param localizer        Localizer this robot has.
	 * @param driver           Driver to drive the robot with.
	 * @param destinationPose  The pose to move to.
	 * @param maxDistanceError The maximum amount of distance error in inches.
	 * @param maxRotationError The maximum amount of rotational error in degrees.
	 * @param movementSpeed    Speed to move the robot vertically and horizontally in in/s.
	 * @param rotationalSpeed  Speed to rotate the robot in in/s.
	 */
	public MoveToAction(Localizer<Pose> localizer, MecanumDriver driver, Pose destinationPose, double maxDistanceError, double maxRotationError, double movementSpeed, double rotationalSpeed) {
		this.localizer = localizer;
		this.driver = driver;
		this.destinationPose = destinationPose;
		this.maxDistanceError = maxDistanceError;
		this.maxRotationError = maxRotationError;
		this.movementSpeed = movementSpeed;
		this.rotationalSpeed = rotationalSpeed;
	}

	@Override
	public void init() {
	}

	@Override
	public void loop() {
		Pose currentPose = this.localizer.getFieldCentric();

		double dy = this.destinationPose.getY() - currentPose.getY();
		double dx = this.destinationPose.getX() - currentPose.getX();
		double dh = MathUtils.angleDifference(
				currentPose.getHeading(AngleUnit.RADIANS),
				this.destinationPose.getHeading(AngleUnit.RADIANS),
				AngleUnit.RADIANS
		);

		double vy = this.movementSpeed / (1 + Math.pow(0.1 * Math.E, dx)) - (this.movementSpeed / 2);
		double vx = this.movementSpeed / (1 + Math.pow(0.1 * Math.E, dy)) - (this.movementSpeed / 2);
		double vh = this.rotationalSpeed / (1 + Math.pow(1.5 * Math.E, -dh)) - (this.rotationalSpeed / 2);

		MovementVector vector = new MovementVector(
				vy,
				vx,
				vh,
				AngleUnit.RADIANS
		);

		this.driver.setVelocityFieldCentric(
				currentPose,
				vector
		);
	}

	@Override
	public boolean isComplete() {
		Pose fieldCentricPose = this.localizer.getFieldCentric();

		boolean isDistanceComplete = fieldCentricPose.distanceTo(this.destinationPose) < this.maxDistanceError;
		boolean isRotationComplete = Math.abs(this.destinationPose.getHeading(AngleUnit.DEGREES) - fieldCentricPose.getHeading(AngleUnit.DEGREES)) < this.maxRotationError;
		boolean isComplete = isDistanceComplete && isRotationComplete;

		if (isComplete) {
			this.driver.halt();
		}

		return isComplete;
	}
}


