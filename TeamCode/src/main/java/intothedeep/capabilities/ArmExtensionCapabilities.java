package intothedeep.capabilities;

import intothedeep.SnowballConfig;

import t10.Loop;
import t10.auto.AutoAction;
import t10.motion.hardware.Motor;
import t10.utils.PIDController;

public class ArmExtensionCapabilities implements Loop {
	public static final int POSITION_FULLY_RETRACTED = 0;
	public static final int POSITION_FULLY_EXTENDED = -1700;
	public static final int MAX_ERROR_ALLOWED = 40;
	private final Motor armExtension;
	private final PIDController armExtensionStabilizer;
	private int targetPosition;
	private boolean isManuallyControlled;

	public ArmExtensionCapabilities(SnowballConfig config) {
		this.armExtension = config.armExtension;
		this.isManuallyControlled = true;
		this.armExtensionStabilizer = new PIDController(0.0165, 0, 0);
	}

	@Override
	public void loop() {
		if (!this.isManuallyControlled) {
			double power = armExtensionStabilizer.calculate(
					this.getPosition(),
					this.targetPosition
			) * -1;

			this.setPower(power);
		}
	}

	public int getPosition() {
		return this.armExtension.motor.getCurrentPosition();
	}

	public void setTargetPosition(int targetPosition) {
		this.targetPosition = targetPosition;
		this.isManuallyControlled = false;
	}

	public void setPowerManually(double power) {
		if (power == 0) {
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
