package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
<<<<<<< HEAD
import intothedeep.SnowballConfig;
import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.ArmRotationCapabilities;
import intothedeep.capabilities.ClawCapabilities;
import intothedeep.capabilities.CraneCapabilities;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
=======

>>>>>>> 532e169d62ad9220e9c2a48a590ca1c564a7016d
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.motion.mecanum.MecanumDriver;

@TeleOp
public class CompetitionTeleOp extends TeleOpOpMode {
    private SnowballConfig config;
    private CraneCapabilities crane;
    private GController g1;
    private GController g2;
    private MecanumDriver driver;
    private ClawCapabilities claw;
<<<<<<< HEAD
    private ArmExtensionCapabilities armExtension;
    private ArmRotationCapabilities armRotation;
=======
    private ArmExtensionCapabilities extension;

    private Telemetry.Item x;
    private Telemetry.Item y;
    private Telemetry.Item r;
    private Telemetry.Item extend_length, rotation, craneLeft, craneRight;
>>>>>>> 532e169d62ad9220e9c2a48a590ca1c564a7016d

    @Override
    public void initialize() {
        this.config = new SnowballConfig(this.hardwareMap);

        // Robot Capabilities
        this.crane = new CraneCapabilities(this.config);
        this.armExtension = new ArmExtensionCapabilities(this.config);
        this.armRotation = new ArmRotationCapabilities(this.config);
        this.claw = new ClawCapabilities(this.config);
<<<<<<< HEAD

        // Driving
        this.driver = this.config.createMecanumDriver();

        // Gamepad
        // G1 controls the robot's movement.
=======
        this.extension = new ArmExtensionCapabilities(config);
        this.g2 = new GController(this.gamepad2)
                .dpadUp.onPress(() -> this.extension.extend(1)).onRelease(() -> this.extension.extend(0)).ok()
                .dpadDown.onPress(() -> this.extension.extend(-1)).onRelease(() -> this.extension.extend(0)).ok()
                .a.onPress(() -> this.claw.toggle()).ok()
                .x.onPress(() -> this.crane.positionHighBasket()).ok();
>>>>>>> 532e169d62ad9220e9c2a48a590ca1c564a7016d
        this.g1 = new GController(this.gamepad1)
                .x.initialToggleState(true).ok();

<<<<<<< HEAD
        // G2 controls the intake/outtake
        this.g2 = new GController(this.gamepad2)
                .rightTrigger.whileDown(proportion -> this.armExtension.setPowerManually(proportion)).onRelease(() -> this.armExtension.setPowerManually(0)).ok()
                .leftTrigger.whileDown(proportion -> this.armExtension.setPowerManually(-proportion)).onRelease(() -> this.armExtension.setPowerManually(0)).ok()
                .rightJoystick.onMove((x, y) -> this.crane.setPowerManually(y)).ok()
                .leftJoystick.onMove((x,  y) -> this.armRotation.setPowerManually(y)).ok()
                .a.onPress(() -> this.claw.toggle()).ok();
=======
        this.x = this.telemetry.addData("x_novel: ", "0");
        this.y = this.telemetry.addData("y_novel: ", "0");
        this.r = this.telemetry.addData("r_novel: ", "0");
        this.extend_length = this.telemetry.addData("Extension: ", 0);
        this.rotation = this.telemetry.addData("Rotation: ", 0);
        this.craneLeft = this.telemetry.addData("Crane Left: ", 0);
        this.craneRight = this.telemetry.addData("Crane Right: ", 0);
>>>>>>> 532e169d62ad9220e9c2a48a590ca1c564a7016d
    }

    @Override
    public void loop() {
<<<<<<< HEAD
=======
        if (Math.abs(this.gamepad2.left_stick_y) < 0.1) {
            this.arm.rotate(0);
        } else {
            this.arm.rotate(this.gamepad2.left_stick_y);
        }

        if (Math.abs(this.gamepad2.right_stick_y) < 0.1) {
            this.crane.runCrane(0);
        } else {
            this.crane.runCrane(this.gamepad2.right_stick_y);
        }

        this.x.setValue(this.odometry.getFieldCentricPose().getX());
        this.y.setValue(this.odometry.getFieldCentricPose().getY());
        this.r.setValue(this.odometry.getFieldCentricPose().getHeading(AngleUnit.DEGREES));
//        this.extend_length.setValue(config.armExtension.motor.getCurrentPosition());
//        this.rotation.setValue(config.armRotation.motor.getCurrentPosition());
        this.extend_length.setValue(config.liftLeft.motor.getCurrentPosition());
        this.rotation.setValue(crane.position);
        this.craneLeft.setValue(config.liftLeft.motor.getPower());
        this.craneRight.setValue(config.liftRight.motor.getPower());

>>>>>>> 532e169d62ad9220e9c2a48a590ca1c564a7016d
        this.telemetry.update();
        this.driver.useGamepad(this.gamepad1, this.g1.x.isToggled() ? 1 : 0.5);
        this.g2.update();
        this.g1.update();
        this.crane.update();
        this.armExtension.update();
        this.armRotation.update();
    }
}
