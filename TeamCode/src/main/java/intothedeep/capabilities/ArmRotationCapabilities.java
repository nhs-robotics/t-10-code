package intothedeep.capabilities;

import intothedeep.SnowballConfig;

import t10.motion.hardware.Motor;
import t10.utils.PIDController;

/**
 * Capabilities for rotating the arm.
 *
 * <h1>Power > 0</h1>
 * <li>Rotate UPWARDS</li>
 * <li>Position (Ticks) INCREASES</li>
 *
 * <h1>Power < 0</h1>
 * <li>Rotate DOWNWARDS</li>
 * <li>Position (Ticks) DECREASES</li>
 */
public class ArmRotationCapabilities {
	public static final int POSITION_FULLY_DOWNWARDS = -50;
	public static final int POSITION_INSPECTION = 725;
	public static final int POSITION_FULLY_UPWARDS = 788;
	private static final int MAX_ERROR_ALLOWED = 25;
	private final Motor armRotation;
	private final PIDController armRotationStabilizer;
	private int targetPosition;
	private int position;
	private boolean isManuallyControlled;

	public ArmRotationCapabilities(SnowballConfig config) {
		this.armRotation = config.armRotation;
		this.isManuallyControlled = true;
		this.armRotationStabilizer = new PIDController(0.01, 0, 0);
	}

	public void update() {
		this.position = this.armRotation.motor.getCurrentPosition();

		if (!this.isManuallyControlled) {
			double power = this.armRotationStabilizer.calculate(
					this.position,
					this.targetPosition
			);

			this.setPower(power);
		}
	}

	public void setTargetPosition(int targetPosition) {
		this.targetPosition = targetPosition;
		this.isManuallyControlled = false;
	}

	public void setPowerManually(double power) {
		if (power == 0) {
			if (this.isManuallyControlled) {
				this.targetPosition = this.position;
				this.isManuallyControlled = false;
				this.setPower(0);
			}

			return;
		}

		this.isManuallyControlled = true;
		this.setPower(power);
	}

	public boolean isAtTargetPosition() {
		return Math.abs(this.targetPosition - this.position) < MAX_ERROR_ALLOWED;
	}

	private void setPower(double power) {
		// TODO: re-implement bounds, if necessary.
		this.armRotation.setPower(power);
	}
}
