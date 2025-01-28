package t10.motion.hardware;

import java.security.InvalidParameterException;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import t10.utils.PIDController;

/**
 * A controller for DC motors that allows them to be controlled by setting a target position.
 */
public class PositionalMotor {
	private final DcMotorEx motor;
	private final PIDController pidController;
	private final int minBoundPosition;
	private final int maxBoundPosition;
	private final int initialPosition;
	private int targetPosition;

	/**
	 * Creates a PositionalMotor.
	 *
	 * @param motor            The motor to control.
	 * @param minBoundPosition The minimum inclusive position the motor will be allowed to rotate to.
	 * @param maxBoundPosition The maximum inclusive position the motor will be allowed to rotate to.
	 * @param initialPosition  The initial position of the motor (probably 0).
	 */
	public PositionalMotor(DcMotorEx motor, int minBoundPosition, int maxBoundPosition, int initialPosition, PIDController pidController) {
		if (minBoundPosition >= maxBoundPosition) {
			throw new InvalidParameterException("minBoundRotation must be less than maxBoundRotation");
		}

		this.motor = motor;
		this.minBoundPosition = minBoundPosition;
		this.maxBoundPosition = maxBoundPosition;
		this.initialPosition = initialPosition;
		this.pidController = pidController;
	}

	/**
	 * Sets the target position of the motor in ticks, relative to {@code initialPosition}.
	 * Said target position is the position that the PID will attempt to maintain.
	 *
	 * @param position The position in ticks to set the motor to, relative to the initialPosition.
	 */
	public void setPosition(int position) {
		if (position < this.minBoundPosition) {
			this.setPosition(this.minBoundPosition);
		} else if (position > this.maxBoundPosition) {
			this.setPosition(this.maxBoundPosition);
		} else {
			this.targetPosition = position - this.initialPosition;
		}
	}

	/**
	 * Must be called as frequently as possible, if you want to run the PID.
	 * If you're running the motor to change the position, don't call this or things will be jerky.
	 */
	public void update() {
		this.motor.setPower(
				this.pidController.calculate(
						this.motor.getCurrentPosition(),
						this.targetPosition
				)
		);
	}

	/**
	 * @return The position that you last set the motor to. This is the position the PID is maintaining. This is relative to initialPosition.
	 */
	public int getTargetPosition() {
		return this.targetPosition + initialPosition;
	}

	/**
	 * @return The actual position of the motor in real life. This is relative to initialPosition.
	 */
	public int getPosition() {
		return this.motor.getCurrentPosition() + initialPosition;
	}
}
