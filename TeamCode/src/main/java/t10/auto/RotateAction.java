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
public class RotateAction implements AutoAction {
	/**
	 * The maximum amount of error allowed in distance in inches.
	 */
	private final double maxRotationError;

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
	 * The speed that the robot will rotate in inches/sec. TODO: make this degrees
	 */
	private final double rotationalSpeed;

	private final double minSpeed;


	/**
	 * Creates a MoveToAction.
	 *
	 * @param localizer        Localizer this robot has.
	 * @param driver           Driver to drive the robot with.
	 * @param destinationPose  The pose to move to.
	 * @param maxRotationError The maximum amount of rotational error in degrees.
	 * @param rotationalSpeed  Speed to rotate the robot in in/s.
	 */
	public RotateAction(Localizer<Pose> localizer, MecanumDriver driver, Pose destinationPose, double maxRotationError, double rotationalSpeed) {
		this.localizer = localizer;
		this.driver = driver;
		this.destinationPose = destinationPose;
		this.maxRotationError = maxRotationError;
		this.rotationalSpeed = rotationalSpeed;
		this.minSpeed = 3;
	}

	public RotateAction(Localizer<Pose> localizer,
						MecanumDriver driver,
						Pose destinationPose,
						double maxRotationError,
						double rotationalSpeed,
						double minSpeed) {
		this.localizer = localizer;
		this.driver = driver;
		this.destinationPose = destinationPose;
		this.maxRotationError = maxRotationError;
		this.rotationalSpeed = rotationalSpeed;
		this.minSpeed = Math.abs(minSpeed);
	}

	@Override
	public void init() {
	}

	@Override
	public void loop() {
		Pose currentPose = this.localizer.getFieldCentric();

		double dh = MathUtils.angleDifference(
				currentPose.getHeading(AngleUnit.RADIANS),
				this.destinationPose.getHeading(AngleUnit.RADIANS),
				AngleUnit.RADIANS
		);

		double vy = 0;

		double vx = 0;

		double vh = this.rotationalSpeed / (1 + Math.pow(1.5 * Math.E, -dh)) - (this.rotationalSpeed / 2);
		vh = Math.max(Math.abs(vh),minSpeed) * Math.signum(vh);
		if(dh < maxRotationError) {
			vh = 0;
		}

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

		boolean isComplete = Math.abs(this.destinationPose.getHeading(AngleUnit.DEGREES) - fieldCentricPose.getHeading(AngleUnit.DEGREES)) < this.maxRotationError;

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
