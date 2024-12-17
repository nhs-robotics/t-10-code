package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import intothedeep.CraneCapabilities;
import intothedeep.SnowballConfig;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.geometry.Pose;
import t10.localizer.odometry.OdometryLocalizer;
import t10.motion.mecanum.MecanumDriver;
import t10.motion.profile.TrapezoidalMotionProfile;

@TeleOp
public class VeloProfileTestDrive extends TeleOpOpMode {
    private SnowballConfig config;
    private MecanumDriver driver;
    private OdometryLocalizer odometry;
    private GController g1;    private Telemetry.Item x;
    private Telemetry.Item y;
    private Telemetry.Item r;
    private Telemetry.Item distVal, lookAheadVal, state, changeDir, veloProfile, profileDone, vInitVal, pos, vFinalVal;
    private TrapezoidalMotionProfile profile;
    private double dist = 40, lookAhead = 2, vFinal = 0, acceleration = 10, vInit = 0, position = 0;
    private double currentY = 0;
    double velocity = 0;
    boolean velocitying = false;


    @Override
    public void initialize() {
        this.x = this.telemetry.addData("x: ", "0");
        this.y = this.telemetry.addData("y: ", "0");
        this.r = this.telemetry.addData("r: ", "0");
        this.veloProfile = this.telemetry.addData("velocity profile: ", "0");
        this.pos = this.telemetry.addData("position: ","0");

        profile = new TrapezoidalMotionProfile(0, 15,vFinal,acceleration,dist,lookAhead);
        this.config = new SnowballConfig(this.hardwareMap);
        this.driver = this.config.createMecanumDriver();
        this.odometry = this.config.createOdometry();
        this.g1 = new GController(gamepad1)
                .dpadUp.onPress(() -> dist += 10).ok()
                .dpadDown.onPress(() -> dist -= 10).ok()
                .y.onPress(() -> position += 1).ok()
                .a.onPress(() -> position -= 1).ok()
                .rightBumper.onPress(() -> vFinal += 1).ok()
                .leftBumper.onPress(() -> vFinal -= 1).ok()
                .b.onPress(() -> currentY = odometry.getFieldCentricPose().getY()).ok()
                .x.onPress(() -> velocitying = !velocitying).ok();
    }

    @Override
    public void loop() {
        //driver.setVelocity(odometry.getRobotCentricVelocity(profile.getVelocity(odometry.getFieldCentricPose().getY() - startPose.getY()), 0));
        odometry.update();
        telemetry.update();
        this.x.setValue(odometry.getFieldCentricPose().getX());
        this.y.setValue(odometry.getFieldCentricPose().getY());
        this.r.setValue(odometry.getFieldCentricPose().getHeading(AngleUnit.DEGREES));
        this.pos.setValue(odometry.getFieldCentricPose().getY() - currentY);

        velocity = profile.getVelocity(odometry.getFieldCentricPose().getY() - currentY);
        this.veloProfile.setValue(velocity);
        if (velocitying) {
            driver.setVelocity(odometry.getRobotCentricVelocity(profile.getVelocity(odometry.getFieldCentricPose().getY() - currentY), 0));
        }
        else {
            this.driver.useGamepad(this.gamepad1, 1);
        }

    }
    private void drive()
    {
        double startPoseY = odometry.getFieldCentricPose().getY();
        TrapezoidalMotionProfile profile1 = new TrapezoidalMotionProfile(vInit,15,0,10,dist,lookAhead);
        while(!profile1.isDone())
        {
            double currentPosY = odometry.getFieldCentricPose().getY();
            driver.setVelocity(odometry.getRobotCentricVelocity(profile1.getVelocity(currentPosY - startPoseY),0));
        }
    }
}
