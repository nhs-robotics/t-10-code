package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import intothedeep.SnowballConfig;
import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.ArmRotationCapabilities;
import intothedeep.capabilities.ClawCapabilities;
import intothedeep.capabilities.CraneCapabilities;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.geometry.MovementVector;
import t10.geometry.Pose;
import t10.localizer.odometry.OdometryLocalizer;
import t10.localizer.odometry.OdometryNavigation;
import t10.motion.mecanum.MecanumDriver;

@TeleOp
public class OdometryDriveTeleop extends TeleOpOpMode {
    private SnowballConfig config;
    private CraneCapabilities crane;
    private GController g1;
    private GController g2;
    private MecanumDriver driver;
    private ClawCapabilities claw;
    private ArmExtensionCapabilities armExtension;
    private ArmRotationCapabilities armRotation;
    private Telemetry.Item horizontal,vertical, angle, cranePos;
    private OdometryLocalizer odometry;
    private OdometryNavigation navigator;
    double speed = 3;

    @Override
    public void initialize() {
        this.config = new SnowballConfig(this.hardwareMap);

        // Driving
        this.driver = this.config.createMecanumDriver();

        odometry = config.createOdometry();
        navigator = new OdometryNavigation(odometry,driver);

        // Gamepad
        // G1 controls the robot's movement.
        this.g1 = new GController(this.gamepad1)
                .x.initialToggleState(true).ok()
                .dpadUp.onPress(() -> navigator.odometryDrive(10,0)).ok()
                .dpadDown.onPress(() -> navigator.odometryDrive(-10,0)).ok()
                .dpadRight.onPress(() -> navigator.odometryDrive(0,10)).ok()
                .dpadLeft.onPress(() -> navigator.odometryDrive(0,-10)).ok();


        this.vertical = telemetry.addData("y: ", 0);
        this.horizontal = telemetry.addData("x: ", 0);
        this.angle = telemetry.addData("angle: ", 0);
    }

    @Override
    public void loop() {
        Pose pose = odometry.getFieldCentricPose();

        vertical.setValue(pose.getY());
        horizontal.setValue(pose.getX());
        angle.setValue(pose.getHeading(AngleUnit.DEGREES));



        this.driver.useGamepad(this.gamepad1, this.g1.x.isToggled() ? 1 : 0.25);

        this.g1.update();
        this.telemetry.update();
        this.odometry.update();
    }
}
