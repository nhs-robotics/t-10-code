package intothedeep.auto;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import intothedeep.ArmCapabilities;
import intothedeep.ClawCapabilities;
import intothedeep.CraneCapabilities;
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
    private SnowballConfig config;
    private ArmCapabilities arm;
    private ClawCapabilities claw;
    private CraneCapabilities crane;
    //private UpdateAuto updateAuto;

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
        this.arm = new ArmCapabilities(config);
        this.claw = new ClawCapabilities(config);
        this.crane = new CraneCapabilities(config);
        //this.updateAuto = new UpdateAuto(arm, crane);
        setInitialPose(alliance, startingTile);
        //this.updateAuto.start();
    }

    public void setInitialPose(double y, double x, double theta)
    {
        odometry.setFieldCentricPose(new Pose(y,x,0, AngleUnit.DEGREES));
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

    public void setInitialPose(Alliance alliance, double startingTile) {
        double startingX = 0;
        double startingY = 0;
        double startingHeading = 0;
        if(alliance != null)
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

    public void openClaw() {
        claw.setPosition(true);
    }

    public void closeClaw() {
        claw.setPosition(false);
    }

    public void setCraneBottom() {
        crane.positionBottom();
    }

    public void setCraneLowBasket() {
        crane.positionLowBasket();
    }

    public void setCraneHighBasket() {
        crane.positionHighBasket();
    }

    public void stop() {
        //this.updateAuto.stopNow();
        super.stop();
    }
}

class UpdateAuto extends Thread {
    private final ArmCapabilities arm;
    private final CraneCapabilities crane;
    private boolean stop;

    public UpdateAuto(ArmCapabilities arm, CraneCapabilities crane) {
        this.arm = arm;
        this.crane = crane;
        this.stop = false;
    }

    @Override
    public void run() {
        while (!stop) {
            arm.update();
            crane.update();
        }
    }

    public void stopNow() {
        stop = true;
    }
}