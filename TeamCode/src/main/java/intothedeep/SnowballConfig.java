package intothedeep;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.digitalchickenlabs.OctoQuad;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import t10.bootstrap.AbstractRobotConfiguration;
import t10.bootstrap.Hardware;
import t10.localizer.OdometryCoefficientSet;
import t10.localizer.OdometryIMULocalizer;
import t10.motion.hardware.Motor;
import t10.motion.hardware.OctoQuadEncoder;
import t10.motion.mecanum.MecanumDriver;

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

    @Hardware(name = "Grabber")
    public Servo clawServo;

    @Hardware(name = "ClawTwist")
    public Servo clawTwist;

    @Hardware(name = "ClawRotate")
    public Servo clawRotate;

	@Hardware(name = "ClawGrip")
	public Servo clawGrip;

	public SnowballConfig(HardwareMap hardwareMap) {
		super(hardwareMap);

		BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
		parameters.accelRange = BNO055IMU.AccelRange.G2;
		parameters.accelBandwidth = BNO055IMU.AccelBandwidth.HZ125;
		parameters.gyroRange = BNO055IMU.GyroRange.DPS2000;
		parameters.gyroBandwidth = BNO055IMU.GyroBandwidth.HZ230;
		parameters.calibrationDataFile = "BNO055IMUCalibration.json";
		this.imu.initialize(parameters);
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
	public OdometryIMULocalizer createLocalizer() {
		return new OdometryIMULocalizer(
				new OdometryCoefficientSet(1, 1, -1),
				// 4-6-5 is right-left-perpendicular
				new OctoQuadEncoder(octoQuad, 4, Constants.Odometry.ODOMETRY_WHEEL_DIAMETER_IN, Constants.Odometry.TICKS_PER_ODOMETRY_REVOLUTION),
				new OctoQuadEncoder(octoQuad, 6, Constants.Odometry.ODOMETRY_WHEEL_DIAMETER_IN, Constants.Odometry.TICKS_PER_ODOMETRY_REVOLUTION),
				new OctoQuadEncoder(octoQuad, 5, Constants.Odometry.ODOMETRY_WHEEL_DIAMETER_IN, Constants.Odometry.TICKS_PER_ODOMETRY_REVOLUTION),
				11.5,
				-6.5,
				this.imu
		);
	}
}
