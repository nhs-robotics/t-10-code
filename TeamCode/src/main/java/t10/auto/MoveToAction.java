package t10.auto;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.geometry.MovementVector;
import t10.geometry.Pose;
import t10.localizer.Localizer;
import t10.motion.mecanum.MecanumDriver;
import t10.utils.MathUtils;

public class MoveToAction implements AutoAction {
	/**
	 * The maximum amount of error allowed in distance in inches. Current recommended is 0.5 inches.
	 */
	private static final double MAX_ERROR_DISTANCE = 0.5;

	/**
	 * The maximum amount of error allowed in rotation in degrees. Current recommended is 2 degrees.
	 */
	private static final double MAX_ERROR_ROTATION = 2;
	private final Localizer<Pose> localizer;
	private final MecanumDriver driver;
	private final Pose destinationPose;

	/**
	 * Moves the robot from its current pose to a new `destinationPose`.
	 */
	public MoveToAction(Localizer<Pose> localizer, MecanumDriver driver, Pose destinationPose) {
		this.localizer = localizer;
		this.driver = driver;
		this.destinationPose = destinationPose;
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

		double vx = 30 / (1 + Math.pow(0.1 * Math.E, dy)) - 15;
		double vy = 30 / (1 + Math.pow(0.1 * Math.E, dx)) - 15;
		double vh = 70 / (1 + Math.pow(1.5 * Math.E, -dh)) - 35;

		MovementVector vector = new MovementVector(
				vx,
				vy,
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

		boolean isDistanceComplete = fieldCentricPose.distanceTo(this.destinationPose) < MAX_ERROR_DISTANCE;
		boolean isRotationComplete = Math.abs(this.destinationPose.getHeading(AngleUnit.DEGREES) - fieldCentricPose.getHeading(AngleUnit.DEGREES)) < MAX_ERROR_ROTATION;
		boolean isComplete = isDistanceComplete && isRotationComplete;

		if (isComplete) {
			this.driver.halt();
		}

		return isComplete;
	}
}
