package intothedeep.auto;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.bootstrap.AbstractRobotConfiguration;
import t10.bootstrap.AutonomousOpMode;
import t10.localizer.odometry.OdometryLocalizer;
import t10.motion.mecanum.MecanumDriver;

import intothedeep.SnowballConfig;
import t10.localizer.odometry.OdometryNavigation;
import t10.geometry.Pose;
import t10.utils.Alliance;

public abstract class EasyAuto extends AutonomousOpMode {
    public MecanumDriver driver;
    public OdometryLocalizer odometry;
    public OdometryNavigation navigator;
    public double idealAngle = 0;
    public double idealX = 0;
    public double idealY = 0;
    private final Alliance alliance;
    private double startingTile = 0;
    private AbstractRobotConfiguration config;

    public EasyAuto(Alliance alliance) {
        this.alliance = alliance;
    }

    public EasyAuto(Alliance alliance, double startingTile) {
        this.alliance = alliance;
        this.startingTile = startingTile;
    }

    @Override
    public void initialize() {
        this.config = new SnowballConfig(this.hardwareMap);
        this.driver = config.createMecanumDriver();
        this.odometry = config.createOdometry();
        this.navigator = new OdometryNavigation(odometry, driver);
        setInitialPose(alliance, startingTile);
    }

    public void setInitialPose(double y, double x, double theta)
    {
        odometry.setFieldCentricPose(new Pose(y,x,theta, AngleUnit.DEGREES));
        idealY = y;
        idealX = x;
        idealAngle = theta;
    }

    public void setInitialPose(Alliance alliance, double startingTile) {
        double startingX = 0;
        double startingY = 0;
        double startingHeading = 0;
        if(alliance != Alliance.NULL)
        {
             startingX = alliance == Alliance.RED ? 60: -60;
             startingY = (startingTile * 24 - 84) * (alliance == Alliance.RED ? 1 : -1);
             startingHeading = alliance == Alliance.RED ? 90 : -90;
        }
        setInitialPose(startingY, startingX, startingHeading);
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
