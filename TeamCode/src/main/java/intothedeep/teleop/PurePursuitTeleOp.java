package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import intothedeep.Constants;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import intothedeep.IntoTheDeepRobotConfiguration;
import intothedeep.KevinRobotConfiguration;
import t10.bootstrap.AbstractRobotConfiguration;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.geometry.Pose;
import t10.localizer.Localizer;
import t10.localizer.apriltag.AprilTagLocalizer;
import t10.localizer.odometry.OdometryLocalizer;
import t10.metrics.Metric;
import t10.motion.mecanum.MecanumDriver;
import t10.motion.path.PurePursuitPathFollower;

@TeleOp
public class PurePursuitTeleOp extends TeleOpOpMode {
    private MecanumDriver driver;
    private GController gamepadController;
    private AbstractRobotConfiguration c;
    private OdometryLocalizer odometry;
    private Telemetry.Item x;
    private Telemetry.Item y;
    private Telemetry.Item r;
    private Telemetry.Item movementPower;
    private Telemetry.Item vert, hor;
    private PurePursuitPathFollower humanPlayerPathFollower;
    private PurePursuitPathFollower specimenRungPathFollower;
    private PurePursuitPathFollower specimenBasketPathFollower;
    private PurePursuitPathFollower hangingBarPathFollower;
    private boolean followingPath = false;
    private double purePursuitLookAheadDistance = 18; // TODO: Find Optimal Lookahead Distance
    private double purePursuitSpeed = 25; // TODO: Find Optimal Speed

    @Metric
    public Pose pose;

    @Metric
    public int microMovement;
    private Localizer localizer;
    private AprilTagLocalizer atl;

    @Override
    public void initialize() {
        this.x = this.telemetry.addData("x_novel: ", "0");
        this.y = this.telemetry.addData("y_novel: ", "0");
        this.r = this.telemetry.addData("r_novel: ", "0");
        this.movementPower = this.telemetry.addData("movement power: ", "0");
        this.vert = this.telemetry.addData("vert: ", "0");
        this.hor = this.telemetry.addData("hor: ", "0");
        this.c = new IntoTheDeepRobotConfiguration(this.hardwareMap);
        this.driver = this.c.createMecanumDriver();
        this.odometry = this.c.createOdometry();
        this.gamepadController = new GController(this.gamepad1)
                .leftBumper.initialToggleState(true).ok();  // micro-movement
        this.atl = new AprilTagLocalizer(Constants.Webcam.C270_FOCAL_LENGTH_X, Constants.Webcam.C270_FOCAL_LENGTH_Y, Constants.Webcam.C270_PRINCIPAL_POINT_X, Constants.Webcam.C270_PRINCIPAL_POINT_Y);
        this.localizer = new Localizer(
                null, // TODO: Switch to this.atl when Camera is calibrated.
                this.odometry,
                new Pose(0, 0, 0, AngleUnit.RADIANS) //TODO: Change x back to 48
        );

        // Initialize Path Followers
        {
            humanPlayerPathFollower = new PurePursuitPathFollower.Builder()
                    .addPoint(0, -48)
                    .addPoint(48, -48)
                    .addPoint(0, 48)
                    .addPoint(48, 48)
                    .addPoint(48, -48)
                    .addPoint(60, -60)
                    .setLocalizer(this.localizer)
                    .setLookaheadDistance(purePursuitLookAheadDistance)
                    .setSpeed(purePursuitSpeed)
                    .build();

            specimenRungPathFollower = new PurePursuitPathFollower.Builder()
                    .addPoint(0, -48)
                    .addPoint(48, -48)
                    .addPoint(48, 0)
                    .addPoint(0, 48)
                    .addPoint(48, 48)
                    .addPoint(48, 0)
                    .addPoint(36, 0)
                    .setLocalizer(this.localizer)
                    .setLookaheadDistance(purePursuitLookAheadDistance)
                    .setSpeed(purePursuitSpeed)
                    .build();

            specimenBasketPathFollower = new PurePursuitPathFollower.Builder()
                    .addPoint(0, 48)
                    .addPoint(48, 48)
                    .addPoint(0, -48)
                    .addPoint(48, -48)
                    .addPoint(48, 48)
                    .addPoint(60, 60)
                    .setLocalizer(this.localizer)
                    .setLookaheadDistance(purePursuitLookAheadDistance)
                    .setSpeed(purePursuitSpeed)
                    .build();

            hangingBarPathFollower = new PurePursuitPathFollower.Builder()
                    .addPoint(0, -48)
                    .addPoint(48, -48)
                    .addPoint(48, 48)
                    .addPoint(0, 48)
                    .addPoint(0, 36)
                    .setLocalizer(this.localizer)
                    .setLookaheadDistance(purePursuitLookAheadDistance)
                    .setSpeed(purePursuitSpeed)
                    .build();
        }


    }

    @Override
    public void loop() {
        this.pose = this.localizer.getFieldCentricPose();

        if (!this.gamepadController.rightBumper.isPressed() && !this.isStopRequested()) {
            try {
                if (this.gamepadController.x.isPressed()) {
                    humanPlayerPathFollower.follow(this.driver);
                } else if (this.gamepadController.y.isPressed()) {
                    specimenRungPathFollower.follow(this.driver);
                } else if (this.gamepadController.a.isPressed()) {
                    specimenBasketPathFollower.follow(this.driver);
                } else if (this.gamepadController.b.isPressed()) {
                    hangingBarPathFollower.follow(this.driver);
                } else {
                    this.microMovement = this.gamepadController.leftBumper.isToggled() ? 1 : 0;
                    this.gamepadController.update();
                    this.driver.useGamepad(this.gamepad1, this.gamepadController.leftBumper.isToggled() ? 0.25 : 1);
                }
            } catch (IllegalStateException e) {
                System.out.println("Too far from lookahead path.");
            }
        }


        this.x.setValue(this.odometry.getFieldCentricPose().getX());
        this.y.setValue(this.odometry.getFieldCentricPose().getY());
        this.r.setValue(this.odometry.getFieldCentricPose().getHeading(AngleUnit.DEGREES));
        this.vert.setValue(this.odometry.getRobotCentricVelocity(-1,0));
        this.hor.setValue(this.odometry.getRobotCentricVelocity(0,1));
        this.movementPower.setValue(this.gamepadController.leftBumper.isToggled() ? 0.25 : 1);
        this.telemetry.update();
        this.odometry.update();
    }
}
