package intothedeep.capabilities;

import intothedeep.Constants;
import intothedeep.SnowballConfig;

import t10.Loop;
import t10.auto.AutoAction;
import t10.motion.hardware.Motor;
import t10.utils.MathUtils;
import t10.utils.PIDController;

/**
 * Capabilities for rotating the arm.
 */
public class ArmRotationCapabilities implements Loop {
	public static final int POSITION_FULLY_DOWNWARDS = -50;
	public static final int POSITION_INSPECTION = 725;
	public static final int POSITION_FULLY_UPWARDS = 788;
	private static final int MAX_ERROR_ALLOWED = 25;
	private final Motor armRotation;
	private final PIDController armRotationStabilizer;
	private int targetPosition;
	private boolean isManuallyControlled;

	public ArmRotationCapabilities(SnowballConfig config) {
		this.armRotation = config.armRotation;
		this.isManuallyControlled = true;
		this.armRotationStabilizer = new PIDController(0.0145, 0, 0);
	}

	@Override
	public void loop() {
		if (!this.isManuallyControlled) {
			double power = this.armRotationStabilizer.calculate(
					this.getPosition(),
					this.targetPosition
			);

			this.setPower(power);
		}
	}

	public void setTargetPosition(int targetPosition) {
		this.targetPosition = targetPosition;
		this.isManuallyControlled = false;
	}

	public static int getTargetPositionAngle(double angleDegrees) {
		return (int) ((angleDegrees / 360) * Constants.TickCounts.LIFT_MOTOR_TICK_COUNT);
	}

	public void setPowerManually(double power) {
		if (Math.abs(power) < 0.1) {
			if (this.isManuallyControlled) {
				this.targetPosition = this.getPosition();
				this.isManuallyControlled = false;
				this.setPower(0);
			}

			return;
		}

		this.isManuallyControlled = true;
		this.setPower(power);
	}

	public boolean isAtTargetPosition() {
		return Math.abs(this.targetPosition - this.getPosition()) < MAX_ERROR_ALLOWED;
	}

	/**
	 * Rotate upwards with a POSITIVE power (ticks increase). Rotate downwards with a NEGATIVE power (ticks decrease).
	 * @param power The power that the arm should rotate at.
	 */
	private void setPower(double power) {
		// TODO: re-implement bounds, if necessary.
		this.armRotation.setPower(power);
	}

    public int getPosition() {
        return this.armRotation.motor.getCurrentPosition();
    }

	public static class ArmRotationAction implements AutoAction {
		private final ArmRotationCapabilities armRotationCapabilities;
		private final int position;

		public ArmRotationAction(ArmRotationCapabilities armRotationCapabilities, int position) {
			this.armRotationCapabilities = armRotationCapabilities;
			this.position = position;
		}

		@Override
		public void init() {
			this.armRotationCapabilities.setTargetPosition(this.position);
		}

		@Override
		public void loop() {
		}

		@Override
		public boolean isComplete() {
			return this.armRotationCapabilities.isAtTargetPosition();
		}
	}
}
