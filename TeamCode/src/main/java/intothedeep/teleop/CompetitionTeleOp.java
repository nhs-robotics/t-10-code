package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import intothedeep.SnowballConfig;
import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.ArmRotationCapabilities;
import intothedeep.capabilities.ClawCapabilities;
import intothedeep.capabilities.CraneCapabilities;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.geometry.Pose;
import t10.localizer.odometry.OdometryLocalizer;
import t10.motion.mecanum.MecanumDriver;

@TeleOp
public class CompetitionTeleOp extends TeleOpOpMode {
    private SnowballConfig config;
    private CraneCapabilities crane;
    private GController g1;
    private GController g2;
    private MecanumDriver driver;
    private ClawCapabilities claw;
    private ArmExtensionCapabilities armExtension;
    private ArmRotationCapabilities armRotation;
    private Telemetry.Item horizontal,vertical, angle, extension;
    private OdometryLocalizer odometry;

    @Override
    public void initialize() {
        this.config = new SnowballConfig(this.hardwareMap);

        // Robot Capabilities
        this.crane = new CraneCapabilities(this.config);
        this.armExtension = new ArmExtensionCapabilities(this.config);
        this.armRotation = new ArmRotationCapabilities(this.config);
        this.claw = new ClawCapabilities(this.config);

        // Driving
        this.driver = this.config.createMecanumDriver();

        // Gamepad
        // G1 controls the robot's movement.
        this.g1 = new GController(this.gamepad1)
                .x.initialToggleState(true).ok();

        // G2 controls the intake/outtake
        this.g2 = new GController(this.gamepad2)
                .rightTrigger.whileDown(proportion -> this.armExtension.setPowerManually(-proportion)).onRelease(() -> this.armExtension.setPowerManually(0)).ok()
                .leftTrigger.whileDown(proportion -> this.armExtension.setPowerManually(proportion)).onRelease(() -> this.armExtension.setPowerManually(0)).ok()
                .rightJoystick.onMove((x, y) -> this.crane.setPowerManually(-y)).ok()
                .leftJoystick.onMove((x,  y) -> this.armRotation.setPowerManually(-y)).ok()
                .a.onPress(() -> this.claw.toggle()).ok();
        odometry = config.createOdometry();

        this.vertical = telemetry.addData("y: ", 0);
        this.horizontal = telemetry.addData("x: ", 0);
        this.angle = telemetry.addData("angle: ", 0);
        this.extension = telemetry.addData("extension: ", 0);
    }

    @Override
    public void loop() {
        Pose pose = odometry.getFieldCentricPose();
        vertical.setValue(pose.getY());
        horizontal.setValue(pose.getX());
        angle.setValue(pose.getHeading(AngleUnit.DEGREES));
        extension.setValue(armExtension.getPosition());
        this.driver.useGamepad(this.gamepad1, this.g1.x.isToggled() ? 1 : 0.5);
        this.g2.update();
        this.g1.update();
        this.crane.update();
        this.telemetry.update();
        this.odometry.update();
        this.armExtension.update();
        this.armRotation.update();
    }
}
