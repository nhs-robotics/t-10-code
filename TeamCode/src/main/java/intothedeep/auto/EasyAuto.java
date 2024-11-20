package intothedeep.auto;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.bootstrap.AutonomousOpMode;
import t10.localizer.odometry.OdometryLocalizer;
import t10.motion.mecanum.MecanumDriver;

import intothedeep.Constants;
import intothedeep.IntoTheDeepRobotConfiguration;
import t10.localizer.odometry.OdometryNavigation;
import t10.geometry.Pose;

public abstract class EasyAuto extends AutonomousOpMode {
    private IntoTheDeepRobotConfiguration config;
    public MecanumDriver driver;
    public OdometryLocalizer odometry;
    public OdometryNavigation navigator;
    public Telemetry.Item x, y, r;
    public double idealAngle = 0;
    public double idealX = 0;
    public double idealY = 0;


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
        odometry.setFieldCentricPose(new Pose(y,x,theta, AngleUnit.DEGREES));
        idealY = 0;
        idealX = 0;
        idealAngle = 0;
    }

    public void horizontalMovement(double distX) {
        idealX += distX;
        this.navigator.driveHorizontal(distX);
    }

    public void verticalMovement(double distY) {
        idealY += distY;
        this.navigator.driveLateral(distY);
    }

    public void diagonalMovement(double distX, double distY) {
        idealX += distX;
        idealY += distY;
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

    /**
     * The below correction functions are currently untested. You have been warned.
     * -Arlan
     */
    public void angleCorrect()
    {
        turnTo(idealAngle);
    }

    public void horizontalCorrect()
    {
        navigator.driveHorizontal(idealX - odometry.getFieldCentricPose().getX());
    }

    public void verticalCorrect()
    {
        navigator.driveLateral(idealY - odometry.getFieldCentricPose().getY());
    }

    public void correctAll()
    {
        angleCorrect();
        horizontalCorrect();
        verticalCorrect();
    }
}
