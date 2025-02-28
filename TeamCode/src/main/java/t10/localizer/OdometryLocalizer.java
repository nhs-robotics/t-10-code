package t10.localizer;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.geometry.Pose;
import t10.motion.hardware.Encoder;

public class OdometryLocalizer implements Localizer<Pose> {
	private final OdometryCoefficientSet coefficients;
	protected final Encoder rightEncoder;
	protected final Encoder leftEncoder;
	protected final Encoder perpendicularEncoder;

	/**
	 * Distance between parallel wheels.
	 */
	private final double lateralWheelDistance;

	/**
	 * Distance from robot center to perpendicular wheel.
	 */
	private final double perpendicularWheelOffset;
	private Pose fieldCentricPose;
	private double leftWheelPos;
	private double rightWheelPos;
	private double perpendicularWheelPos;
	private double deltaLeftWheelPos;
	private double deltaRightWheelPos;
	private double deltaPerpendicularWheelPos;
	private static final double maxDeltaValue = 1;

	/**
	 * @param coefficients             The coefficients to use for the odometers. Chances are this is {@link OdometryCoefficientSet#DEFAULT}.
	 * @param rightEncoder             The right side encoder.
	 * @param leftEncoder              The left side encoder.
	 * @param perpendicularEncoder     The perpendicular encoder.
	 * @param lateralWheelDistance     The distance between the lateral wheels.
	 * @param perpendicularWheelOffset The offset of the perpendicular wheel from the center of the robot chassis.
	 * @see <a href="http://web.archive.org/web/20230529000105if_/https://gm0.org/en/latest/_images/offsets-and-trackwidth.png">Diagram (archived)</a> or <a href="https://gm0.org/en/latest/_images/offsets-and-trackwidth.png">diagram</a>.
	 */
	public OdometryLocalizer(
			OdometryCoefficientSet coefficients,
			Encoder rightEncoder,
			Encoder leftEncoder,
			Encoder perpendicularEncoder,
			double lateralWheelDistance,
			double perpendicularWheelOffset
	) {
		this.coefficients = coefficients;
		this.rightEncoder = rightEncoder;
		this.leftEncoder = leftEncoder;
		this.perpendicularEncoder = perpendicularEncoder;
		this.lateralWheelDistance = lateralWheelDistance;
		this.perpendicularWheelOffset = perpendicularWheelOffset;
	}

	protected double computeDeltaHeading() {
		return (this.deltaLeftWheelPos - this.deltaRightWheelPos) / this.lateralWheelDistance;
	}

	@Override
	public void loop() {
		// Get new wheel positions
		double newLeftWheelPos = this.leftEncoder.getCurrentInches();
		double newRightWheelPos = this.rightEncoder.getCurrentInches();
		double newPerpendicularWheelPos = this.perpendicularEncoder.getCurrentInches();

		// Get changes in odometry wheel positions since last update - results from the robot's perspective
		this.deltaLeftWheelPos = this.coefficients.leftCoefficient * (newLeftWheelPos - this.leftWheelPos);
		if(Math.abs(deltaLeftWheelPos) > maxDeltaValue) {
			deltaLeftWheelPos = maxDeltaValue * Math.signum(deltaLeftWheelPos);
		}

		this.deltaRightWheelPos = this.coefficients.rightCoefficient * (newRightWheelPos - this.rightWheelPos);
		if(Math.abs(deltaRightWheelPos) > maxDeltaValue) {
			deltaRightWheelPos = maxDeltaValue * Math.signum(deltaRightWheelPos);
		}

		this.deltaPerpendicularWheelPos = this.coefficients.perpendicularCoefficient * (newPerpendicularWheelPos - this.perpendicularWheelPos);
		if(Math.abs(deltaPerpendicularWheelPos) > maxDeltaValue) {
			deltaPerpendicularWheelPos = maxDeltaValue * Math.signum(deltaPerpendicularWheelPos);
		}

		// Convert changes in robot-perspective wheel positions into changes in x/y/angle from the robot's perspective
		double deltaHeading = this.computeDeltaHeading();
		double forwardRelative = (deltaLeftWheelPos + deltaRightWheelPos) / 2d;
		double rightwardRelative = deltaPerpendicularWheelPos - this.perpendicularWheelOffset * deltaHeading;

		// Computes the robot's new  heading for purposes of trig
		double heading = this.fieldCentricPose.getHeading(AngleUnit.RADIANS) + deltaHeading;

		//converts x and y positions from robot-relative to field-relative
		double deltaX = forwardRelative * Math.sin(heading) + rightwardRelative * Math.cos(heading);
		double deltaY = forwardRelative * Math.cos(heading) - rightwardRelative * Math.sin(heading);

		// Updates the Pose (position + heading)
		this.fieldCentricPose = this.fieldCentricPose.add(new Pose(deltaY, deltaX, deltaHeading, AngleUnit.RADIANS));

		// Update encoder wheel position
		this.leftWheelPos = newLeftWheelPos;
		this.rightWheelPos = newRightWheelPos;
		this.perpendicularWheelPos = newPerpendicularWheelPos;
	}

	@Override
	public void setFieldCentric(Pose pose) {
		this.fieldCentricPose = pose;  // Reset field-centric pose to match new robot-centric pose
		this.leftEncoder.reset();
		this.rightEncoder.reset();
		this.perpendicularEncoder.reset();
		this.leftWheelPos = this.leftEncoder.getCurrentInches();
		this.rightWheelPos = this.rightEncoder.getCurrentInches();
		this.perpendicularWheelPos = this.perpendicularEncoder.getCurrentInches();
	}

	@Override
	public Pose getFieldCentric() {
		return this.fieldCentricPose;
	}
}
