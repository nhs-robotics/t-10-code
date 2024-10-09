package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import intothedeep.KevinRobotConfiguration;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.novel.mecanum.MecanumDriver;
import t10.novel.odometry.NovelOdometry;

@TeleOp
public class OdometryTestingTeleOp extends TeleOpOpMode {
    private MecanumDriver driver;
    private GController gamepadController;
    private KevinRobotConfiguration c;
    private NovelOdometry odometry;
    private Telemetry.Item x;
    private Telemetry.Item y;
    private Telemetry.Item r;

    @Override
    public void initialize() {
        this.c = new KevinRobotConfiguration(this.hardwareMap);
        this.driver = this.c.createMecanumDriver();
        this.gamepadController = new GController(this.gamepad1)
                .x.initialToggleState(true).ok();  // micro-movement

        this.odometry = c.createOdometry();

        //this.telemetry.setNumDecimalPlaces(0, 4);
        this.x = this.telemetry.addData("x", "0");
        this.y = this.telemetry.addData("y", "0");
        this.r = this.telemetry.addData("r", "0");

    }

    @Override
    public void loop() {
        this.gamepadController.update();
        this.driver.useGamepad(this.gamepad1, this.gamepadController.x.isToggled() ? 4 : 1);
        this.x.setValue(this.odometry.getRelativePose().getX());
        this.y.setValue(this.odometry.getRelativePose().getX());
        this.r.setValue(this.odometry.getRelativePose().getHeading(AngleUnit.DEGREES));

        this.telemetry.update();
        this.odometry.update();
    }
}
