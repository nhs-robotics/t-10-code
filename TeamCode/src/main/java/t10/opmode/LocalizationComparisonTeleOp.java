package t10.opmode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import intothedeep.Constants;
import intothedeep.SnowballConfig;

import t10.bootstrap.BootstrappedOpMode;
import t10.geometry.Pose;
import t10.localizer.MecanumEncodersLocalizer;
import t10.localizer.OdometryCoefficientSet;
import t10.localizer.OdometryIMULocalizer;
import t10.localizer.OdometryLocalizer;
import t10.metrics.Metric;
import t10.motion.hardware.OctoQuadEncoder;
import t10.motion.mecanum.MecanumDriver;

@TeleOp
public class LocalizationComparisonTeleOp extends BootstrappedOpMode {
	private SnowballConfig config;
	private MecanumDriver driver;
	private MecanumEncodersLocalizer mecanumLocalizer;

	@Metric
	public Pose imuPose;

	@Metric
	public Pose mecanumPose;
	private boolean stop = false;
	private UpdateThread ut;

	@Override
	public void init() {
		super.init();
		this.config = new SnowballConfig(this.hardwareMap);
		this.driver = this.config.createMecanumDriver();

		this.mecanumLocalizer = new MecanumEncodersLocalizer(this.driver);

		this.ut = new UpdateThread();
		this.ut.start();
	}

	@Override
	public void loop() {
		this.driver.useGamepad(this.gamepad1, 0.5);
		this.metrics.loop();
	}

	@Override
	public void stop() {
		super.stop();
		this.stop = true;
	}

	public class UpdateThread extends Thread {
		@Override
		public void run() {
			while (!LocalizationComparisonTeleOp.this.stop) {
				LocalizationComparisonTeleOp.this.mecanumLocalizer.loop();

				LocalizationComparisonTeleOp.this.mecanumPose = LocalizationComparisonTeleOp.this.mecanumLocalizer.getFieldCentric();
			}
		}
	}
}
