package t10.auto;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.geometry.MovementVector;
import t10.geometry.Pose;
import t10.localizer.Localizer;
import t10.motion.mecanum.MecanumDriver;
import t10.utils.MathUtils;

/**
 * An {@link AutoAction} that moves the robot to a position on the field.
 */
public class MoveToAction implements AutoAction {
	/**
	 * The maximum amount of error allowed in distance in inches.
	 */
	private final double maxRotationError;

	/**
	 * The maximum amount of error allowed in rotation in degrees.
	 */
	private final double maxDistanceError;

	/**
	 * The localizer to keep track of the robot's position
	 */
	private final Localizer<Pose> localizer;

	/**
	 * The driver to drive the robot with.
	 */
	private final MecanumDriver driver;

	/**
	 * The destination position and orientation that the robot will drive to.
	 */
	private final Pose destinationPose;

	/**
	 * The speed of the robot to move horizontally and vertically in inches/sec.
	 */
	private final double movementSpeed;

	/**
	 * The speed that the robot will rotate in inches/sec. TODO: make this degrees
	 */
	private final double rotationalSpeed;

	/**
	 * Moves the robot from its current pose to a new `destinationPose`.
	 *
	 * @param localizer       The localizer to keep track of the robot's position
	 * @param driver          The driver to drive the robot with.
	 * @param destinationPose The destination position and orientation that the robot will drive to.
	 */
	public MoveToAction(Localizer<Pose> localizer, MecanumDriver driver, Pose destinationPose) {
		this(localizer, driver, destinationPose, 1.25, 1.25, 30, 50);
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

		double vy = this.movementSpeed / (1 + Math.pow(0.5 * Math.E, -dy)) - (this.movementSpeed / 2);
		double vx = this.movementSpeed / (1 + Math.pow(0.5 * Math.E, -dx)) - (this.movementSpeed / 2);
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
	
	private double ensureMinimumSpeed(double v, double min) {
		if (v < min && -min < v) {
			return min * Math.signum(v);
		}

		return v;
	}
}
