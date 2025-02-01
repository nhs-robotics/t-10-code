package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import intothedeep.Constants;
import intothedeep.SnowballConfig;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.bootstrap.TeleOpOpMode;
import t10.geometry.Pose;
import t10.localizer.MecanumEncodersLocalizer;
import t10.localizer.OdometryCoefficientSet;
import t10.localizer.OdometryIMULocalizer;
import t10.localizer.OdometryLocalizer;
import t10.metrics.Metric;
import t10.motion.hardware.OctoQuadEncoder;
import t10.motion.mecanum.MecanumDriver;

@TeleOp
public class LocalizerTest extends TeleOpOpMode {
	private SnowballConfig config;
	private MecanumDriver driver;

	@Metric
	public Pose mecanumPose;
	@Metric
	public Pose odometryImuPose;
	@Metric
	public Pose odometryPose;

	private MecanumEncodersLocalizer mecanumLocalizer;
	private OdometryLocalizer odometryLocalizer;
	private OdometryIMULocalizer odometryIMULocalizer;

	@Override
	public void initialize() {
		this.config = new SnowballConfig(this.hardwareMap);
		this.driver = this.config.createMecanumDriver();
		this.mecanumLocalizer = new MecanumEncodersLocalizer(this.driver);
		this.odometryLocalizer = new OdometryLocalizer(
				new OdometryCoefficientSet(1, 1, -1),
				// 4-6-5 is right-left-perpendicular
				new OctoQuadEncoder(this.config.octoQuad, 4, Constants.Odometry.ODOMETRY_WHEEL_DIAMETER_IN, Constants.Odometry.TICKS_PER_ODOMETRY_REVOLUTION),
				new OctoQuadEncoder(this.config.octoQuad, 6, Constants.Odometry.ODOMETRY_WHEEL_DIAMETER_IN, Constants.Odometry.TICKS_PER_ODOMETRY_REVOLUTION),
				new OctoQuadEncoder(this.config.octoQuad, 5, Constants.Odometry.ODOMETRY_WHEEL_DIAMETER_IN, Constants.Odometry.TICKS_PER_ODOMETRY_REVOLUTION),
				11.5,
				-6.5
		);
		this.odometryIMULocalizer = this.config.createLocalizer();
		this.mecanumLocalizer.setFieldCentric(new Pose(0, 0, 0, AngleUnit.DEGREES));
		this.odometryLocalizer.setFieldCentric(new Pose(0, 0, 0, AngleUnit.DEGREES));
		this.odometryIMULocalizer.setFieldCentric(new Pose(0, 0, 0, AngleUnit.DEGREES));
	}

	@Override
	public void loop() {
		this.driver.useGamepad(this.gamepad1, 0.75);
		this.mecanumLocalizer.loop();
		this.odometryLocalizer.loop();
		this.odometryIMULocalizer.loop();
		this.metrics.loop();
		this.mecanumPose = this.mecanumLocalizer.getFieldCentric();
		this.odometryImuPose = this.odometryIMULocalizer.getFieldCentric();
		this.odometryPose = this.odometryLocalizer.getFieldCentric();
	}
}
