package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import intothedeep.SnowballConfiguration;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.novel.mecanum.MecanumDriver;
import t10.novel.odometry.NovelOdometry;

@TeleOp
public class SnowballTeleop extends TeleOpOpMode {
    private MecanumDriver driver;
    private GController controller1;
    private GController controller2;
    private SnowballConfiguration c;
    private NovelOdometry odometry;
    private Telemetry.Item x;
    private Telemetry.Item y;
    private Telemetry.Item r;
    private Telemetry.Item vert, hor;

    @Override
    public void initialize() {
        this.c = new SnowballConfiguration(this.hardwareMap);
        this.driver = this.c.createMecanumDriver();
        this.controller1 = new GController(this.gamepad1)
                .x.initialToggleState(true).ok(); // micro-movement
        this.controller2 = new GController(this.gamepad2);
        this.odometry = c.createOdometry();

    }

    @Override
    public void loop() {
        update();
    }

    private void update()
    {
        this.controller1.update();
        this.controller2.update();
        this.driver.useGamepad(this.gamepad1, this.controller1.x.isToggled() ? 4 : 1);
        this.telemetry.update();
        this.odometry.update();
    }
}
