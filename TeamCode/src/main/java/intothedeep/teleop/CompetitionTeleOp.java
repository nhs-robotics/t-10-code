package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import intothedeep.CraneCapabilities;
import intothedeep.SnowballConfig;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.motion.mecanum.MecanumDriver;

@TeleOp
public class CompetitionTeleOp extends TeleOpOpMode {
    private SnowballConfig config;
    private CraneCapabilities crane;
    private GController g2;
    private MecanumDriver driver;

    @Override
    public void initialize() {
        this.config = new SnowballConfig(this.hardwareMap);
        this.crane = new CraneCapabilities(this.config);
        this.g2 = new GController(this.gamepad2)
                .dpadUp.onPress(() -> this.crane.extendArm(1)).onRelease(() -> this.crane.extendArm(0)).ok()
                .dpadDown.onPress(() -> this.crane.extendArm(-1)).onRelease(() -> this.crane.extendArm(0)).ok();
        this.driver = this.config.createMecanumDriver();
    }

    @Override
    public void loop() {
        if (Math.abs(this.gamepad2.left_stick_y) < 0.1) {
            this.crane.runRotation(0);
        } else {
            this.crane.runRotation(this.gamepad2.left_stick_y);
        }

        if (Math.abs(this.gamepad2.right_stick_y) < 0.1) {
            this.crane.runCrane(0);
        } else {
            this.crane.runCrane(this.gamepad2.right_stick_y);
        }

        this.driver.useGamepad(this.gamepad1, 1);
        this.g2.update();
        this.crane.update();
    }
}
