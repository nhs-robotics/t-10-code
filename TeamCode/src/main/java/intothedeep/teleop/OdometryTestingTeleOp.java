package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import intothedeep.IntoTheDeepRobotConfiguration;
import intothedeep.KevinRobotConfiguration;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.novel.mecanum.MecanumDriver;
import t10.novel.odometry.NovelOdometry;
import t10.novel.odometry.OdometryNavigation;
import t10.reconstructor.Pose;
import t10.utils.MovementVector;

@TeleOp
public class OdometryTestingTeleOp extends TeleOpOpMode {
    private MecanumDriver driver;
    private GController gamepadController;
    private IntoTheDeepRobotConfiguration c;
    private NovelOdometry odometry;
    private OdometryNavigation navigator;
    private Telemetry.Item x;
    private Telemetry.Item y;
    private Telemetry.Item r;
    private Telemetry.Item vert, hor, direct270, direct0, direct45, direct225;
    private Telemetry.Item angle;
    private double distance;
    private Pose init_pose;
    MovementVector vector;

    @Override
    public void initialize() {
        this.c = new IntoTheDeepRobotConfiguration(this.hardwareMap);
        this.driver = this.c.createMecanumDriver();
        this.gamepadController = new GController(this.gamepad1)
                .x.initialToggleState(false).ok();  // micro-movement
        this.gamepadController = new GController(this.gamepad1)
                .a.initialToggleState(false).ok();
        this.gamepadController = new GController(this.gamepad1)
                .b.initialToggleState(false).ok();

        this.odometry = c.createOdometry();
        this.navigator = new OdometryNavigation(odometry, driver);

        this.x = this.telemetry.addData("x_novel: ", "0");
        this.y = this.telemetry.addData("y_novel: ", "0");
        this.r = this.telemetry.addData("r_novel: ", "0");
        this.vert = this.telemetry.addData("vert: ", "0");
        this.hor = this.telemetry.addData("hor: ", "0");

        distance = 10;
        init_pose = odometry.getRelativePose();
        c.fl.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        c.fr.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        c.bl.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        c.br.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    @Override
    public void loop() {
        this.gamepadController.update();
        this.x.setValue(this.odometry.getRelativePose().getX());
        this.y.setValue(this.odometry.getRelativePose().getY());
        this.r.setValue(this.odometry.getRelativePose().getHeading(AngleUnit.DEGREES));
        this.vert.setValue(this.odometry.getRelativeVelocity(1,0));
        this.hor.setValue(this.odometry.getRelativeVelocity(0,1));

        gamepadController.x.onPress(() -> navigator.driveDiagonal(-distance,distance));
        gamepadController.a.onPress(() -> navigator.driveDiagonal(-distance,-distance));
        gamepadController.y.onPress(() -> navigator.driveDiagonal(distance,distance));
        gamepadController.b.onPress(() -> navigator.driveDiagonal(distance,-distance));


        gamepadController.dpadLeft.onPress(() -> navigator.driveHorizontal(-distance));
        gamepadController.dpadUp.onPress(() -> navigator.driveLateral(distance));
        gamepadController.dpadRight.onPress(() -> navigator.driveHorizontal(distance));
        gamepadController.dpadDown.onPress(() -> navigator.driveLateral(-distance));

        gamepadController.rightBumper.onPress(() -> navigator.turnRelative(45));
        gamepadController.leftBumper.onPress(() -> navigator.turnRelative(-45));

        this.telemetry.update();
        this.odometry.update();
    }

}