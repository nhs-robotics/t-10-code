package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import intothedeep.SnowballConfig;
import intothedeep.capabilities.ClawCapabilities;
import intothedeep.capabilities.ClawPreset;
import t10.bootstrap.AbstractRobotConfiguration;
import t10.bootstrap.BootstrappedOpMode;
import t10.bootstrap.Hardware;
import t10.gamepad.GController;
import t10.localizer.Localizer;
import t10.motion.mecanum.MecanumDriver;

@TeleOp
public class ClawTest extends BootstrappedOpMode {
    private ClawTest.Config config;
    private ClawCapabilities claw;
    private GController g;
	private Telemetry.Item rotatePosition;
	private double position = 0;

    @Override
    public void init() {
        super.init();

        this.config = new ClawTest.Config(hardwareMap);
        this.claw = new ClawCapabilities(this.config);
        this.g = new GController(this.gamepad1)
                .a.onToggle(state -> this.claw.setOpen(state)).ok()
                .x.onPress(() -> this.claw.setPreset(ClawPreset.UP)).ok()
                .b.onPress(() -> this.claw.setPreset(ClawPreset.FORWARD)).ok()
                .y.onPress(() -> this.claw.setPreset(ClawPreset.DOWN)).ok()
				.dpadUp.onPress(() -> position += 0.05).ok()
				.dpadDown.onPress(() -> position -= 0.05).ok()
				.dpadRight.onPress(() -> this.config.clawRotate.setPosition(position)).ok()
				.dpadLeft.onPress(() -> this.config.clawTwist.setPosition(position)).ok()
				.rightBumper.onPress(() -> position += 0.01).ok()
				.leftBumper.onPress(() -> position -= 0.01).ok();

		rotatePosition = this.telemetry.addData("Position ", 0);
    }

    @Override
    public void loop() {
        this.g.loop();
		rotatePosition.setValue(position);
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
