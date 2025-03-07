package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import intothedeep.SnowballConfig;
import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.ArmRotationCapabilities;
import intothedeep.capabilities.ClawCapabilities;
import intothedeep.capabilities.CraneCapabilities;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.bootstrap.BootstrappedOpMode;
import t10.gamepad.GController;
import t10.geometry.MovementVector;
import t10.geometry.Pose;
import t10.localizer.Localizer;
import t10.metrics.Metric;
import t10.motion.mecanum.MecanumDriver;
import t10.utils.MathUtils;
import t10.vision.SampleAlignmentProcessor;

@TeleOp
public class VisionTeleOp extends BootstrappedOpMode {
	private SnowballConfig config;
	private CraneCapabilities crane;
	private GController g1;
	private GController g2;
	private MecanumDriver driver;
	private ClawCapabilities claw;
	private ArmExtensionCapabilities armExtension;
	private ArmRotationCapabilities armRotation;
	private Localizer<Pose> localizer;

	@Metric
	public Pose pose;
	private SampleAlignmentProcessor sampleAlignmentProcessor;

	@Override
	public void init() {
		super.init();

		this.config = new SnowballConfig(this.hardwareMap);

		// Robot Capabilities
		this.crane = new CraneCapabilities(this.config);
		this.armExtension = new ArmExtensionCapabilities(this.config);
		this.armRotation = new ArmRotationCapabilities(this.config);
		this.claw = new ClawCapabilities(this.config);

		// Driving
		this.driver = this.config.createMecanumDriver();
		this.localizer = this.config.createLocalizer();

		// Gamepad
		// G1 controls the robot's moveTo.
		this.g1 = new GController(this.gamepad1)
				.x.initialToggleState(true).ok()
				.y.onPress(() -> this.claw.setPreset(ClawCapabilities.ClawPreset.UP, false)).ok()
				.b.onPress(() -> this.claw.setPreset(ClawCapabilities.ClawPreset.FORWARD, false)).ok()
				.a.onPress(() -> this.claw.setPreset(ClawCapabilities.ClawPreset.DOWN, false)).ok();

		// G2 controls the intake/outtake
		this.g2 = new GController(this.gamepad2)
				.rightTrigger.whileDown(proportion -> this.armExtension.setPowerManually(-proportion)).onRelease(() -> this.armExtension.setPowerManually(0)).ok()
				.rightBumper.onPress(() -> this.armExtension.setTargetPosition((int) (0.75 * ArmExtensionCapabilities.POSITION_FULLY_EXTENDED))).ok()
				.leftTrigger.whileDown(proportion -> this.armExtension.setPowerManually(proportion)).onRelease(() -> this.armExtension.setPowerManually(0)).ok()
				.leftBumper.onPress(() -> this.armExtension.setTargetPosition(0)).ok()
				.rightJoystick.onMove((x, y) -> this.crane.setPowerManually(-y)).ok()
				.leftJoystick.onMove((x, y) -> this.armRotation.setPowerManually(-y)).ok()
				.b.onPress(() -> crane.setTargetPosition(CraneCapabilities.POSITION_HIGH_BASKET)).ok()
				.x.onPress(() -> {
					if (Math.abs(armExtension.getPosition() - 0) < ArmExtensionCapabilities.MAX_ERROR_ALLOWED) {
						crane.setTargetPosition(CraneCapabilities.POSITION_BOTTOM);
					}
				}).ok()
				.a.onPress(() -> this.claw.toggleClaw()).ok()
				.y.onPress(() -> {
					crane.setTargetPosition(CraneCapabilities.POSITION_HIGH_CHAMBER);
					armRotation.setTargetPosition(0);
				}).ok();

		this.claw.setPreset(ClawCapabilities.ClawPreset.FORWARD, false);
		this.claw.setOpen(true);
		this.sampleAlignmentProcessor = new SampleAlignmentProcessor(SampleAlignmentProcessor.SampleColor.BLUE);
		this.config.webcam.start(this.sampleAlignmentProcessor);
		this.metrics.streamWebcam(this.config.webcam);
	}

	@Override
	public void loop() {
		if (!gamepad2.dpad_up && !gamepad2.dpad_down && !gamepad2.dpad_right && !gamepad2.dpad_left) {
			this.driver.useGamepad(this.gamepad1, this.g1.x.isToggled() ? 1 : 0.25);
		}

		this.pose = this.localizer.getFieldCentric();

		if (this.gamepad1.left_bumper) {
			double center = this.sampleAlignmentProcessor.detectedSpecimen.x + this.sampleAlignmentProcessor.detectedSpecimen.width;
			double difference = center - SampleAlignmentProcessor.CENTER_X_POSITION;
			double vh = 20 / (1 + Math.pow(1.5 * Math.E, -(90 - this.pose.getHeading(AngleUnit.RADIANS)))) - (20 / 2);

			this.driver.setVelocity(new MovementVector(
					0, difference * -0.15, vh, AngleUnit.DEGREES
			));
		}

		this.g1.loop();
		this.g2.loop();
		this.telemetry.update();
		this.armRotation.loop();
		this.armExtension.loop();
		this.crane.loop();
		this.claw.loop();
		this.localizer.loop();
	}
}
