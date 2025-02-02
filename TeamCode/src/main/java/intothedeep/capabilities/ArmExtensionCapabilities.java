package intothedeep.capabilities;

import intothedeep.SnowballConfig;

import t10.Loop;
import t10.auto.AutoAction;
import t10.motion.hardware.Motor;

/**
 * Capabilities for extending the arm.
 * <h1>Power > 0</h1>
 * <li>RETRACTS</li>
 * <li>Position (Ticks) INCREASES</li>
 *
 * <h1>Power < 0</h1>
 * <li>EXTENDS</li>
 * <li>Position (Ticks) DECREASES</li>
 */
public class ArmExtensionCapabilities implements Loop {
	public static final int POSITION_FULLY_RETRACTED = 0;
	public static final int POSITION_FULLY_EXTENDED = -6000;
	public static final int MAX_ERROR_ALLOWED = 100;
	private final Motor armExtension;
	private int targetPosition;
	private int position;
	private boolean isManuallyControlled;

	public ArmExtensionCapabilities(SnowballConfig config) {
		this.armExtension = config.armExtension;
		this.isManuallyControlled = true;
	}

	@Override
	public void loop() {
		this.position = this.armExtension.motor.getCurrentPosition();

		if (!this.isManuallyControlled) {
			double error = this.targetPosition - this.position;
			double power = error * 0.01;
			this.setPower(power);
		}
	}

	public int getPosition() {
		return position;
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
		if (Math.abs(power) < 0.25) {
			this.armExtension.setPower(0);
			return;
		}

		this.armExtension.setPower(power);
	}

    public static class ArmExtensionAction implements AutoAction {
        private final ArmExtensionCapabilities armExtensionCapabilities;
        private final int position;

        public ArmExtensionAction(ArmExtensionCapabilities armExtensionCapabilities, int position) {
            this.armExtensionCapabilities = armExtensionCapabilities;
            this.position = position;
        }

        @Override
        public void init() {
            this.armExtensionCapabilities.setTargetPosition(this.position);
        }

        @Override
        public void loop() {
        }

        @Override
        public boolean isComplete() {
            return this.armExtensionCapabilities.isAtTargetPosition();
        }
    }
}
