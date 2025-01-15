package intothedeep.capabilities;

import intothedeep.SnowballConfig;
import t10.motion.hardware.Motor;
import t10.motion.hardware.PositionalMotor;
import t10.utils.PIDController;

/**
 * <h1>Power > 0</h1>
 * <li>RETRACTS</li>
 * <li>Position (Ticks) INCREASES</li>
 *
 * <h1>Power < 0</h1>
 * <li>EXTENDS</li>
 * <li>Position (Ticks) DECREASES</li>
 */
public class ArmExtensionCapabilities {
    private final Motor armExtension;
    private int targetPosition;
    private int position;
    private boolean isManuallyControlled;

    public ArmExtensionCapabilities(SnowballConfig config) {
        this.armExtension = config.armExtension;
        this.isManuallyControlled = true;
    }

    public void update() {
        this.position = this.armExtension.motor.getCurrentPosition();

        if (!this.isManuallyControlled) {
            double error = this.targetPosition - this.position;
            double power = -error * 0.01;

            this.armExtension.motor.setPower(power);
        }
    }
}
