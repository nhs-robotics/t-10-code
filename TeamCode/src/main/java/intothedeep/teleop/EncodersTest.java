package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import intothedeep.SnowballConfig;

import t10.bootstrap.TeleOpOpMode;
import t10.localizer.mecanum.MecanumEncodersLocalizer;
import t10.motion.mecanum.MecanumDriver;

@TeleOp
public class EncodersTest extends TeleOpOpMode {
	private SnowballConfig config;
	private MecanumDriver driver;
	private MecanumEncodersLocalizer localizer;

	@Override
	public void initialize() {
		this.config = new SnowballConfig(this.hardwareMap);
		this.driver = this.config.createMecanumDriver();
		this.localizer = new MecanumEncodersLocalizer(this.driver);
	}

	@Override
	public void loop() {
		this.localizer.update();
	}
}
