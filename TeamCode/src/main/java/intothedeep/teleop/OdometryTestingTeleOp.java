package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import intothedeep.KevinRobotConfiguration;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.novel.mecanum.MecanumDriver;
import t10.novel.odometry.NaiveOdometry;
import t10.novel.odometry.NovelOdometry;
import t10.novel.odometry.OdometryNavigation;

@TeleOp
public class OdometryTestingTeleOp extends TeleOpOpMode {
    private MecanumDriver driver;
    private GController gamepadController;
    private KevinRobotConfiguration c;
    private NovelOdometry odometry;
    private NaiveOdometry dumb_odometry;
    private OdometryNavigation navigator;
    private Telemetry.Item x;
    private Telemetry.Item y;
    private Telemetry.Item r;
    private Telemetry.Item x_dumb, y_dumb, r_dumb;
    private double distance;
    private double final_position;

    @Override
    public void initialize() {
        this.c = new KevinRobotConfiguration(this.hardwareMap);
        this.driver = this.c.createMecanumDriver();
        this.gamepadController = new GController(this.gamepad1)
                .x.initialToggleState(true).ok();  // micro-movement

        this.odometry = c.createOdometry();
        this.dumb_odometry = c.createDumbOdometry();
        this.navigator = new OdometryNavigation(odometry,driver);

        //this.telemetry.setNumDecimalPlaces(0, 4);
        this.x = this.telemetry.addData("x_novel: ", "0");
        this.y = this.telemetry.addData("y_novel: ", "0");
        this.r = this.telemetry.addData("r_novel: ", "0");
        this.x_dumb = this.telemetry.addData("x_naive: ", "0");
        this.y_dumb = this.telemetry.addData("y_naive: ", "0");
        this.r_dumb = this.telemetry.addData("r_naive: ", "0");

        distance = 20;
        final_position = odometry.getRelativePose().getY() + distance;
/*
        this.leftWheel = this.telemetry.addData("Left Wheel: ", "0");
        this.rightWheel = this.telemetry.addData("Right Wheel: ", "0");
        this.perpWheel = this.telemetry.addData("Perp Wheel: ", "0");

 */
    }

    @Override
    public void loop() {
        this.gamepadController.update();
        this.driver.useGamepad(this.gamepad1, this.gamepadController.x.isToggled() ? 4 : 1);
        this.x.setValue(this.odometry.getRelativePose().getX());
        this.y.setValue(this.odometry.getRelativePose().getY());
        this.r.setValue(this.odometry.getRelativePose().getHeading(AngleUnit.DEGREES));

        this.x_dumb.setValue(this.dumb_odometry.getRelativePose().getX());
        this.y_dumb.setValue(this.dumb_odometry.getRelativePose().getY());
        this.r_dumb.setValue(this.dumb_odometry.getRelativePose().getHeading(AngleUnit.DEGREES));

        if(Math.abs(final_position - odometry.getRelativePose().getY()) > 2)
        {
            driver.setVelocity(new Vector3D(Math.signum(final_position - odometry.getRelativePose().getY())*10,0,0));
        }
        else {
            driver.setVelocity(new Vector3D(0,0,0));
        }

        this.telemetry.update();
        this.odometry.update();
        this.dumb_odometry.update();
    }
}
