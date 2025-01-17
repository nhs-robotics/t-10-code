package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import intothedeep.SnowballConfig;
import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.ArmRotationCapabilities;
import intothedeep.capabilities.ClawCapabilities;
import intothedeep.capabilities.CraneCapabilities;
import t10.bootstrap.AutonomousOpMode;
import t10.geometry.MovementVector;
import t10.motion.mecanum.MecanumDriver;

@Autonomous
public class CompetitionAuto extends AutonomousOpMode {
    private SnowballConfig config;
    private MecanumDriver driver;
    private ArmExtensionCapabilities armExtension;
    private ArmRotationCapabilities armRotation;
    private ClawCapabilities claw;
    private CraneCapabilities crane;

    @Override
    public void initialize() {
        this.config = new SnowballConfig(this.hardwareMap);
        this.driver = this.config.createMecanumDriver();
        this.armExtension = new ArmExtensionCapabilities(this.config);
        this.armRotation = new ArmRotationCapabilities(this.config);
        this.claw = new ClawCapabilities(this.config);
        this.crane = new CraneCapabilities(this.config);
    }

    @Override
    public void run() {
        while (!this.crane.isAtTargetPosition()) {
            this.crane.update();
        }
    }
}
