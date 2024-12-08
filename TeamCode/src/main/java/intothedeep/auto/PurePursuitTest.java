package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import intothedeep.Constants;
import intothedeep.KevinRobotConfiguration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import t10.bootstrap.AutonomousOpMode;
import t10.geometry.Pose;
import t10.localizer.Localizer;
import t10.localizer.apriltag.AprilTagLocalizer;
import t10.localizer.odometry.OdometryLocalizer;
import t10.metrics.Metric;
import t10.motion.mecanum.MecanumDriver;
import t10.motion.path.PurePursuitPathFollower;

@Autonomous
public class PurePursuitTest extends AutonomousOpMode {
    private KevinRobotConfiguration c;
    private MecanumDriver driver;
    private OdometryLocalizer odometry;
    private Localizer localizer;
    private PurePursuitPathFollower path1;
    private PurePursuitPathFollower path2;

    @Metric
    public Pose pose;

    @Override
    public void initialize() {
        this.c = new KevinRobotConfiguration(this.hardwareMap);
        this.driver = this.c.createMecanumDriver();
        this.odometry = this.c.createOdometry();
        AprilTagLocalizer aprilTagLocalizer = new AprilTagLocalizer(Constants.Webcam.C270_FOCAL_LENGTH_X, Constants.Webcam.C270_FOCAL_LENGTH_Y, Constants.Webcam.C270_PRINCIPAL_POINT_X, Constants.Webcam.C270_PRINCIPAL_POINT_Y);
        this.localizer = new Localizer(
                aprilTagLocalizer,
                this.odometry,
                new Pose(-30, 62, 0, AngleUnit.DEGREES)
        );
        this.c.webcam.start(aprilTagLocalizer.aprilTagProcessor);
        this.path1 = new PurePursuitPathFollower.Builder()
                .addPoint(0, 44)
                .addPoint(44, 44)
                .addPoint(44, -44)
                .addPoint(-44, -44)
                .addPoint(-44, 0)
                .setLocalizer(this.localizer)
                .setLookaheadDistance(12.5)
                .setSpeed(30)
                .build();
    }

    private void followPath(PurePursuitPathFollower path) {
        while (!this.isStopRequested() && !path.follow(this.driver)) {
            this.pose = this.localizer.getFieldCentricPose();
        }
    }

    @Override
    public void run() {
        this.followPath(this.path1);
        this.followPath(this.path2);
    }
}
