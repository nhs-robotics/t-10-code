package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import intothedeep.SnowballConfig;
import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.ArmRotationCapabilities;
import intothedeep.capabilities.CraneCapabilities;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.geometry.MovementVector;
import t10.geometry.Pose;
import t10.localizer.Localizer;
import t10.motion.mecanum.MecanumDriver;
import t10.motion.profile.IMotionProfile;
import t10.motion.profile.TrapezoidalMotionProfile;

@TeleOp
public class VeloTestTeleop extends TeleOpOpMode {
	private SnowballConfig config;
	private GController g1;
	private MecanumDriver driver;
	private Telemetry.Item state, horizontal, vertical, angle, veloX, veloY, veloH, Finalhorizontal, Finalvertical, Finalangle, calcVelo;
	private Localizer<Pose> localizer;
	private TrapezoidalMotionProfile motionProfile;
	MovementVector initialVelocity = new MovementVector(0,0,0,AngleUnit.RADIANS), endVelocity = new MovementVector(0,0,0, AngleUnit.RADIANS), maxAccel = new MovementVector(5,5,2 * Math.PI, AngleUnit.RADIANS);
	Pose initialPose = new Pose(0,0,0,AngleUnit.RADIANS), finalPose = new Pose(0,0,0,AngleUnit.RADIANS);

	@Override
	public void init() {
		super.init();

		this.config = new SnowballConfig(this.hardwareMap);


		// Driving
		this.driver = this.config.createMecanumDriver();
		this.localizer = this.config.createLocalizer();
		this.motionProfile = new TrapezoidalMotionProfile();

		// Gamepad
		// G1 controls the robot's moveTo.
		this.g1 = new GController(this.gamepad1)
				.dpadUp.onPress(() -> finalPose = finalPose.add(new Pose(5,0,0,AngleUnit.RADIANS))).ok()
				.dpadDown.onPress(() -> finalPose = finalPose.add(new Pose(-5,0,0,AngleUnit.RADIANS))).ok()
				.dpadLeft.onPress(() -> finalPose = finalPose.add(new Pose(0,-5,0,AngleUnit.RADIANS))).ok()
				.dpadRight.onPress(() -> finalPose = finalPose.add(new Pose(0,5,0,AngleUnit.RADIANS))).ok()
				.rightBumper.onPress(() -> finalPose = finalPose.add(new Pose(0,0,Math.PI / 4, AngleUnit.RADIANS))).ok()
				.leftBumper.onPress(() -> finalPose = finalPose.add(new Pose(0,0,-Math.PI / 4, AngleUnit.RADIANS))).ok()
				.a.onPress(() -> initialPose = localizer.getFieldCentric()).ok()
				.x.whileDown(() -> driver.setVelocity(
						motionProfile.calculate(
								initialVelocity,
								new MovementVector(25,25,2*Math.PI,AngleUnit.RADIANS),
								new MovementVector(3,3,0.125,AngleUnit.RADIANS),
								endVelocity,
								maxAccel,
								initialPose,
								localizer.getFieldCentric(),
								localizer.getVelocity(),
								finalPose,
								3)
				)).onRelease(() -> driver.halt()).ok();

		this.state = telemetry.addData("","");
		this.calcVelo = telemetry.addData("Velocity: ", 0);
		this.vertical = telemetry.addData("y: ", 0);
		this.horizontal = telemetry.addData("x: ", 0);
		this.angle = telemetry.addData("angle: ", 0);
		this.veloY = telemetry.addData("veloY: ", 0);
		this.veloX = telemetry.addData("veloX: ", 0);
		this.veloH = telemetry.addData("veloH: ", 0);
		this.Finalvertical = telemetry.addData("y: ", 0);
		this.Finalhorizontal = telemetry.addData("x: ", 0);
		this.Finalangle = telemetry.addData("angle: ", 0);
	}

	@Override
	public void loop() {
		state.setValue(motionProfile.getState().toString());
		calcVelo.setValue(motionProfile.calculate(
				initialVelocity,
				new MovementVector(25,25,2*Math.PI,AngleUnit.RADIANS),
				new MovementVector(10,10,1,AngleUnit.RADIANS),
				endVelocity,
				maxAccel,
				initialPose,
				localizer.getFieldCentric(),
				localizer.getVelocity(),
				finalPose,
				3).toString());
		finalPose.toString();

		Pose pose = this.localizer.getFieldCentric();
		vertical.setValue(pose.getY());
		horizontal.setValue(pose.getX());
		angle.setValue(pose.getHeading(AngleUnit.DEGREES));
		MovementVector velocity = this.localizer.getVelocity();
		veloY.setValue(velocity.getVertical());
		veloX.setValue(velocity.getHorizontal());
		veloH.setValue(velocity.getRotation());

		Finalvertical.setValue(finalPose.getY());
		Finalhorizontal.setValue(finalPose.getX());
		Finalangle.setValue(finalPose.getHeading(AngleUnit.DEGREES));

		this.g1.loop();
		this.telemetry.update();
		this.localizer.loop();
	}

	private void newMove() {

	}
}
