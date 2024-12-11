package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import intothedeep.IntoTheDeepRobotConfiguration;
import intothedeep.KevinRobotConfiguration;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.geometry.MovementVector;
import t10.geometry.Pose;
import t10.localizer.odometry.OdometryLocalizer;
import t10.localizer.odometry.OdometryNavigation;
import t10.motion.mecanum.MecanumDriver;

@TeleOp
public class OdometryTestingTeleOp extends TeleOpOpMode {
    private MecanumDriver driver;
    private GController gamepadController;
    private IntoTheDeepRobotConfiguration c;
    private OdometryLocalizer odometry;
    private OdometryNavigation navigator;
    private Telemetry.Item x;
    private Telemetry.Item y;
    private Telemetry.Item r;
    private Telemetry.Item vert, hor, direct270, direct0, direct90, direct180;
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

        this.direct0 = this.telemetry.addData("0: ", "0");
        this.direct90 = this.telemetry.addData("90: ", "0");
        this.direct180 = this.telemetry.addData("180: ", "0");
        this.direct270 = this.telemetry.addData("-90: ", "0");

        distance = 10;
        init_pose = odometry.getFieldCentricPose();
        c.fl.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        c.fr.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        c.bl.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        c.br.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    @Override
    public void loop() {
        this.gamepadController.update();
        this.x.setValue(this.odometry.getFieldCentricPose().getX());
        this.y.setValue(this.odometry.getFieldCentricPose().getY());
        this.r.setValue(this.odometry.getFieldCentricPose().getHeading(AngleUnit.DEGREES));
        this.vert.setValue(this.odometry.getRobotCentricVelocity(1,0));
        this.hor.setValue(this.odometry.getRobotCentricVelocity(0,1));

        this.direct0.setValue(navigator.findTurnSpeed(odometry.getFieldCentricPose().getHeading(AngleUnit.DEGREES),0));
        this.direct90.setValue(navigator.findTurnSpeed(odometry.getFieldCentricPose().getHeading(AngleUnit.DEGREES),90));
        this.direct180.setValue(navigator.findTurnSpeed(odometry.getFieldCentricPose().getHeading(AngleUnit.DEGREES),180));
        this.direct270.setValue(navigator.findTurnSpeed(odometry.getFieldCentricPose().getHeading(AngleUnit.DEGREES),-90));

        gamepadController.x.onPress(() -> navigator.driveDiagonal(-distance,distance));
        gamepadController.a.onPress(() -> navigator.driveDiagonal(-distance,-distance));
        gamepadController.y.onPress(() -> navigator.driveDiagonal(distance,distance));
        gamepadController.b.onPress(() -> navigator.driveDiagonal(distance,-distance));


        gamepadController.dpadLeft.onPress(() -> navigator.turnAbsolute(-90));
        gamepadController.dpadUp.onPress(() -> navigator.turnAbsolute(0));
        gamepadController.dpadRight.onPress(() -> navigator.turnAbsolute(90));
        gamepadController.dpadDown.onPress(() -> navigator.turnAbsolute(180));

        gamepadController.rightBumper.onPress(() -> navigator.turnRelative(45));
        gamepadController.leftBumper.onPress(() -> navigator.turnRelative(-45));

        this.telemetry.update();
        this.odometry.update();
    }

}