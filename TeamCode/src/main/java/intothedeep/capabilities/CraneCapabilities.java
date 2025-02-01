package intothedeep.capabilities;

import intothedeep.SnowballConfig;

import t10.Loop;
import t10.motion.hardware.Motor;
import t10.utils.MathUtils;
import t10.utils.PIDController;

public class CraneCapabilities implements Loop {
	public static final int POSITION_BOTTOM = 0;
	public static final int POSITION_LOW_BASKET = 1800;
	public static final int POSITION_HIGH_BASKET = 3450;
	public static final int CRANE_DIFFERENCE_FAIL_SAFE = 760 * 10 ^ 5;
	public static final int POSITION_HIGH_CHAMBER = 2892;
	private static final int MAX_ERROR_ALLOWED = 30;
	private final Motor liftLeft;
	private final Motor liftRight;
	private final PIDController stabilizerLeft;
	private final PIDController stabilizerRight;
	private int positionLeft;
	private int positionRight;
	private int targetPosition;
	private boolean isManuallyControlled;

	public CraneCapabilities(SnowballConfig config) {
		this.targetPosition = 0;
		this.liftLeft = config.liftLeft;
		this.liftRight = config.liftRight;
		this.isManuallyControlled = true;
		this.stabilizerLeft = new PIDController(0.01, 0, 0);
		this.stabilizerRight = new PIDController(0.01, 0, 0);
	}

	@Override
	public void loop() {
		this.positionLeft = this.liftLeft.motor.getCurrentPosition();
		this.positionRight = this.liftRight.motor.getCurrentPosition();

		if (!this.isManuallyControlled) {
			// DON'T ASK about the negative coefficients on the powers. THEY JUST WORK. ACCEPT IT.
			// Left
			double powerLeft = this.stabilizerLeft.calculate(
					this.positionLeft,
					this.targetPosition
			);

			// Right
			double powerRight = this.stabilizerRight.calculate(
					this.positionRight,
					this.targetPosition
			);

			this.setPower(-powerLeft, this.positionLeft, this.liftLeft);
			this.setPower(-powerRight, this.positionRight, this.liftRight);
		}

		if (Math.abs(this.positionLeft - this.positionRight) >= CRANE_DIFFERENCE_FAIL_SAFE) {
			throw new RuntimeException("Difference between left and right lifts is too high! Stopping!");
		}
	}

	public void setTargetPosition(int targetPosition) {
		this.targetPosition = targetPosition;
		this.isManuallyControlled = false;
	}

	public int getPositionLeft() {
		return positionLeft;
	}

	public int getPositionRight() {
		return positionRight;
	}

	public int getTargetPosition() {
		return targetPosition;
	}

	public void setPowerManually(double power) {
		if (power == 0) {
			if (this.isManuallyControlled) {
				this.targetPosition = MathUtils.average(this.positionLeft, this.positionRight);
				this.isManuallyControlled = false;
				this.liftLeft.setPower(0);
				this.liftRight.setPower(0);
			}

			return;
		}

		this.isManuallyControlled = true;
		this.setPower(power, this.positionLeft, this.liftLeft);
		this.setPower(power, this.positionRight, this.liftRight);
	}

	public boolean isAtTargetPosition() {
		return Math.abs(this.targetPosition - this.positionLeft) < MAX_ERROR_ALLOWED && Math.abs(this.targetPosition - this.positionRight) < MAX_ERROR_ALLOWED;
	}

	// TODO: re-implement bounds, if necessary.
	private void setPower(double power, int position, Motor motor) {
		// If the power is less than 30%, then just stop the motor. This helps to conserve power
		// because 30% power or less typically will not be able to lift the crane, and that
		// power is therefore wasted.
		if (Math.abs(power) < 0.2) {
			motor.setPower(0);
			return;
		}

		motor.setPower(power);
	}
}
