package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.ArmRotationCapabilities;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import intothedeep.capabilities.ClawCapabilities;
import intothedeep.capabilities.CraneCapabilities;
import org.jetbrains.annotations.NotNull;

import t10.bootstrap.AutonomousOpMode;
import t10.localizer.odometry.OdometryLocalizer;
import t10.motion.mecanum.MecanumDriver;

import intothedeep.SnowballConfig;
import t10.localizer.odometry.OdometryNavigation;
import t10.geometry.Pose;
import t10.utils.Alliance;

@Autonomous
public class MoveWhileRotatingAuto extends AutonomousOpMode {
    public MecanumDriver driver;
    public OdometryLocalizer odometry;
    public OdometryNavigation navigator;
    public double idealAngle = 0;
    public double idealX = 0;
    public double idealY = 0;
    private double startingTile = 0;
    private SnowballConfig config;

    @Override
    public void initialize() {
        this.config = new SnowballConfig(this.hardwareMap);

        // Driving & Localization
        this.driver = config.createMecanumDriver();
        this.odometry = config.createOdometry();
        this.navigator = new OdometryNavigation(odometry, driver);

        // Configure robot's initial state
        //this.setInitialPose(startingTile);
        odometry.setFieldCentricPose(new Pose(0, 48, 0, AngleUnit.DEGREES));
    }

    // TODO: Test this out to see if it works. Otherwise, switch to threads.
    @Override
    public void run() {
        telemetry.addLine("Init");
        telemetry.update();
        this.navigator.driveForwardWhileRotating(40, 40, telemetry);
        telemetry.addLine("Done");
        telemetry.update();
    }

    public void setInitialPose(double y, double x, double theta) {
        odometry.setFieldCentricPose(new Pose(y, x, 0, AngleUnit.DEGREES));
        idealY = y;
        idealX = x;
        idealAngle = 0;
        /*
        TODO: Setting idealAngle to 0 is a terrible fix to ensure easyAuto motion is 'relative' to
         the robot's starting position, because autoBuilder only generates relative motion, not
         absolute motion. NEVER update easyAuto or autoBuilder to use AprilTagLocalizer, because
         that would automatically adjust the robots absolute position to be the correct position &
         orientation, as opposed to it's relative one. When we eventually fix the absolute vs.
         relative issue, idealAngle should be set to -theta.
         */
    }

    public void setInitialPose(double startingTile) {
        double startingX = 0;
        double startingY = 0;
        double startingHeading = 0;

        startingX = 60;
        startingY = (startingTile * 24 - 84);
        startingHeading = 90;

        setInitialPose(startingY, startingX, startingHeading);
    }
}
