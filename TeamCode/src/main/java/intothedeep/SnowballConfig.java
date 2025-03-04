package intothedeep;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.digitalchickenlabs.OctoQuad;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import t10.bootstrap.AbstractRobotConfiguration;
import t10.bootstrap.Hardware;
import t10.geometry.Pose;
import t10.localizer.Localizer;
import t10.localizer.OdometryCoefficientSet;
import t10.localizer.OdometryIMULocalizerFast;
import t10.localizer.OdometryLocalizerFast;
import t10.motion.hardware.Motor;
import t10.motion.mecanum.MecanumDriver;
import t10.vision.Webcam;

public class SnowballConfig extends AbstractRobotConfiguration {
	@Hardware(
			name = "FL",
			diameterIn = Constants.Robot.ACTUAL_DIAMETER_IN,
			ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT,
			zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
	)
	public Motor fl;

	@Hardware(
			name = "FR",
			diameterIn = Constants.Robot.ACTUAL_DIAMETER_IN,
			ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT,
			zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
	)
	public Motor fr;

	@Hardware(
			name = "BL",
			diameterIn = Constants.Robot.ACTUAL_DIAMETER_IN,
			ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT,
			zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
	)
	public Motor bl;

	@Hardware(
			name = "BR",
			diameterIn = Constants.Robot.ACTUAL_DIAMETER_IN,
			ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT,
			zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
	)
	public Motor br;

	@Hardware(name = "imu")
	public BNO055IMU imu;

	@Hardware(
			name = "LiftRight",
			ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT
	)
	public Motor liftRight;

	@Hardware(
			name = "LiftLeft",
			ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT
	)
	public Motor liftLeft;

	@Hardware(
			name = "ArmExtension",
			ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT
	)
	public Motor armExtension;

	@Hardware(
			name = "ArmRotation",
			ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT
	)
	public Motor armRotation;

	@Hardware(name = "OctoQuad")
	public OctoQuad octoQuad;

	@Hardware(name = "ClawRotate")
	public Servo clawRotate;

	@Hardware(name = "ClawGrip")
	public Servo clawGrip;

	@Hardware(name = "Webcam")
	public Webcam webcam;

	public SnowballConfig(HardwareMap hardwareMap) {
		super(hardwareMap);

		this.imu.initialize(Constants.IMU_PARAMETERS);
	}

	@Override
	public MecanumDriver createMecanumDriver() {
		return new MecanumDriver(
				this.fl,
				this.fr,
				this.bl,
				this.br,
				Constants.Coefficients.SNOWBALL_COEFFICIENTS
		);
	}

	@Override
	public Localizer<Pose> createLocalizer() {
		return new OdometryIMULocalizerFast(
				new OdometryCoefficientSet(1, 1, -1),
				octoQuad,
				4,
				6,
				5,
				11.5,
				-6.5,
				Constants.Odometry.TICKS_PER_ODOMETRY_REVOLUTION,
				Constants.Odometry.ODOMETRY_WHEEL_DIAMETER_IN,
				this.imu
		);
	}
}
