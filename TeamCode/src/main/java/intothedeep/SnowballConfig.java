package intothedeep;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.digitalchickenlabs.OctoQuad;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import t10.bootstrap.AbstractRobotConfiguration;
import t10.bootstrap.Hardware;
import t10.bootstrap.PinPointHardware;
import t10.geometry.Pose;
import t10.localizer.Localizer;
import t10.localizer.PinPointLocalizer;
import t10.motion.hardware.Motor;
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

	@Hardware(name = "ClawRotate")
	public Servo clawRotate;

	@Hardware(name = "ClawGrip")
	public Servo clawGrip;

	@Hardware(name = "PinPoint")
	public PinPointHardware pinPoint;

	public SnowballConfig(HardwareMap hardwareMap) {
		super(hardwareMap);

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
		return new PinPointLocalizer(
				pinPoint,
				0.32/25.4,
				PinPointHardware.EncoderDirection.FORWARD,
				-48/25.4,
				PinPointHardware.EncoderDirection.REVERSED,
				PinPointHardware.GoBildaOdometryPods.goBILDA_4_BAR_POD
		);
	}
}

