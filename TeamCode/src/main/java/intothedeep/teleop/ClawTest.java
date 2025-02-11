package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import intothedeep.SnowballConfig;
import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.ArmRotationCapabilities;
import intothedeep.capabilities.ClawCapabilities;

import intothedeep.capabilities.CraneCapabilities;

import t10.bootstrap.AbstractRobotConfiguration;
import t10.bootstrap.BootstrappedOpMode;
import t10.bootstrap.Hardware;
import t10.gamepad.GController;
import t10.localizer.Localizer;
import t10.motion.mecanum.MecanumDriver;

@TeleOp
public class ClawTest extends BootstrappedOpMode {
    private SnowballConfig config;
    private ClawCapabilities claw;
	private ArmExtensionCapabilities armExtension;
	private ArmRotationCapabilities armRotation;
	private CraneCapabilities crane;
    private GController g, g2;
	private Telemetry.Item rotatePosition, extension, rotation;
	private double position = 0;

    @Override
    public void init() {
        super.init();

        this.config = new SnowballConfig(hardwareMap);
        this.claw = new ClawCapabilities(this.config);
		this.armExtension = new ArmExtensionCapabilities(this.config);
		this.armRotation = new ArmRotationCapabilities(this.config);
		this.crane = new CraneCapabilities(this.config);
        this.g = new GController(this.gamepad1)
                .a.onToggle(state -> this.claw.setOpen(state)).ok()
                .x.onPress(() -> this.claw.setPreset(ClawCapabilities.ClawPreset.UP, true)).ok()
                .b.onPress(() -> this.claw.setPreset(ClawCapabilities.ClawPreset.FORWARD, true)).ok()
                .y.onPress(() -> this.claw.setPreset(ClawCapabilities.ClawPreset.DOWN, true)).ok()
				.dpadUp.onPress(() -> position += 0.05).ok()
				.dpadDown.onPress(() -> position -= 0.05).ok()
				.dpadRight.onPress(() -> this.config.clawRotate.setPosition(position)).ok()
				.dpadLeft.onPress(() -> this.config.clawTwist.setPosition(position)).ok()
				.rightBumper.onPress(() -> position += 0.01).ok()
				.leftBumper.onPress(() -> position -= 0.01).ok();

		this.g2 = new GController(this.gamepad2)
				.rightTrigger.whileDown(proportion -> this.armExtension.setPowerManually(-proportion)).onRelease(() -> this.armExtension.setPowerManually(0)).ok()
				.rightBumper.onPress(() -> this.armExtension.setTargetPosition((int) (0.75 * ArmExtensionCapabilities.POSITION_FULLY_EXTENDED))).ok()
				.leftTrigger.whileDown(proportion -> this.armExtension.setPowerManually(proportion)).onRelease(() -> this.armExtension.setPowerManually(0)).ok()
				.leftBumper.onPress(() -> this.armExtension.setTargetPosition(0)).ok()
				.rightJoystick.onMove((x, y) -> this.crane.setPowerManually(-y)).ok()
				.leftJoystick.onMove((x, y) -> this.armRotation.setPowerManually(-y)).ok()
				.a.onPress(() -> this.claw.toggleClaw()).ok();

		rotatePosition = this.telemetry.addData("Position ", 0);
		extension = this.telemetry.addData("extension: ", 0);
		rotation = this.telemetry.addData("rotation: ",0);
    }

    @Override
    public void loop() {
        this.g.loop();
		this.g2.loop();
		rotatePosition.setValue(position);
		extension.setValue(config.armExtension.motor.getCurrentPosition());
		rotation.setValue(config.armRotation.motor.getCurrentPosition());
		claw.loop(config.armExtension.motor.getCurrentPosition());
		telemetry.update();
    }

    public static class Config extends AbstractRobotConfiguration {
        @Hardware(name = "ClawTwist")
        public Servo clawTwist;

        @Hardware(name = "ClawRotate")
        public Servo clawRotate;

        @Hardware(name = "ClawGrip")
        public Servo clawGrip;

        public Config(HardwareMap hardwareMap) {
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

}
