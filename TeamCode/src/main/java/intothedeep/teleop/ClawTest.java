package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
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

    @Override
    public void init() {
        super.init();

        this.config = new ClawTest.Config(hardwareMap);
        this.claw = new ClawCapabilities(this.config);
        this.g = new GController(this.gamepad1)
                .a.onToggle(state -> this.claw.setOpen(state)).ok()
                .x.onPress(() -> this.claw.setPreset(ClawPreset.COLLECT_SPECIMEN_FROM_WALL)).ok()
                .b.onPress(() -> this.claw.setPreset(ClawPreset.PLACE_SPECIMEN)).ok()
                .y.onPress(() -> this.claw.setPreset(ClawPreset.STRAIGHT_FORWARD)).ok();
    }

    @Override
    public void loop() {
        this.g.loop();
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
