package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import intothedeep.SnowballConfig;
import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.ArmRotationCapabilities;
import intothedeep.capabilities.ClawCapabilities;
import intothedeep.capabilities.CraneCapabilities;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import t10.bootstrap.TeleOpOpMode;

import java.net.HttpCookie;

@TeleOp
public class ArmTest extends TeleOpOpMode
{
    private SnowballConfig config;
    private Telemetry.Item rotation;
    private Telemetry.Item liftLeft, liftRight;
    private ArmRotationCapabilities armRotation;
    private CraneCapabilities crane;
    private ArmExtensionCapabilities armExtension;
    private Telemetry.Item rotationPos;
    private Telemetry.Item liftLeftPos;
    private Telemetry.Item liftRightPos;

    @Override
    public void initialize() {
        this.config = new SnowballConfig(this.hardwareMap);
        this.rotation = this.telemetry.addData("rotation", 0);
        this.liftLeft = this.telemetry.addData("liftLeft", 0);
        this.liftRight = this.telemetry.addData("liftRight", 0);
        this.rotationPos = this.telemetry.addData("rotationPos", 0);
        this.liftLeftPos = this.telemetry.addData("liftLeftPos", 0);
        this.liftRightPos = this.telemetry.addData("liftRightPos", 0);
        this.armRotation = new ArmRotationCapabilities(this.config);
        this.armExtension = new ArmExtensionCapabilities(this.config);
        this.crane = new CraneCapabilities(this.config);
    }

    @Override
    public void loop() {
        this.armRotation.setPowerManually(this.gamepad1.left_stick_y);
        this.armRotation.update();

        if (this.gamepad1.a) {
            this.armRotation.setTargetPosition(ArmRotationCapabilities.POSITION_INSPECTION);
        } else if (this.gamepad1.x) {
            this.armRotation.setTargetPosition(ArmRotationCapabilities.POSITION_FULLY_UPWARDS);
        } else if (this.gamepad1.b) {
            this.armRotation.setTargetPosition(ArmRotationCapabilities.POSITION_FULLY_DOWNWARDS);
        }

        this.crane.setPowerManually(this.gamepad1.right_stick_y);
        this.crane.update();

        this.armExtension.setPowerManually(this.gamepad1.left_stick_x);
        this.armExtension.update();

        this.rotation.setValue(this.config.armRotation.motor.getPower());
        this.liftLeft.setValue(this.config.liftLeft.motor.getPower());
        this.liftRight.setValue(this.config.liftRight.motor.getPower());
        this.rotationPos.setValue(this.config.armRotation.motor.getCurrentPosition());
        this.liftLeftPos.setValue(this.config.liftLeft.motor.getCurrentPosition());
        this.liftRightPos.setValue(this.config.liftRight.motor.getCurrentPosition());

        this.telemetry.update();
    }
}
