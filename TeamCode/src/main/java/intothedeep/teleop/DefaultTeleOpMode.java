package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import intothedeep.Constants;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import intothedeep.KevinRobotConfiguration;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.geometry.Pose;
import t10.localizer.Localizer;
import t10.localizer.apriltag.AprilTagLocalizer;
import t10.localizer.odometry.OdometryLocalizer;
import t10.metrics.Metric;
import t10.motion.mecanum.MecanumDriver;

@TeleOp
public class DefaultTeleOpMode extends TeleOpOpMode {
    private MecanumDriver driver;
    private GController gamepadController;
    private KevinRobotConfiguration c;
    private OdometryLocalizer odometry;

    @Metric
    public Pose pose;

    @Metric
    public int microMovement;
    private Localizer localizer;
    private AprilTagLocalizer atl;

    @Override
    public void initialize() {
        this.c = new KevinRobotConfiguration(this.hardwareMap);
        this.driver = this.c.createMecanumDriver();
        this.odometry = this.c.createOdometry();
        this.gamepadController = new GController(this.gamepad1)
                .x.initialToggleState(true).ok();  // micro-movement
        this.atl = new AprilTagLocalizer(Constants.Webcam.C270_FOCAL_LENGTH_X, Constants.Webcam.C270_FOCAL_LENGTH_Y, Constants.Webcam.C270_PRINCIPAL_POINT_X, Constants.Webcam.C270_PRINCIPAL_POINT_Y);
        this.localizer = new Localizer(
                atl,
                this.odometry,
                new Pose(0, 0, 0, AngleUnit.RADIANS)
        );
        this.c.webcam.start(atl.aprilTagProcessor);
        this.metrics.streamWebcam(this.c.webcam);
    }

    @Override
    public void loop() {
        this.pose = this.localizer.getFieldCentricPose();
        this.microMovement = this.gamepadController.x.isToggled() ? 1 : 0;

        this.gamepadController.update();
        this.driver.useGamepad(this.gamepad1, this.gamepadController.x.isToggled() ? 4 : 1);
        this.telemetry.update();
    }
}
