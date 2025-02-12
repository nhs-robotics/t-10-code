package t10.opmode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import intothedeep.Constants;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import t10.bootstrap.AbstractRobotConfiguration;
import t10.bootstrap.BootstrappedOpMode;
import t10.bootstrap.Hardware;
import t10.gamepad.GController;
import t10.localizer.Localizer;
import t10.motion.hardware.Motor;
import t10.motion.mecanum.MecanumDriver;

@TeleOp
public class TestMotor extends BootstrappedOpMode {
	private Config config;
	private GController g1;
	private Telemetry.Item Position, Power;
	private double power = 0.1;
	private Motor motor;

	@Override
	public void init() {
		super.init();
		this.config = new Config(this.hardwareMap);
		this.motor = config.motor;

		// Gamepad
		// G1 controls the robot's movement.
		this.g1 = new GController(this.gamepad1)
				.dpadUp.onPress(() -> motor.setPower(-power)).onRelease(() -> motor.setPower(0)).ok()
				.dpadDown.onPress(() -> motor.setPower(power)).onRelease(() -> motor.setPower(0)).ok()
				.rightBumper.onPress(() -> power += 0.1).ok()
				.leftBumper.onPress(() -> power -= 0.1).ok();
		this.Position = telemetry.addData("Position: ", 0);
		this.Power = telemetry.addData("Power: ", 0);
	}

	@Override
	public void loop() {
		Position.setValue(motor.motor.getCurrentPosition());
		Power.setValue(power);
		this.g1.loop();
		this.telemetry.update();
	}
}

class Config extends AbstractRobotConfiguration {
	@Hardware(
			name = "motor",
			diameterIn = Constants.Robot.ACTUAL_DIAMETER_IN,
			ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT,
			zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
	)
	public Motor motor;

	Config(HardwareMap hardwareMap) {
		super(hardwareMap);
	}

	@Override
	public MecanumDriver createMecanumDriver() {
		return null;
	}

	@Override
	public Localizer<?> createLocalizer() {
		return null;
	}
}
