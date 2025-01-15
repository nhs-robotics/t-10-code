package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import intothedeep.SnowballConfig;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import t10.bootstrap.TeleOpOpMode;

@TeleOp
public class CraneTest extends TeleOpOpMode
{
    private SnowballConfig config;
    private Telemetry.Item liftRightPower;
    private Telemetry.Item liftLeftPower;

    @Override
    public void initialize() {
        this.config = new SnowballConfig(this.hardwareMap);
        this.liftRightPower = this.telemetry.addData("liftRightPower", 0);
        this.liftLeftPower = this.telemetry.addData("liftLeftPower", 0);
    }

    @Override
    public void loop() {
        this.config.liftRight.setPower(this.gamepad1.left_stick_y);
        this.config.liftLeft.setPower(this.gamepad1.left_stick_y);

        this.liftRightPower.setValue(this.config.liftRight.motor.getPower());
        this.liftLeftPower.setValue(this.config.liftLeft.motor.getPower());
    }
}
