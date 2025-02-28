package t10.localizer;

import intothedeep.Constants;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.geometry.Pose;
import t10.motion.hardware.MotorEncoder;
import t10.motion.mecanum.MecanumCoefficientMatrix;
import t10.motion.mecanum.MecanumDriver;

/**
 * A localizer for a mecanum drivetrain. Not recommended, but it works to an extent.
 */
public class MecanumEncodersLocalizer implements Localizer<Pose> {
	private final MotorEncoder fr;
	private final MotorEncoder br;
	private final MotorEncoder fl;
	private final MotorEncoder bl;
	private final MecanumCoefficientMatrix coefficients;
	private double lastFrInches;
	private double lastBrInches;
	private double lastFlInches;
	private double lastBlInches;
	private Pose fieldCentricPose;

	public MecanumEncodersLocalizer(MecanumDriver driver) {
		this.coefficients = driver.omniDriveCoefficients;
		this.fr = driver.frontRight.encoder;
		this.br = driver.backRight.encoder;
		this.fl = driver.frontLeft.encoder;
		this.bl = driver.backLeft.encoder;
		this.lastFrInches = this.fr.getCurrentInches();
		this.lastBrInches = this.br.getCurrentInches();
		this.lastFlInches = this.fl.getCurrentInches();
		this.lastBlInches = this.bl.getCurrentInches();
		this.setFieldCentric(new Pose(0, 0, 0, AngleUnit.DEGREES));
	}

	@Override
	public void loop() {
		// Calculate change in encoder ticks
		double frInches = this.fr.getCurrentInches() * this.coefficients.totals.frontRight;
		double brInches = this.br.getCurrentInches() * this.coefficients.totals.backRight;
		double flInches = this.fl.getCurrentInches() * this.coefficients.totals.frontLeft;
		double blInches = this.bl.getCurrentInches() * this.coefficients.totals.backLeft;
		double deltaFr = frInches - this.lastFrInches;
		double deltaBr = brInches - this.lastBrInches;
		double deltaFl = flInches - this.lastFlInches;
		double deltaBl = blInches - this.lastBlInches;

		// Calculate robot moveTo components
		double deltaForward = (deltaFl + deltaFr + deltaBl + deltaBr) / 4.0;
		double deltaRight = (deltaFl - deltaFr - deltaBl + deltaBr) / 4.0;
		double deltaRotation = (deltaFl - deltaFr + deltaBl - deltaBr) / (4.0 * Constants.Robot.ROBOT_WIDTH_IN);

		// Convert to global coordinates based on current heading
		double currentHeading = this.fieldCentricPose.getHeading(AngleUnit.RADIANS) + deltaRotation;
		double deltaX = deltaRight * Math.cos(currentHeading) - deltaForward * Math.sin(currentHeading);
		double deltaY = deltaRight * Math.sin(currentHeading) + deltaForward * Math.cos(currentHeading);

		// Update pose
		Pose deltaPose = new Pose(deltaY, deltaX, deltaRotation, AngleUnit.RADIANS);
		this.fieldCentricPose = this.fieldCentricPose.add(deltaPose);

		// Update last tick values
		this.lastFrInches = frInches;
		this.lastBrInches = brInches;
		this.lastFlInches = flInches;
		this.lastBlInches = blInches;
	}

	@Override
	public Pose getFieldCentric() {
		return this.fieldCentricPose;
	}

	@Override
	public void setFieldCentric(Pose pose) {
		this.fieldCentricPose = pose;
		this.fr.reset();
		this.br.reset();
		this.fl.reset();
		this.bl.reset();
		this.lastFrInches = this.fr.getCurrentInches();
		this.lastBrInches = this.br.getCurrentInches();
		this.lastFlInches = this.fl.getCurrentInches();
		this.lastBlInches = this.bl.getCurrentInches();
	}
}
