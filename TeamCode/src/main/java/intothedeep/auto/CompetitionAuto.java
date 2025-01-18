package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import intothedeep.Constants;
import intothedeep.SnowballConfig;
import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.ArmRotationCapabilities;
import intothedeep.capabilities.ClawCapabilities;
import intothedeep.capabilities.CraneCapabilities;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.jetbrains.annotations.NotNull;
import t10.bootstrap.AutonomousOpMode;
import t10.geometry.Pose;
import t10.localizer.Localizer;
import t10.localizer.apriltag.AprilTagLocalizer;
import t10.localizer.odometry.OdometryLocalizer;
import t10.motion.mecanum.MecanumDriver;

@Autonomous
public class CompetitionAuto extends AutonomousOpMode {
    private SnowballConfig config;
    private MecanumDriver driver;
    private ArmExtensionCapabilities armExtension;
    private ArmRotationCapabilities armRotation;
    private ClawCapabilities claw;
    private CraneCapabilities crane;
    private OdometryLocalizer odometry;
    private AprilTagLocalizer aprilTagLocalizer;
    private Localizer localizer;
    private LocalizationThread localizationThread;
    private CapabilitiesUpdateThread capabilitiesUpdateThread;

    @Override
    public void initialize() {
        this.config = new SnowballConfig(this.hardwareMap);

        // Capabilities
        this.armExtension = new ArmExtensionCapabilities(this.config);
        this.armRotation = new ArmRotationCapabilities(this.config);
        this.claw = new ClawCapabilities(this.config);
        this.crane = new CraneCapabilities(this.config);

        // Driving & Localization
        this.driver = this.config.createMecanumDriver();
        this.odometry = this.config.createOdometry();
        this.aprilTagLocalizer = new AprilTagLocalizer(Constants.Webcam.C270_FOCAL_LENGTH_X, Constants.Webcam.C270_FOCAL_LENGTH_Y, Constants.Webcam.C270_PRINCIPAL_POINT_X, Constants.Webcam.C270_PRINCIPAL_POINT_Y);
        this.localizer = new Localizer(this.aprilTagLocalizer, this.odometry, new Pose(0, 0, 0, AngleUnit.RADIANS));

        // Configure systems
        this.armRotation.setTargetPosition(ArmRotationCapabilities.POSITION_INSPECTION);
        this.armExtension.setTargetPosition(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED);
        this.crane.setTargetPosition(CraneCapabilities.POSITION_BOTTOM);
        this.claw.setOpen(false);

        // Start threads
        // TODO: test to see if the `loop()` works, if not use threads like commented out below
//        this.localizationThread = new LocalizationThread(this, this.odometry);
//        this.localizationThread.start();
//        this.capabilitiesUpdateThread = new CapabilitiesUpdateThread(this, this.armExtension, this.armRotation, this.crane);
//        this.capabilitiesUpdateThread.start();
    }

    @Override
    public void run() {
        // TODO: integrate with EasyAuto.
    }

    @Override
    public void loop() {
        this.odometry.update();
        this.armRotation.update();
        this.armExtension.update();
        this.crane.update();
    }

    public static class LocalizationThread extends Thread {
        private final @NotNull CompetitionAuto competitionAuto;
        private @NotNull OdometryLocalizer localizer;

        public LocalizationThread(@NotNull CompetitionAuto competitionAuto, @NotNull OdometryLocalizer localizer) {
            this.competitionAuto = competitionAuto;
            this.localizer = localizer;
        }

        public void setLocalizer(@NotNull OdometryLocalizer localizer) {
            this.localizer = localizer;
        }

        @Override
        public void run() {
            while (!this.competitionAuto.isStopRequested()) {
                this.localizer.update();
            }
        }
    }

    public static class CapabilitiesUpdateThread extends Thread {
        private final @NotNull CompetitionAuto competitionAuto;
        private @NotNull ArmExtensionCapabilities armExtension;
        private @NotNull ArmRotationCapabilities armRotation;
        private @NotNull CraneCapabilities crane;

        public CapabilitiesUpdateThread(@NotNull CompetitionAuto competitionAuto, @NotNull ArmExtensionCapabilities armExtension, @NotNull ArmRotationCapabilities armRotation, @NotNull CraneCapabilities crane) {
            this.competitionAuto = competitionAuto;
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
            while (!this.competitionAuto.isStopRequested()) {
                this.armRotation.update();
                this.armExtension.update();
                this.crane.update();
            }
        }
    }
}
