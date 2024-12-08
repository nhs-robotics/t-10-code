package intothedeep.teleop;

import android.annotation.SuppressLint;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import intothedeep.Constants;
import intothedeep.KevinRobotConfiguration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.geometry.Pose;
import t10.localizer.Localizer;
import t10.localizer.apriltag.AprilTagLocalizer;
import t10.localizer.odometry.OdometryLocalizer;
import t10.metrics.Metric;
import t10.motion.mecanum.MecanumDriver;

@TeleOp
public class PathCreatorPushTeleOp extends TeleOpOpMode {
    private MecanumDriver driver;
    private GController gamepadController;
    private KevinRobotConfiguration c;
    private OdometryLocalizer odometry;

    @Metric
    public Pose pose;

    @Metric
    public String recordedPoints = "";

    private Localizer localizer;
    private AprilTagLocalizer atl;

    @SuppressLint("DefaultLocale")
    @Override
    public void initialize() {
        this.c = new KevinRobotConfiguration(this.hardwareMap);
        this.driver = this.c.createMecanumDriver();
        this.odometry = this.c.createOdometry();
        this.gamepadController = new GController(this.gamepad1)
                .a.onPress(() -> {
                    Pose fieldCentricPose = this.localizer.getFieldCentricPose();
                    recordedPoints += String.format("%f %f %f\n", fieldCentricPose.getX(), fieldCentricPose.getY(), fieldCentricPose.getHeading(AngleUnit.RADIANS));
                }).ok();
        this.atl = new AprilTagLocalizer(Constants.Webcam.C270_FOCAL_LENGTH_X, Constants.Webcam.C270_FOCAL_LENGTH_Y, Constants.Webcam.C270_PRINCIPAL_POINT_X, Constants.Webcam.C270_PRINCIPAL_POINT_Y);
        this.localizer = new Localizer(
                atl,
                this.odometry,
                new Pose(0, 0, 0, AngleUnit.RADIANS)
        );
        this.c.webcam.start(atl.aprilTagProcessor);
        this.metrics.streamWebcam(this.c.webcam);
        this.c.fl.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.c.bl.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.c.br.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.c.fr.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    @Override
    public void loop() {
        this.pose = this.localizer.getFieldCentricPose();

        this.gamepadController.update();
        this.driver.useGamepad(this.gamepad1, 0.5);
        this.telemetry.update();
    }
}
