package intothedeep.auto;

import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.bootstrap.AutonomousOpMode;
import t10.novel.mecanum.MecanumDriver;

import intothedeep.Constants;
import intothedeep.IntoTheDeepRobotConfiguration;
import t10.novel.odometry.NovelOdometry;
import t10.novel.odometry.OdometryNavigation;
import t10.reconstructor.Pose;
import t10.utils.MovementVector;

public abstract class EasyAuto extends AutonomousOpMode {
    private IntoTheDeepRobotConfiguration config;
    private MecanumDriver driver;
    private NovelOdometry odometry;
    private OdometryNavigation navigator;
    private Telemetry.Item x, y, r;


    public EasyAuto() {}

    @Override
    public void initialize() {
        this.config = new IntoTheDeepRobotConfiguration(this.hardwareMap);
        this.driver = config.createMecanumDriver();
        this.odometry = config.createOdometry();
        this.navigator = new OdometryNavigation(odometry, driver, telemetry);
        this.x = this.telemetry.addData("x_novel: ", "0");
        this.y = this.telemetry.addData("y_novel: ", "0");
        this.r = this.telemetry.addData("r_novel: ", "0");
    }

    public void setInitialPose(double y,double x,double theta)
    {
        odometry.setRelativePose(new Pose(y,x,theta, AngleUnit.DEGREES));
    }

    public void horizontalMovement(double distX) {
        this.navigator.driveHorizontal(distX);
        sleep(1);
    }

    public void verticalMovement(double distY) {
        this.navigator.driveLateral(distY);
        sleep(1);
    }

    public void diagonalMovement(double distX, double distY) {
        this.navigator.driveDiagonal(distX,distY);
        sleep(1);
    }

    public void turnTo(double angle) {
        this.navigator.turnAbsolute(angle);
    }

    public void turnRelative(double angle) {
        this.navigator.turnRelative(angle);
    }
}
