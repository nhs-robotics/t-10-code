package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import intothedeep.ArmCapabilities;
import intothedeep.ClawCapabilities;
import intothedeep.CraneCapabilities;
import intothedeep.SnowballConfig;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.localizer.Localizer;
import t10.localizer.odometry.OdometryLocalizer;
import t10.motion.mecanum.MecanumDriver;

@TeleOp
public class CompetitionTeleOp extends TeleOpOpMode {
    protected SnowballConfig config;
    protected CraneCapabilities crane;
    private ArmCapabilities arm;
    protected GController g1;
    protected GController g2;
    private MecanumDriver driver;
    private OdometryLocalizer odometry;
    private ClawCapabilities clawCapabilities;

    private Telemetry.Item x;
    private Telemetry.Item y;
    private Telemetry.Item r;
    private Telemetry.Item diffMotor, extension, rotation, craneLeft, craneRight;
    private boolean ignoreBounds = false, microMove = false;

    @Override
    public void initialize() {
        this.config = new SnowballConfig(this.hardwareMap);
        this.crane = new CraneCapabilities(this.config);
        this.arm = new ArmCapabilities(this.config);
        this.clawCapabilities = new ClawCapabilities(this.config);
        this.g2 = new GController(this.gamepad2)
                .dpadUp.onPress(() -> this.arm.extendSafe(1, ignoreBounds)).onRelease(() -> this.arm.extendSafe(0, ignoreBounds)).ok()
                .dpadDown.onPress(() -> this.arm.extendSafe(-1, ignoreBounds)).onRelease(() -> this.arm.extendSafe(0, ignoreBounds)).ok()
                .a.onPress(() -> this.clawCapabilities.toggle()).ok();
        this.g1 = new GController(this.gamepad1)
                .x.onToggleOn(() -> microMove = true).onToggleOff(() -> microMove = false).ok()
                .a.onToggleOn(() -> ignoreBounds = true).onToggleOff(() -> ignoreBounds = false).ok();
        this.driver = this.config.createMecanumDriver();
        this.odometry = config.createOdometry();

        this.config.liftLeft.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.config.liftRight.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.config.armRotation.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.config.armExtension.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        this.x = this.telemetry.addData("x_novel: ", "0");
        this.y = this.telemetry.addData("y_novel: ", "0");
        this.r = this.telemetry.addData("r_novel: ", "0");
        this.extension = this.telemetry.addData("Extension: ", 0);
        this.rotation = this.telemetry.addData("Rotation: ", 0);
        this.craneLeft = this.telemetry.addData("Crane Left: ", 0);
        this.craneRight = this.telemetry.addData("Crane Right: ", 0);
    }

    @Override
    public void loop() {
        if (Math.abs(this.gamepad2.left_stick_y) < 0.1) {
            this.arm.rotateSafe(0, ignoreBounds);
        } else {
            this.arm.rotateSafe(this.gamepad2.left_stick_y, ignoreBounds);
        }

        if (Math.abs(this.gamepad2.right_stick_y) < 0.1) {
            this.crane.runCrane(0, ignoreBounds);
        } else {
            this.crane.runCrane(this.gamepad2.right_stick_y, ignoreBounds);
        }

        this.x.setValue(this.odometry.getFieldCentricPose().getX());
        this.y.setValue(this.odometry.getFieldCentricPose().getY());
        this.r.setValue(this.odometry.getFieldCentricPose().getHeading(AngleUnit.DEGREES));
        this.extension.setValue(config.armExtension.motor.getCurrentPosition());
        this.rotation.setValue(config.armRotation.motor.getCurrentPosition());
        this.craneLeft.setValue(config.liftLeft.motor.getCurrentPosition());
        this.craneRight.setValue(config.liftRight.motor.getCurrentPosition());
        updateAll();
    }

    private void updateAll()
    {
        telemetry.update();
        odometry.update();
        this.driver.useGamepad(this.gamepad1, 1);
        this.g2.update();
        this.g1.update();
        this.crane.update();
        this.arm.update();
    }
}
