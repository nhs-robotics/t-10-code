package intothedeep.capabilities;

import intothedeep.SnowballConfig;
import t10.motion.hardware.Motor;
import t10.utils.MathUtils;
import t10.utils.PIDController;

public class CraneCapabilities {
    public static final int POSITION_BOTTOM = 0;
    public static final int POSITION_LOW_BASKET = 1800;
    public static final int POSITION_HIGH_BASKET = 3450;
    public static final int CRANE_DIFFERENCE_FAIL_SAFE = 120;
    private static final int MAX_ERROR_ALLOWED = 25;
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
        this.stabilizerLeft = new PIDController(0.001, 0, 0);
        this.stabilizerRight = new PIDController(0.001, 0, 0);
    }

    public void update() {
        this.positionLeft = this.liftLeft.motor.getCurrentPosition();
        this.positionRight = this.liftLeft.motor.getCurrentPosition();

        if (!this.isManuallyControlled) {
            // Left
            double powerLeft = this.stabilizerLeft.calculate(
                    this.positionLeft,
                    this.targetPosition
            );

            this.setPower(powerLeft, this.positionLeft, this.liftLeft);

            // Right
            double powerRight = this.stabilizerRight.calculate(
                    this.positionRight,
                    this.targetPosition
            );

            this.setPower(powerRight, this.positionRight, this.liftRight);
        }

        if (Math.abs(this.positionLeft - this.positionRight) >= CRANE_DIFFERENCE_FAIL_SAFE) {
            throw new RuntimeException("Difference between left and right lifts is too high! Stopping!");
        }
    }

    public void setTargetPosition(int targetPosition) {
        this.targetPosition = targetPosition;
        this.isManuallyControlled = false;
    }

    public void setPowerManually(double power) {
        if (power == 0) {
            if (this.isManuallyControlled) {
                this.targetPosition = MathUtils.average(this.positionLeft, this.positionRight);
                this.isManuallyControlled = false;
                this.setPower(0, this.positionLeft, this.liftLeft);
                this.setPower(0, this.positionRight, this.liftRight);
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

    private void setPower(double power, int position, Motor motor) {
        // If the power is less than 30%, then just stop the motor. This helps to conserve power
        // because 30% power or less typically will not be able to lift the crane, and that
        // power is therefore wasted.
        if (Math.abs(power) < 0.3 && power != 0) {
            motor.setPower(0);
            return;
        }

        // TODO: re-implement bounds.
//        if (power < 0 && position > POSITION_HIGH_BASKET) {
//            // This would over-retract the motor. Stop.
//            motor.setPower(0);
//            return;
//        }
//
//        if (power > 0 && position < POSITION_BOTTOM) {
//            // This would over-extend the motor. Stop
//            motor.setPower(0);
//            return;
//        }

        motor.setPower(power);
    }
}