package intothedeep.auto;

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
    public ArmExtensionCapabilities armExtension;
    public ArmRotationCapabilities armRotation;
    public ClawCapabilities claw;
    public CraneCapabilities crane;
    private CapabilitiesUpdateThread updater;

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

        // Driving & Localization
        this.driver = config.createMecanumDriver();
        this.odometry = config.createOdometry();
        this.navigator = new OdometryNavigation(odometry, driver);


        // Capabilities
        this.armExtension = new ArmExtensionCapabilities(config);
        this.armRotation = new ArmRotationCapabilities(config);
        this.claw = new ClawCapabilities(config);
        this.crane = new CraneCapabilities(config);


        this.updater = new CapabilitiesUpdateThread(this,armExtension,armRotation, crane);
        updater.start();

        // Configure robot's initial state
        this.armRotation.setTargetPosition(ArmRotationCapabilities.POSITION_INSPECTION);
        this.armExtension.setTargetPosition(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED);
        this.crane.setTargetPosition(CraneCapabilities.POSITION_BOTTOM);
        this.claw.setOpen(false);  // Keep closed to grasp a block for auto
        this.setInitialPose(alliance, startingTile);
    }

    // TODO: Test this out to see if it works. Otherwise, switch to threads.
    @Override
    public void loop() {
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

    public void setInitialPose(Alliance alliance, double startingTile) {
        double startingX = 0;
        double startingY = 0;
        double startingHeading = 0;

        if (alliance != null) {
            startingX = alliance == Alliance.RED ? 60 : -60;
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
        this.navigator.driveDiagonal(distX, distY);
    }

    public void turnTo(double angle) {
        this.navigator.turnAbsolute(angle);
        idealAngle = angle;
    }

    public void turnRelative(double angle) {
        this.navigator.turnRelative(angle);
        idealAngle += angle;
        if (idealAngle > 180) {
            idealAngle -= 180;
        }
        if (idealAngle < -180) {
            idealAngle += 180;
        }
    }

    /**
     * The below correction functions are currently untested. You have been warned.
     * -Arlan
     */
    public void angleCorrect() {
        turnTo(idealAngle);
    }

    public void horizontalCorrect() {
        navigator.driveHorizontal(idealX - odometry.getFieldCentricPose().getX());
    }

    public void verticalCorrect() {
        navigator.driveLateral(idealY - odometry.getFieldCentricPose().getY());
    }

    public void correctAll() {
        angleCorrect();
        horizontalCorrect();
        verticalCorrect();
    }

    private static class LocalizationUpdateThread extends Thread {
        private final @NotNull EasyAuto easyAuto;
        private @NotNull OdometryLocalizer localizer;

        public LocalizationUpdateThread(@NotNull EasyAuto easyAuto, @NotNull OdometryLocalizer localizer) {
            this.easyAuto = easyAuto;
            this.localizer = localizer;
        }

        public void setLocalizer(@NotNull OdometryLocalizer localizer) {
            this.localizer = localizer;
        }

        @Override
        public void run() {
            while (!this.easyAuto.isStopRequested()) {
                this.localizer.update();
            }
        }
    }





    private static class CapabilitiesUpdateThread extends Thread {
        private final @NotNull EasyAuto easyAuto;
        private @NotNull ArmExtensionCapabilities armExtension;
        private @NotNull ArmRotationCapabilities armRotation;
        private @NotNull CraneCapabilities crane;

        public CapabilitiesUpdateThread(@NotNull EasyAuto easyAuto, @NotNull ArmExtensionCapabilities armExtension, @NotNull ArmRotationCapabilities armRotation, @NotNull CraneCapabilities crane) {
            this.easyAuto = easyAuto;
            this.armExtension = armExtension;
            this.armRotation = armRotation;
            this.crane = crane;
        }

        public void setArmExtension(@NotNull ArmExtensionCapabilities armExtension) {
            this.armExtension = armExtension;
        }

        public void setArmRotation(@NotNull ArmRotationCapabilities armRotation) {
            this.armRotation = armRotation;
        }

        public void setCrane(@NotNull CraneCapabilities crane) {
            this.crane = crane;
        }

        @Override
        public void run() {
            while (!this.easyAuto.isStopRequested()) {
                this.armRotation.update();
                this.armExtension.update();
                this.crane.update();
            }
        }
    }
}
