package intothedeep;

import t10.motion.hardware.PositionalMotor;
import t10.utils.PIDController;

public class CraneCapabilities {
    private final SnowballConfig c;
    private final PIDController armRotationStabilizer;
    private final PIDController craneStabilizer;
    public final PositionalMotor liftLeft;
    public final PositionalMotor liftRight;
    private int armRotationTarget;
    private int craneTarget;
    private boolean runningCrane = false;
    private boolean runningRotation = false;
    private boolean shouldUpdatePositionalMotors = false;
    private final int minError = 500;

    public CraneCapabilities(SnowballConfig c) {
        this.c = c;
        this.armRotationStabilizer = new PIDController(0.05, 0, 0);
        this.craneStabilizer = new PIDController(0.16, 0, 0);

        liftLeft = new PositionalMotor(this.c.liftLeft.motor, 0, Constants.TickCounts.CRANE_MAX, 0, craneStabilizer, 1);
        liftRight = new PositionalMotor(this.c.liftLeft.motor, 0, Constants.TickCounts.CRANE_MAX, 0, craneStabilizer, -1);
    }

    public void positionBottom() {
        this.liftLeft.setTargetPosition(0);
        this.liftRight.setTargetPosition(0);
        this.shouldUpdatePositionalMotors = true;
    }

    public void positionLowBasket() {
        this.liftLeft.setTargetPosition(Constants.TickCounts.CRANE_LOW_BASKET);
        this.liftRight.setTargetPosition(Constants.TickCounts.CRANE_LOW_BASKET);
        this.shouldUpdatePositionalMotors = true;
    }

    public void positionHighBasket() {
        this.liftLeft.setTargetPosition(Constants.TickCounts.CRANE_HIGH_BASKET);
        this.liftRight.setTargetPosition(Constants.TickCounts.CRANE_HIGH_BASKET);
        this.shouldUpdatePositionalMotors = true;
    }

    public void runCrane(double speed) {
        this.c.liftLeft.motor.setVelocity(speed * c.liftLeft.ticksPerRevolution);
        this.c.liftRight.motor.setVelocity(speed * c.liftRight.ticksPerRevolution);
        if (speed != 0) {
            runningCrane = true;
        }
        else if(!shouldUpdatePositionalMotors) {
            this.liftLeft.setTargetPosition(liftLeft.getPosition(), true);
            this.liftRight.setTargetPosition(liftRight.getPosition(), true);
        }
    }

    public void update() {
        // Crane
        if (!runningCrane)
        {
            updatePositionalMotors();
        }

        if (Math.abs(this.liftLeft.getPosition() - this.liftRight.getPosition()) >= Constants.TickCounts.CRANE_DIFFERENCE_FAIL_SAFE) {
            throw new RuntimeException("Difference between left and right lifts is too high!! Stopping!");
        }
    }

    public void updatePositionalMotors() {
        this.liftLeft.update();
        this.liftRight.update();
        if(Math.abs(liftLeft.distToPosition()) < minError && Math.abs(liftRight.distToPosition()) < minError) {
            shouldUpdatePositionalMotors = false;
        }

    }

    public void stopAutoRun()
    {
        shouldUpdatePositionalMotors = false;
    }
}
