package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import intothedeep.SnowballConfig;
import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.ArmRotationCapabilities;
import intothedeep.capabilities.ClawCapabilities;
import intothedeep.capabilities.CraneCapabilities;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import t10.auto.MoveToAction;
import t10.bootstrap.BootstrappedOpMode;
import t10.gamepad.GController;
import t10.geometry.MovementVector;
import t10.geometry.Pose;
import t10.localizer.Localizer;
import t10.metrics.Metric;
import t10.motion.mecanum.MecanumDriver;

import java.util.ArrayList;
import java.util.List;

@TeleOp
public class CompetitionTeleOp extends BootstrappedOpMode {
	private SnowballConfig config;
	private CraneCapabilities crane;
	private GController g1;
	private GController g2;
	private MecanumDriver driver;
	private ClawCapabilities claw;
	private ArmExtensionCapabilities armExtension;
	private ArmRotationCapabilities armRotation;
	private Localizer<Pose> localizer;
	private long updates;
	private long startUpdates;

	@Metric
	public Pose pose;
	private Telemetry.Item t_armRotation;
	private Telemetry.Item t_armExtension;
	private Telemetry.Item ups;

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
		this.localizer.setFieldCentric(new Pose(0, 64, -90, AngleUnit.DEGREES));

		// Gamepad
		// G1 controls the robot's moveTo.
		this.g1 = new GController(this.gamepad1)
				.x.initialToggleState(true).ok()
				.dpadRight.onPress(() -> claw.setAbsoluteMode(false)).ok()
				.y.onPress(() -> this.claw.setPreset(ClawCapabilities.ClawPreset.UP, true)).ok()
				.b.onPress(() -> this.claw.setPreset(ClawCapabilities.ClawPreset.FORWARD, false)).ok()
				.a.onPress(() -> this.claw.setPreset(ClawCapabilities.ClawPreset.DOWN, false)).ok()
				.rightBumper.onPress(() -> this.claw.setTwist(config.clawTwist.getPosition() + 0.1)).ok()
				.leftBumper.onPress(() -> this.claw.setTwist(config.clawTwist.getPosition() - 0.1)).ok();

		// G2 controls the intake/outtake
		this.g2 = new GController(this.gamepad2)
				.rightTrigger.whileDown(proportion -> this.armExtension.setPowerManually(proportion)).onRelease(() -> this.armExtension.setPowerManually(0)).ok()
				.rightBumper.onPress(() -> this.armExtension.setTargetPosition((int) (ArmExtensionCapabilities.POSITION_FULLY_EXTENDED))).ok()
				.leftTrigger.whileDown(proportion -> this.armExtension.setPowerManually(-proportion)).onRelease(() -> this.armExtension.setPowerManually(0)).ok()
				.leftBumper.onPress(() -> this.armExtension.setTargetPosition(0)).ok()
				// todo: check inversion
				.rightJoystick.onMove((x, y) -> this.crane.setPowerManually(-y)).ok()
				.leftJoystick.onMove((x, y) -> this.armRotation.setPowerManually(-y)).ok()
				.b.onPress(() -> {
					armExtension.setTargetPosition(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED);
					armRotation.setTargetPosition(400);
					claw.setPreset(ClawCapabilities.ClawPreset.FORWARD, true);
					claw.setOpen(true);
				}).ok()
				.a.onPress(() -> this.claw.toggleClaw()).ok()
				.x.onPress(() -> {
					armRotation.setTargetPosition(628);
					armExtension.setTargetPosition(-1400);
				}).ok()
				/*Todo: add basket presets for d-pad*/;

		this.claw.setPreset(ClawCapabilities.ClawPreset.UP, true);
		this.t_armRotation = this.telemetry.addData("armRotation", "");
		this.t_armExtension = this.telemetry.addData("armExtension", "");
		this.ups = this.telemetry.addData("ups", 0);
	}

	@Override
	public void loop() {
		super.loop();

		if (updates == 0) {
			startUpdates = System.currentTimeMillis() / 1000L;
		}

		updates++;

		if (!gamepad2.dpad_up && !gamepad2.dpad_down && !gamepad2.dpad_right && !gamepad2.dpad_left) {
			this.driver.useGamepad(this.gamepad1, this.g1.x.isToggled() ? 1 : 0.25);
		}

		this.localizer.loop();
		this.pose = this.localizer.getFieldCentric();
		this.t_armRotation.setValue(this.armRotation.getPosition());
		this.t_armExtension.setValue(this.armExtension.getPosition());
		this.ups.setValue(updates / ((System.currentTimeMillis() / 1000L + 1) - startUpdates));
		this.g1.loop();
		this.g2.loop();
		this.telemetry.update();
		this.armRotation.loop();
		this.armExtension.loop();
		this.crane.loop();
		this.claw.loop();
	}
}
