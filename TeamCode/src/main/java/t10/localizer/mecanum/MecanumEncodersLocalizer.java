package t10.localizer.mecanum;

import intothedeep.Constants;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.geometry.Pose;
import t10.motion.hardware.MotorEncoder;
import t10.motion.mecanum.MecanumDriver;

/**
 * Unstable API. Untested.A localizer for a mecanum drivetrain.
 */
// TODO: Might need to apply coefficients
public class MecanumEncodersLocalizer {
	private final MotorEncoder fr;
	private final MotorEncoder br;
	private final MotorEncoder fl;
	private final MotorEncoder bl;
	private double lastFrInches;
	private double lastBrInches;
	private double lastFlInches;
	private double lastBlInches;
	private Pose currentPose;

	public MecanumEncodersLocalizer(MecanumDriver driver) {
		this.fr = driver.frontRight.encoder;
		this.br = driver.backRight.encoder;
		this.fl = driver.frontLeft.encoder;
		this.bl = driver.backLeft.encoder;
		this.lastFrInches = this.fr.getCurrentInches();
		this.lastBrInches = this.br.getCurrentInches();
		this.lastFlInches = this.fl.getCurrentInches();
		this.lastBlInches = this.bl.getCurrentInches();
		resetPose();
	}

	public void update() {
		// Calculate change in encoder ticks
		double frInches = this.fr.getCurrentInches();
		double brInches = this.br.getCurrentInches();
		double flInches = this.fl.getCurrentInches();
		double blInches = this.bl.getCurrentInches();
		double deltaFr = frInches - lastFrInches;
		double deltaBr = brInches - lastBrInches;
		double deltaFl = flInches - lastFlInches;
		double deltaBl = blInches - lastBlInches;

		// Calculate robot moveTo components
		double deltaForward = (deltaFl + deltaFr + deltaBl + deltaBr) / 4.0;
		double deltaRight = (deltaFl - deltaFr - deltaBl + deltaBr) / 4.0;
		double deltaRotation = (deltaFl - deltaFr + deltaBl - deltaBr) / (4.0 * Constants.Robot.ROBOT_WIDTH_IN);

		// Convert to global coordinates based on current heading
		double currentHeading = currentPose.getHeading(AngleUnit.RADIANS);
		double deltaX = deltaRight * Math.cos(currentHeading) - deltaForward * Math.sin(currentHeading);
		double deltaY = deltaForward * Math.cos(currentHeading) + deltaRight * Math.sin(currentHeading);

		// Update pose
		Pose deltaPose = new Pose(deltaY, deltaX, deltaRotation, AngleUnit.RADIANS);
		this.currentPose = this.currentPose.add(deltaPose);

		// Update last tick values
		this.lastFrInches = frInches;
		this.lastBrInches = brInches;
		this.lastFlInches = flInches;
		this.lastBlInches = blInches;
	}

	public void resetPose() {
		this.currentPose = new Pose(0, 0, 0, AngleUnit.RADIANS);
	}

	public Pose getCurrentPose() {
		return currentPose;
	}
}
