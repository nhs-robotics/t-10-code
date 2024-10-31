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
    public MecanumDriver driver;
    public NovelOdometry odometry;
    public OdometryNavigation navigator;
    public Telemetry.Item x, y, r;
    public double idealAngle = 0;


    public EasyAuto() {}

    @Override
    public void initialize() {
        this.config = new IntoTheDeepRobotConfiguration(this.hardwareMap);
        this.driver = config.createMecanumDriver();
        this.odometry = config.createOdometry();
        this.navigator = new OdometryNavigation(odometry, driver);
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
    }

    public void verticalMovement(double distY) {
        this.navigator.driveLateral(distY);
    }

    public void diagonalMovement(double distX, double distY) {
        this.navigator.driveDiagonal(distX,distY);
    }

    public void turnTo(double angle) {
        this.navigator.turnAbsolute(angle);
        idealAngle = angle;
    }

    public void turnRelative(double angle) {
        this.navigator.turnRelative(angle);
        idealAngle += angle;
        if(idealAngle > 180) {idealAngle -= 180;}
        if(idealAngle < -180) {idealAngle += 180;}
    }
    public void angleCorrect()
    {
        turnTo(idealAngle);
    }
}
