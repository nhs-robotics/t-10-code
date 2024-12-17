package t10.motion.hardware;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import java.security.InvalidParameterException;

import t10.utils.PIDController;

/**
* A controller for DC motors that allows them to be controlled by setting a target position
 */
public class PositionalMotor {
    private final DcMotorEx motor;
    private final PIDController pid;
    private final float offsetTicks;
    private final float ticksPerRotation;
    private final float minBoundTicks;
    private final float maxBoundTicks;
    private final float speed;
    private float currentPositionTicks = 0;
    private float targetTicks;

    /**
     * Creates a PositionalMotor
     * @param motor the motor to control
     * @param ticksPerRotation the motor's ticks for one full revolution
     * @param minBoundRotation the minimum inclusive rotation, in rotations, the motor will be allowed to rotate to (relative to initial position)
     * @param maxBoundRotation the maximum inclusive rotation, in rotations, the motor will be allowed to rotate to (relative to initial position)
     * @param initialRotation the initial position, in rotations, of the motor
     * @param speed the speed for the motor to rotate at (can be negative for inverted rotation)
     */
    public PositionalMotor(DcMotorEx motor, float ticksPerRotation, float minBoundRotation, float maxBoundRotation, float initialRotation, float speed) {
        if (minBoundRotation >= maxBoundRotation) {
            throw new InvalidParameterException("minBoundRotation must be less than maxBoundRotation");
        }

        this.motor = motor;
        this.ticksPerRotation = ticksPerRotation;
        this.minBoundTicks = rotationToTicks(minBoundRotation);
        this.maxBoundTicks = rotationToTicks(maxBoundRotation);
        this.currentPositionTicks = rotationToTicks(initialRotation);
        this.offsetTicks = rotationToTicks(initialRotation);

        this.speed = speed;

        this.pid = new PIDController(0.1, 0, 0);
    }

    private float rotationToTicks(float rotation) {
        return rotation * ticksPerRotation;
    }

    private float ticksToRotation(float ticks) {
        return ticks / ticksPerRotation;
    }

    public void setPosition(float rotation) {
        if (rotation < ticksToRotation(minBoundTicks) || rotation > ticksToRotation(maxBoundTicks)) {
            throw new InvalidParameterException("Position is not within bounds [" + ticksToRotation(minBoundTicks) + "," + ticksToRotation(maxBoundTicks) + "]");
        }

        this.targetTicks = rotationToTicks(rotation);
    }

    public void update() {
        float ticks = targetTicks - currentPositionTicks;

        motor.setVelocity(ticks * speed);

        currentPositionTicks = motor.getCurrentPosition() + offsetTicks;
    }

    public float getPosition() {
        return ticksToRotation(currentPositionTicks);
    }
}
