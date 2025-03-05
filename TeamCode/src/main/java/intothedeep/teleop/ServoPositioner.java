package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import intothedeep.SnowballConfig;

import t10.bootstrap.BootstrappedOpMode;
import t10.gamepad.GController;
import t10.metrics.Metric;

@TeleOp
public class ServoPositioner extends BootstrappedOpMode {
	private SnowballConfig config;
	private GController g;

	@Metric
	public double pos = 0;

	Telemetry.Item pos_t;

	@Override
	public void init() {
		super.init();
		this.config = new SnowballConfig(this.hardwareMap);
		this.g = new GController(this.gamepad1)
				.dpadUp.onPress(() -> pos += 0.05).ok()
				.dpadDown.onPress(() -> pos -= 0.05).ok()
				.rightBumper.onPress(() -> pos += 0.01).ok()
				.leftBumper.onPress(() -> pos -= 0.01).ok();
		pos_t = this.telemetry.addData("Position: ", 0);
	}

	@Override
	public void loop() {
		super.loop();
		this.g.loop();
		this.config.clawRotate.setPosition(pos);
		pos_t.setValue(pos);
	}
}
