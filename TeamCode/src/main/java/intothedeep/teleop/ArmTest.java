package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import intothedeep.SnowballConfig;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import t10.bootstrap.TeleOpOpMode;

@TeleOp
public class ArmTest extends TeleOpOpMode
{
    private SnowballConfig config;
    private Telemetry.Item rotation;
    private Telemetry.Item extension;

    @Override
    public void initialize() {
        this.config = new SnowballConfig(this.hardwareMap);
        this.rotation = this.telemetry.addData("rotation", 0);
        this.extension = this.telemetry.addData("extension", 0);
    }

    @Override
    public void loop() {
        this.config.armRotation.setPower(this.gamepad1.left_stick_y);
        this.config.armExtension.setPower(this.gamepad1.right_stick_y);

        this.rotation.setValue(this.config.armRotation.motor.getCurrentPosition());
        this.extension.setValue(this.config.armExtension.motor.getCurrentPosition());

        this.telemetry.update();
    }
}
