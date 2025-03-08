package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import intothedeep.SnowballConfig;
import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.ArmRotationCapabilities;
import intothedeep.capabilities.ClawCapabilities;
import intothedeep.capabilities.CraneCapabilities;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import t10.auto.MoveToAction;
import t10.bootstrap.BootstrappedOpMode;
import t10.gamepad.GController;
import t10.geometry.Pose;
import t10.localizer.Localizer;
import t10.metrics.Metric;
import t10.motion.mecanum.MecanumDriver;

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
	private double rotationProportion = 1;

	@Metric
	public Pose pose;
	private Telemetry.Item t_armRotation;
	private Telemetry.Item t_armExtension;
	private Telemetry.Item ups;
	@Metric
	private Telemetry.Item posY;
	@Metric
	private Telemetry.Item posX;
	@Metric
	private Telemetry.Item posH;
	@Metric
	private Telemetry.Item fr_voltage;
	@Metric
	private Telemetry.Item crane_l_voltage;
	@Metric
	private Telemetry.Item crane_r_voltage;
	@Metric
	private Telemetry.Item arm_rot_voltage;
	@Metric
	private Telemetry.Item arm_ext_voltage;

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
				.rightBumper.onPress(() -> this.claw.setRotation(config.clawRotate.getPosition() + 0.1)).ok()
				.leftBumper.onPress(() -> this.claw.setRotation(config.clawRotate.getPosition() - 0.1)).ok();

		// G2 controls the intake/outtake
		this.g2 = new GController(this.gamepad2)
				.rightTrigger.whileDown(proportion -> this.armExtension.setPowerManually(proportion)).onRelease(() -> this.armExtension.setPowerManually(0)).ok()
				.rightBumper.onPress(() -> this.armExtension.setTargetPosition((int) (0.75 * ArmExtensionCapabilities.POSITION_FULLY_EXTENDED))).ok()
				.leftTrigger.whileDown(proportion -> this.armExtension.setPowerManually(-proportion)).onRelease(() -> this.armExtension.setPowerManually(0)).ok()
				.leftBumper.onPress(() -> {
					this.armExtension.setTargetPosition(0);
					this.claw.setPreset(ClawCapabilities.ClawPreset.DOWN, true);
				}).ok()
				.rightJoystick.onMove((x, y) -> this.crane.setPowerManually(y)).ok()
				.leftJoystick.onMove((x, y) -> this.armRotation.setPowerManually(-y * rotationProportion)).ok()
				.a.onPress(() -> {
					claw.setPreset(ClawCapabilities.ClawPreset.DOWN,true);
					armExtension.setTargetPosition(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED);
					armRotation.setTargetPosition(0);
					claw.setOpen(true);
				}).ok()
				.b.onPress(() -> {
					armExtension.setTargetPosition(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED);
					armRotation.setTargetPosition(445);
					claw.setPreset(ClawCapabilities.ClawPreset.FORWARD, false, true);
				}).ok()
				.x.onPress(() -> this.claw.toggleClaw()).ok()
				.dpadUp.onPress(() -> {
							armRotation.setTargetPosition(887);
							armExtension.setTargetPosition(ArmExtensionCapabilities.POSITION_FULLY_EXTENDED + 10);
							claw.setPreset(ClawCapabilities.ClawPreset.FORWARD, false, true);
				}).ok()
				.dpadDown.onPress(() -> {
					claw.setPreset(ClawCapabilities.ClawPreset.DOWN, false, true);
					armExtension.setTargetPosition(ArmExtensionCapabilities.POSITION_FULLY_EXTENDED + 200);
				}).ok()
				.dpadRight.onToggleOn(() -> rotationProportion = 0.25).onToggleOff(() -> rotationProportion = 1).ok()
		/*Todo: add basket presets for d-pad*/;

		this.claw.setPreset(ClawCapabilities.ClawPreset.FORWARD, true);
		this.t_armRotation = this.telemetry.addData("armRotation", "");
		this.t_armExtension = this.telemetry.addData("armExtension", "");
		this.ups = this.telemetry.addData("ups", 0);
		this.posY = this.telemetry.addData("y", 0);
		this.posX = this.telemetry.addData("x", 0);
		this.posH = this.telemetry.addData("rotation", 0);
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
		this.posX.setValue(pose.getX());
		this.posY.setValue(pose.getY());
		this.posH.setValue(pose.getHeading(AngleUnit.DEGREES));



		this.g1.loop();
		this.g2.loop();
		this.telemetry.update();
		this.armRotation.loop();
		this.armExtension.loop();
		this.crane.loop();
		this.claw.loop();
	}
}
