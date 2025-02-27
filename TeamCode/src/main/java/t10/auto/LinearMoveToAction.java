package t10.auto;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.geometry.MovementVector;
import t10.geometry.Pose;
import t10.localizer.Localizer;
import t10.motion.mecanum.MecanumDriver;
import t10.motion.profile.TrapezoidalMotionProfile;
import t10.utils.MathUtils;

/**
 * An {@link AutoAction} that moves the robot to a position on the field.
 */
public class LinearMoveToAction implements AutoAction {

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

	private final Pose initialPose;

	/**
	 * The destination position and orientation that the robot will drive to.
	 */
	private final Pose destinationPose;

	/**
	 * The speed of the robot to move horizontally and vertically in inches/sec.
	 */
	private final double maxSpeed;

	/**
	 * The speed that the robot will rotate in inches/sec. TODO: make this degrees
	 */
	private final double maxAcceleration;

	private final double minSpeed;

	private final TrapezoidalMotionProfile motionProfile;

	/**
	 * Moves the robot from its current pose to a new `destinationPose`.
	 *
	 * @param localizer       The localizer to keep track of the robot's position
	 * @param driver          The driver to drive the robot with.
	 * @param destinationPose The destination position and orientation that the robot will drive to.
	 */
	public LinearMoveToAction(Localizer<Pose> localizer, MecanumDriver driver, Pose destinationPose) {
		this(localizer, driver, destinationPose, 1.25, 1.25, 30, 50);
	}

	/**
	 * Creates a MoveToAction.
	 *
	 * @param localizer        Localizer this robot has.
	 * @param driver           Driver to drive the robot with.
	 * @param destinationPose  The pose to move to.
	 * @param maxDistanceError The maximum amount of distance error in inches.
	 * @param maxSpeed    Speed to move the robot vertically and horizontally in in/s.
	 */
	public LinearMoveToAction(Localizer<Pose> localizer, MecanumDriver driver, Pose destinationPose, double maxDistanceError, double maxAcceleration, double maxSpeed) {
		this.localizer = localizer;
		this.driver = driver;
		this.initialPose = localizer.getFieldCentric();
		this.destinationPose = destinationPose;
		this.maxDistanceError = maxDistanceError;
		this.maxAcceleration = maxAcceleration;
		this.maxSpeed = maxSpeed;
		this.minSpeed = 3;

		this.motionProfile = new TrapezoidalMotionProfile();
	}

	public LinearMoveToAction(
			Localizer<Pose> localizer,
			MecanumDriver driver,
			Pose destinationPose,
			double maxDistanceError,
			double maxAcceleration,
			double maxSpeed,
			double minSpeed)
	{
		this.localizer = localizer;
		this.driver = driver;
		this.initialPose = localizer.getFieldCentric();
		this.destinationPose = destinationPose;
		this.maxDistanceError = maxDistanceError;
		this.maxSpeed = maxSpeed;
		this.minSpeed = Math.abs(minSpeed);
		this.maxAcceleration = maxAcceleration;
		this.motionProfile = new TrapezoidalMotionProfile();
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


		MovementVector vector = motionProfile.calculate(new MovementVector(0, 0, 0, AngleUnit.RADIANS), new MovementVector(maxSpeed,maxSpeed,maxSpeed,AngleUnit.RADIANS), new MovementVector(minSpeed, minSpeed, minSpeed, AngleUnit.RADIANS), new MovementVector(0, 0, 0, AngleUnit.RADIANS), new MovementVector(maxAcceleration, maxAcceleration, maxAcceleration, AngleUnit.RADIANS) ,initialPose,currentPose,localizer.getVelocity(),destinationPose,1.0);

		vector.setRotation(0,AngleUnit.RADIANS);

		this.driver.setVelocityFieldCentric(
				currentPose,
				vector
		);
	}

	@Override
	public boolean isComplete() {
		Pose fieldCentricPose = this.localizer.getFieldCentric();

		boolean isComplete = fieldCentricPose.distanceTo(this.destinationPose) < this.maxDistanceError;

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
