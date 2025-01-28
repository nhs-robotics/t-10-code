package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import intothedeep.SnowballConfig;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.bootstrap.TeleOpOpMode;
import t10.geometry.MovementVector;
import t10.geometry.Pose;
import t10.localizer.odometry.OdometryLocalizer;
import t10.motion.mecanum.MecanumDriver;

@TeleOp
public class HealthCheckTeleOp extends TeleOpOpMode {
	private SnowballConfig config;
	private MecanumDriver driver;
	private OdometryLocalizer odometry;
	private Telemetry.Item cFL;
	private Telemetry.Item cFR;
	private Telemetry.Item cBL;
	private Telemetry.Item cBR;
	private Telemetry.Item cStraightMovementVF;
	private Telemetry.Item cStraightMovementVB;
	private Telemetry.Item cStraightMovementHR;
	private Telemetry.Item cStraightMovementHL;
	private Telemetry.Item cRotationCW;
	private Telemetry.Item cRotationCCW;

	@Override
	public void initialize() {
		this.config = new SnowballConfig(this.hardwareMap);
		this.driver = this.config.createMecanumDriver();
		this.odometry = this.config.createOdometry();
		this.cFL = this.telemetry.addData("FL Encoder (<0.5in)						", "---");
		this.cFR = this.telemetry.addData("FR Encoder (<0.5in)						", "---");
		this.cBL = this.telemetry.addData("BL Encoder (<0.5in)						", "---");
		this.cBR = this.telemetry.addData("BR Encoder (<0.5in)						", "---");
		this.cStraightMovementVF = this.telemetry.addData("Straight Movement VF (<0.1in, <1deg)		", "---");
		this.cStraightMovementVB = this.telemetry.addData("Straight Movement VB (<0.1in, <1deg)		", "---");
		this.cStraightMovementHR = this.telemetry.addData("Straight Movement HR (<0.1in, <1deg)		", "---");
		this.cStraightMovementHL = this.telemetry.addData("Straight Movement HL (<0.1in, <1deg)		", "---");
		this.cRotationCW = this.telemetry.addData("Rotation CW (<2deg)						", "---");
		this.cRotationCCW = this.telemetry.addData("Rotation CCW (<2deg)						", "---");
	}

	public Pose runStraightMovementTest(MovementVector vector) {
		Pose initialPose = this.odometry.getFieldCentricPose();

		this.driver.setVelocity(vector);

		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		this.driver.halt();

		Pose finalPose = this.odometry.getFieldCentricPose();
		return finalPose.subtract(initialPose);
	}

	public String pf(boolean bool) {
		return bool ? "PASS" : "FAIL";
	}

	@Override
	public void start() {
		// Vertical Forward pose difference. Expected movement is 10in forward, 0in horizontal, 0deg rotation.
		Pose vfDifference = runStraightMovementTest(new MovementVector(10, 0, 0, AngleUnit.DEGREES));

		// Calculate test results.
		this.cFL.setValue(pf(Math.abs(this.driver.frontLeft.encoder.getCurrentInches() - 10) < 0.5));
		this.cFR.setValue(pf(Math.abs(this.driver.frontRight.encoder.getCurrentInches() - 10) < 0.5));
		this.cBL.setValue(pf(Math.abs(this.driver.backLeft.encoder.getCurrentInches() - 10) < 0.5));
		this.cBR.setValue(pf(Math.abs(this.driver.backRight.encoder.getCurrentInches() - 10) < 0.5));
		this.cStraightMovementVF.setValue(
				pf(vfDifference.getY() > 0 && Math.abs(vfDifference.getY() - 10) < 0.1 && Math.abs(vfDifference.getX()) < 0.1 && Math.abs(vfDifference.getHeading(AngleUnit.DEGREES)) < 1)
		);

		// Vertical Backward pose difference. Expected movement is 10in backward, 0in horizontal, 0deg rotation.
		Pose vbDifference = runStraightMovementTest(new MovementVector(-10, 0, 0, AngleUnit.DEGREES));

		this.cStraightMovementVB.setValue(
				pf(vbDifference.getY() < 0 && Math.abs(vbDifference.getY() - 10) < 0.1 && Math.abs(vbDifference.getX()) < 0.1 && Math.abs(vbDifference.getHeading(AngleUnit.DEGREES)) < 1)
		);

		// Horizontal Right pose difference. Expected movement is 0in vertical, 10in right, 0deg rotation.
		Pose hrDifference = runStraightMovementTest(new MovementVector(0, 10, 0, AngleUnit.DEGREES));

		this.cStraightMovementHR.setValue(
				pf(hrDifference.getX() > 0 && Math.abs(hrDifference.getY()) < 0.1 && Math.abs(hrDifference.getX() - 10) < 0.1 && Math.abs(hrDifference.getHeading(AngleUnit.DEGREES)) < 1)
		);

		// Horizontal Left pose difference. Expected movement is 0in vertical, 10in left, 0deg rotation.
		Pose hlDifference = runStraightMovementTest(new MovementVector(0, -10, 0, AngleUnit.DEGREES));

		this.cStraightMovementHL.setValue(
				pf(hlDifference.getX() < 0 && Math.abs(hlDifference.getY()) < 0.1 && Math.abs(hlDifference.getX() - 10) < 0.1 && Math.abs(hlDifference.getHeading(AngleUnit.DEGREES)) < 1)
		);

		// Rotation CW, expected 10 deg.
		Pose rcwDifference = runStraightMovementTest(new MovementVector(0, 0, 10, AngleUnit.DEGREES));

		this.cRotationCW.setValue(
				pf(rcwDifference.getHeading(AngleUnit.DEGREES) > 0 && Math.abs(rcwDifference.getX()) < 0.1 && Math.abs(rcwDifference.getY()) < 0.1 && Math.abs(rcwDifference.getHeading(AngleUnit.DEGREES) - 10) < 2)
		);

		// Rotation CCW, expected -10 deg.
		Pose rccwDifference = runStraightMovementTest(new MovementVector(0, 0, -10, AngleUnit.DEGREES));

		this.cRotationCCW.setValue(
				pf(rccwDifference.getHeading(AngleUnit.DEGREES) < 0 && Math.abs(rccwDifference.getX()) < 0.1 && Math.abs(rccwDifference.getY()) < 0.1 && Math.abs(rccwDifference.getHeading(AngleUnit.DEGREES) - 10) < 2)
		);
	}

	@Override
	public void loop() {
		this.odometry.update();
	}
}
