package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import intothedeep.Constants;
import intothedeep.SamuelRobotConfiguration;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.novel.motion.NovelMecanumDriver;

@TeleOp
public class DefaultTeleOpMode extends TeleOpOpMode {
    private NovelMecanumDriver driver;
    private GController gamepadController;
    private SamuelRobotConfiguration c;

    @Override
    public void initialize() {
        this.c = new SamuelRobotConfiguration(this.hardwareMap);
        this.driver = this.c.createDriver(Constants.Coefficients.PRODUCTION_COEFFICIENTS);
        this.gamepadController = new GController(this.gamepad1)
                .x.initialToggleState(true).ok();  // micro-movement
    }

    @Override
    public void loop() {
        this.gamepadController.update();
        this.driver.useGamepad(this.gamepad1, this.gamepadController.x.isToggled() ? 4 : 1);
        this.telemetry.update();
    }
}
