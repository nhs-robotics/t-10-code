package intothedeep;

import android.icu.text.Transliterator;

import t10.motion.hardware.PositionalMotor;
import t10.utils.PIDController;

public class CraneCapabilities {
    private final SnowballConfig c;
    private final PIDController armRotationStabilizer;
    private final PIDController craneStabilizer;
    private final PositionalMotor liftLeft;
    private final PositionalMotor liftRight;
    private int armRotationTarget;
    private int craneTarget;
    private boolean runningCrane = false;
    private boolean runningRotation = false;
    private boolean shouldUpdatePositionalMotors = false;

    public CraneCapabilities(SnowballConfig c) {
        this.c = c;
        this.armRotationStabilizer = new PIDController(0.05, 0, 0);
        this.craneStabilizer = new PIDController(0.01, 0, 0);

        liftLeft = new PositionalMotor(this.c.liftLeft.motor, 0, Constants.TickCounts.CRANE_MAX, 0, craneStabilizer, 1);
        liftRight = new PositionalMotor(this.c.liftLeft.motor, 0, Constants.TickCounts.CRANE_MAX, 0, craneStabilizer, -1);
    }

    public void positionBottom() {
        this.liftLeft.setPosition(0);
        this.liftRight.setPosition(0);
        this.shouldUpdatePositionalMotors = true;
    }

    public void positionLowBasket() {
        this.liftLeft.setPosition(Constants.TickCounts.CRANE_LOW_BASKET);
        this.liftRight.setPosition(Constants.TickCounts.CRANE_LOW_BASKET);
        this.shouldUpdatePositionalMotors = true;
    }

    public void positionHighBasket() {
        this.liftLeft.setPosition(Constants.TickCounts.CRANE_HIGH_BASKET);
        this.liftRight.setPosition(Constants.TickCounts.CRANE_HIGH_BASKET);
        this.shouldUpdatePositionalMotors = true;
    }

    public void runCrane(double speed) {
        this.c.liftLeft.motor.setVelocity(speed * c.liftLeft.ticksPerRevolution);
        this.c.liftRight.setVelocity(-speed * c.liftRight.ticksPerRevolution);
        if (speed != 0) {
            runningCrane = true;
        }
    }

    public void update() {
        // Crane
        if (shouldUpdatePositionalMotors) {
            updatePositionalMotors();
        }

        if (Math.abs(this.liftLeft.getPosition() - this.liftRight.getPosition()) >= Constants.TickCounts.CRANE_DIFFERENCE_FAIL_SAFE) {
            throw new RuntimeException("Difference between left and right lifts is too high!! Stopping!");
        }
    }

    public void updatePositionalMotors() {
        this.liftLeft.update();
        this.liftRight.update();


    }
}
