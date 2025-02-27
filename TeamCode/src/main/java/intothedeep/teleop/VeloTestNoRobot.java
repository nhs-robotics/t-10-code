package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.bootstrap.AbstractRobotConfiguration;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.geometry.MovementVector;
import t10.geometry.Pose;
import t10.localizer.Localizer;
import t10.motion.mecanum.MecanumDriver;
import t10.motion.profile.TrapezoidalMotionProfile;
import t10.utils.MathUtils;

@TeleOp
public class VeloTestNoRobot extends TeleOpOpMode {
	private TestConfig config;
	private GController g1;
	private Telemetry.Item state, horizontal, vertical, angle, veloX, veloY, veloH, finalVelo, currentVelo, maxVelo, calcVelo;
	private TrapezoidalMotionProfile motionProfile;
	MovementVector initialVelocity = new MovementVector(0,0,0,AngleUnit.RADIANS), endVelocity = new MovementVector(0,0,0, AngleUnit.RADIANS), maxAccel = new MovementVector(5,5,2 * Math.PI, AngleUnit.RADIANS);
	Pose initialPose = new Pose(0,0,0,AngleUnit.RADIANS), finalPose = new Pose(0,0,0,AngleUnit.RADIANS), currentPose = new Pose(0,0,0,AngleUnit.RADIANS);

	@Override
	public void init() {
		super.init();

		this.config = new TestConfig(this.hardwareMap);


		this.motionProfile = new TrapezoidalMotionProfile();

		// Gamepad
		// G1 controls the robot's moveTo.
		this.g1 = new GController(this.gamepad1)
				.dpadUp.onPress(() -> finalPose = finalPose.add(new Pose(5,0,0,AngleUnit.RADIANS))).ok()
				.dpadDown.onPress(() -> finalPose = finalPose.add(new Pose(-5,0,0,AngleUnit.RADIANS))).ok()
				.dpadLeft.onPress(() -> finalPose = finalPose.add(new Pose(0,-5,0,AngleUnit.RADIANS))).ok()
				.dpadRight.onPress(() -> finalPose = finalPose.add(new Pose(0,5,0,AngleUnit.RADIANS))).ok()
				.y.onPress(() -> currentPose = currentPose.add(new Pose(5,0,0,AngleUnit.RADIANS))).ok()
				.a.onPress(() -> currentPose = currentPose.add(new Pose(-5,0,0,AngleUnit.RADIANS))).ok()
				.x.onPress(() -> currentPose = currentPose.add(new Pose(0,-5,0,AngleUnit.RADIANS))).ok()
				.b.onPress(() -> currentPose = currentPose.add(new Pose(0,5,0,AngleUnit.RADIANS))).ok()
				.rightBumper.onPress(() -> finalPose = finalPose.add(new Pose(0,0,Math.PI / 4, AngleUnit.RADIANS))).ok()
				.leftBumper.onPress(() -> finalPose = finalPose.add(new Pose(0,0,-Math.PI / 4, AngleUnit.RADIANS))).ok();

		this.state = telemetry.addData("","");
		this.calcVelo = telemetry.addData("Velocity: ", 0);
		this.currentVelo = telemetry.addData("current", 0);
		this.finalVelo = telemetry.addData("final", 0);
		this.maxVelo = telemetry.addData("max", 0);
		this.angle = telemetry.addData("calcs",0);
	}

	@Override
	public void loop() {
		String[] veloState = motionProfile.getState();
		state.setValue(veloState[0] + ", " + veloState[1] + ", " + veloState[2] + ", " + veloState[3]);
		MovementVector vector = motionProfile.calculate(
				initialVelocity,
				new MovementVector(25,25,2*Math.PI,AngleUnit.RADIANS),
				new MovementVector(10,10,1,AngleUnit.RADIANS),
				endVelocity,
				maxAccel,
				initialPose,
				currentPose,
				new MovementVector(MathUtils.solveVelocity(0,maxAccel.getVertical(),currentPose.getY()),MathUtils.solveVelocity(0,maxAccel.getHorizontal(),currentPose.getX()),MathUtils.solveVelocity(0,maxAccel.getRotation(),currentPose.getHeading(AngleUnit.RADIANS)),AngleUnit.RADIANS),
				finalPose,
				3);
		calcVelo.setValue(vector.toString());
		angle.setValue(motionProfile.calcs);


		currentVelo.setValue(String.valueOf(currentPose.getY()) + ", " + String.valueOf(currentPose.getX()) + ", " + String.valueOf(currentPose.getHeading(AngleUnit.RADIANS)));
		finalVelo.setValue(String.valueOf(finalPose.getY()) + ", " + String.valueOf(finalPose.getX()) + ", " + String.valueOf(finalPose.getHeading(AngleUnit.RADIANS)));
		maxVelo.setValue(String.valueOf(motionProfile.peakVelocity[0]) + ", " + String.valueOf(motionProfile.peakVelocity[1]) + ", " + String.valueOf(motionProfile.peakVelocity[2]));

		this.g1.loop();
		this.telemetry.update();
	}

	private void newMove() {

	}
}
class TestConfig extends AbstractRobotConfiguration {

	TestConfig(HardwareMap hardwareMap) {
		super(hardwareMap);
	}
	@Override
	public MecanumDriver createMecanumDriver() {
		return null;
	}

	@Override
	public Localizer<?> createLocalizer() {
		return null;
	}
}
