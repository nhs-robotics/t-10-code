package t10.motion.hardware;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import java.security.InvalidParameterException;

import t10.utils.PIDController;

/**
 * A controller for DC motors that allows them to be controlled by setting a target position
 */
public class PositionalMotor {
    private final DcMotorEx motor;
    private final PIDController pidController;
    private final int minBoundPosition;
    private final int maxBoundPosition;
    private final int initialPosition;
    private int targetPosition;
    public int coefficient;

    /**
     * Creates a PositionalMotor
     *
     * @param motor            the motor to control
     * @param minBoundPosition the minimum inclusive position the motor will be allowed to rotate to
     * @param maxBoundPosition the maximum inclusive position the motor will be allowed to rotate to
     * @param initialPosition  the initial position of the motor (probably 0)
     */
    public PositionalMotor(DcMotorEx motor, int minBoundPosition, int maxBoundPosition, int initialPosition, PIDController pidController, int coefficient) {
        if (minBoundPosition >= maxBoundPosition) {
            throw new InvalidParameterException("minBoundRotation must be less than maxBoundRotation");
        }

        this.motor = motor;
        this.minBoundPosition = minBoundPosition;
        this.maxBoundPosition = maxBoundPosition;
        this.initialPosition = initialPosition;
        this.pidController = pidController;
        this.coefficient = coefficient;
    }


    /**
     * Sets the target position of the motor in ticks, relative to {@code initialPosition}.
     * Said target position is the position that the PID will attempt to maintain.
     *
     * @param position The position in ticks to set the motor to, relative to the initialPosition.
     * @param overrideBounds Whether or not the boundaries should be ignored
     */
    public void setTargetPosition(int position, boolean overrideBounds) {
        if (!overrideBounds && (position < this.minBoundPosition || position > this.maxBoundPosition)) {
            throw new IllegalArgumentException(
                    String.format(
                            "Position (%d) is outside bounds [%d, %d]",
                            position,
                            this.minBoundPosition,
                            this.maxBoundPosition
                    )
            );
        }

        this.targetPosition = position * coefficient - this.initialPosition;
    }

    /**
     * Must be called as frequently as possible, if you want to run the PID.
     * If you're running the motor to change the position, don't call this or things will be jerky.
     */
    public void update() {
        this.motor.setPower(
                this.pidController.calculate(
                        this.getPosition(),
                        this.getTargetPosition()
                ) * this.coefficient
        );
    }

    /**
     * @return The position that you last set the motor to.
     * This is the position the PID is maintaining.
     * This is relative to initialPosition.
     */
    public int getTargetPosition() {
        return (this.targetPosition + initialPosition) * coefficient;
    }

    /**
     * @return The actual position of the motor in real life.
     * This is relative to initialPosition.
     */
    public int getPosition() {
        return (this.motor.getCurrentPosition() * this.coefficient) + initialPosition;
    }

    /**
     * @return The number of encoder ticks between the current position and the target position
     */
    public int distToPosition()
    {
        return getTargetPosition() - getPosition();
    }
}
