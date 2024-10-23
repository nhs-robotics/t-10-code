package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import intothedeep.Constants;
import intothedeep.KevinRobotConfiguration;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.novel.mecanum.MecanumDriver;
import t10.novel.odometry.NaiveOdometry;
import t10.novel.odometry.NovelOdometry;
import t10.novel.odometry.OdometryNavigation;
import t10.reconstructor.Pose;

@TeleOp
public class OdometryTestingTeleOp extends TeleOpOpMode {
    private MecanumDriver driver;
    private GController gamepadController;
    private KevinRobotConfiguration c;
    private NovelOdometry odometry;
    private OdometryNavigation navigator;
    private Telemetry.Item x;
    private Telemetry.Item y;
    private Telemetry.Item r;
    private Telemetry.Item v_correct;
    private double distance;
    private Pose init_pose;

    @Override
    public void initialize() {
        this.c = new KevinRobotConfiguration(this.hardwareMap);
        this.driver = this.c.createMecanumDriver();
        this.gamepadController = new GController(this.gamepad1)
                .x.initialToggleState(false).ok();  // micro-movement
        this.gamepadController = new GController(this.gamepad1)
                .a.initialToggleState(false).ok();
        this.gamepadController = new GController(this.gamepad1)
                .b.initialToggleState(false).ok();

        this.odometry = c.createOdometry();
        this.navigator = new OdometryNavigation(odometry, driver);

        //this.telemetry.setNumDecimalPlaces(0, 4);
        this.x = this.telemetry.addData("x_novel: ", "0");
        this.y = this.telemetry.addData("y_novel: ", "0");
        this.r = this.telemetry.addData("r_novel: ", "0");
        this.v_correct = this.telemetry.addData("vector", "0, 0, 0");

        distance = 20;
        init_pose = odometry.getRelativePose();
/*
        this.leftWheel = this.telemetry.addData("Left Wheel: ", "0");
        this.rightWheel = this.telemetry.addData("Right Wheel: ", "0");
        this.perpWheel = this.telemetry.addData("Perp Wheel: ", "0");

 */
    }

    @Override
    public void loop() {
        this.gamepadController.update();
        //this.driver.useGamepad(this.gamepad1, this.gamepadController.x.isToggled() ? 4 : 1);
        this.x.setValue(this.odometry.getRelativePose().getX());
        this.y.setValue(this.odometry.getRelativePose().getY());
        this.r.setValue(this.odometry.getRelativePose().getNegativeHeading(AngleUnit.DEGREES));
        this.v_correct.setValue(this.navigator.calcAdjust(init_pose, odometry.getRelativePose()));

        if (gamepadController.x.isToggled()) {
            driveForward();
        }
        if (gamepadController.a.isToggled()) {
            driveRight();
        }
        if (gamepadController.b.isToggled()) {
            navigator.driveSmart(init_pose);
        }
        else if (gamepadController.y.isToggled()) {
            turnRight();
        }
        else if (gamepadController.rightBumper.isToggled()) {
            driveForwardAbsolute();
        }
        else if (gamepadController.leftBumper.isToggled()) {
            driveRightAbsolute();
        }

        this.telemetry.update();
        this.odometry.update();
    }


    private void driveRight() {
        if (Math.abs(distance - odometry.getRelativePose().getX()) > 2) {
            driver.setVelocity(new Vector3D(0, 10, 0));
        } else {
            driver.setVelocity(new Vector3D(0, 0, 0));
        }
    }

    private void driveForward() {
        if (Math.abs(distance - odometry.getRelativePose().getY()) > 2) {
            driver.setVelocity(new Vector3D(-10, 0, 0));
        } else {
            driver.setVelocity(new Vector3D(0, 0, 0));
        }
    }

    private void turnRight() {
        if (Math.abs(90 - odometry.getRelativePose().getNegativeHeading(AngleUnit.DEGREES)) > 5) {
            driver.setVelocity(new Vector3D(0, 0, 5));
        } else {
            driver.setVelocity(new Vector3D(0, 0, 0));
        }
    }
    private void driveForwardAbsolute() {
        if (Math.abs(distance - odometry.getRelativePose().getY()) > 2) {
            driver.setVelocity(odometry.getRelativeVelocity(new Vector3D(-10, 0, 0)));
        } else {
            driver.setVelocity(new Vector3D(0, 0, 0));
        }
    }
    private void driveRightAbsolute() {
        if (Math.abs(distance - odometry.getRelativePose().getX()) > 2) {
            driver.setVelocity(odometry.getRelativeVelocity(new Vector3D(0, 10, 0)));
        } else {
            driver.setVelocity(new Vector3D(0, 0, 0));
        }
    }
}