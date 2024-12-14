package t10.motion.hardware;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import java.security.InvalidParameterException;

public class PositionalMotor {
    private final DcMotorEx motor;
    private final double ticksPerRevolution;
    private final double startPosition;
    private final double endPosition;
    private final double speed;
    private double currentPosition;
    private int coefficient;
    private double targetPosition;
    private double startTicksPosition;
    private double positionBeforeMovement;

    public PositionalMotor(DcMotorEx motor, double ticksPerRevolution, double startPosition, double endPosition, double speed, int coefficient) {
        if (startPosition >= endPosition) {
            throw new InvalidParameterException("startPosition must be less than endPosition");
        }

        this.motor = motor;
        this.ticksPerRevolution = ticksPerRevolution;
        this.startPosition = startPosition;
        this.currentPosition = startPosition;
        this.endPosition = endPosition;
        this.coefficient = coefficient;
        this.speed = speed;
    }

    public void setPosition(double position) {
        if (position < startPosition || position > endPosition) {
            throw new InvalidParameterException("Position is not within bounds [" + startPosition + "," + endPosition + "]");
        }

        this.targetPosition = position;
        this.startTicksPosition = motor.getCurrentPosition();
        this.positionBeforeMovement = currentPosition;
    }

    public void update() {
        double revolutions = targetPosition - currentPosition;
        double ticks = revolutions * ticksPerRevolution;

        motor.setVelocity(ticks * coefficient * speed);

        currentPosition = ((motor.getCurrentPosition() - startTicksPosition) / ticksPerRevolution) + positionBeforeMovement;
    }

    public double getPosition() {
        return currentPosition;
    }
}
