package intothedeep.teleop;

import ai.onnxruntime.OrtException;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import intothedeep.Constants;
import intothedeep.KevinRobotConfiguration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.geometry.Point3;
import t10.localizer.apriltag.AprilTagLocalizer;
import t10.metrics.Metric;
import t10.metrics.packet.MetricsPointCloudUpdatePacket;
import t10.motion.mecanum.MecanumDriver;
import t10.localizer.odometry.OdometryLocalizer;
import t10.localizer.Localizer;
import t10.geometry.Pose;
import t10.vision.DepthPointCloudProcessor;

import java.io.IOException;
import java.util.List;

@TeleOp
public class DepthTeleOpMode extends TeleOpOpMode {
    private MecanumDriver driver;
    private GController gamepadController;
    private KevinRobotConfiguration c;
    private OdometryLocalizer odometry;
    private DepthPointCloudProcessor p;
    private Localizer localizer;
    private DepthPointCloudProcessor depthCloudProcessor;

    @Metric
    public Pose absolutePose;

    @Override
    public void initialize() {
        this.c = new KevinRobotConfiguration(this.hardwareMap);
        this.driver = this.c.createMecanumDriver();
        this.odometry = this .c.createOdometry();
        this.gamepadController = new GController(this.gamepad1).x.initialToggleState(true).ok();  // micro-movement
        AprilTagLocalizer aprilTagLocalizer = new AprilTagLocalizer(
                Constants.Webcam.C270_FOCAL_LENGTH_X,
                Constants.Webcam.C270_FOCAL_LENGTH_Y,
                Constants.Webcam.C270_PRINCIPAL_POINT_X,
                Constants.Webcam.C270_PRINCIPAL_POINT_Y
        );
        this.localizer = new Localizer(
                aprilTagLocalizer,
                this.odometry,
                new Pose(0, 0, 0, AngleUnit.RADIANS)
        );

        try {
            this.depthCloudProcessor = new DepthPointCloudProcessor(this.localizer, new DistanceSensor() {
                @Override
                public double getDistance(DistanceUnit distanceUnit) {
                    return 40;
                }

                @Override
                public Manufacturer getManufacturer() {
                    return null;
                }

                @Override
                public String getDeviceName() {
                    return "";
                }

                @Override
                public String getConnectionInfo() {
                    return "";
                }

                @Override
                public int getVersion() {
                    return 0;
                }

                @Override
                public void resetDeviceConfigurationForOpMode() {

                }

                @Override
                public void close() {

                }
            }, 0, 0);
        } catch (IOException | OrtException e) {
            throw new RuntimeException(e);
        }

        this.c.webcam.start(
                aprilTagLocalizer.aprilTagProcessor,
                this.depthCloudProcessor
        );
    }

    @Override
    public void loop() {
        this.absolutePose = this.localizer.getFieldCentricPose();

        List<Point3> pointCloud = this.depthCloudProcessor.getPointCloud();
        MetricsPointCloudUpdatePacket packet = new MetricsPointCloudUpdatePacket(pointCloud);
        this.metrics.sendPacket(packet);

        this.gamepadController.update();
        this.driver.useGamepad(this.gamepad1, this.gamepadController.x.isToggled() ? 4 : 1);
    }

    @Override
    public void stop() {
        super.stop();

        try {
            this.depthCloudProcessor.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
