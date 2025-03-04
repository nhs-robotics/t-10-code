package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

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

	@Override
	public void init() {
		super.init();
		this.config = new SnowballConfig(this.hardwareMap);
		this.g = new GController(this.gamepad1)
				.dpadUp.onPress(() -> pos += 0.05).ok()
				.dpadDown.onPress(() -> pos -= 0.05).ok();
	}

	@Override
	public void loop() {
		super.loop();
		this.g.loop();
		this.config.clawRotate.setPosition(pos);
	}
}
