package intothedeep.capabilities;

import intothedeep.SnowballConfig;
import t10.utils.PIDController;

public class CraneCapabilities {
    public static final int CRANE_LOW_BASKET = 1800;
    public static final int CRANE_HIGH_BASKET = 3450;
    public static final int CRANE_DIFFERENCE_FAIL_SAFE = 500;
    private final SnowballConfig c;
    private final PIDController craneStabilizer;
    public int position;
    private boolean isStabilizerEnabled;

    public CraneCapabilities(SnowballConfig c) {
        this.c = c;
        this.craneStabilizer = new PIDController(0.01, 0, 0);
    }

    private void setPosition(int position) {
        this.position = position;
        this.isStabilizerEnabled = true;
    }

    public void positionBottom() {
        this.setPosition(0);
    }

    public void positionLowBasket() {
        this.setPosition(CRANE_LOW_BASKET);
    }

    public void positionHighBasket() {
        this.setPosition(CRANE_HIGH_BASKET);
    }

    public void runCrane(double power) {
        if (power == 0) {
            if (!this.isStabilizerEnabled) {
                this.isStabilizerEnabled = true;
                this.position = this.c.liftLeft.motor.getCurrentPosition();
            }
        } else {
            this.isStabilizerEnabled = false;
            this.c.liftLeft.setPower(power);
            this.c.liftRight.setPower(power);
        }
    }

    public void update() {
        // Crane
        if (this.isStabilizerEnabled) {
            double powerLeft = this.craneStabilizer.calculate(
                    this.c.liftLeft.motor.getCurrentPosition(),
                    this.position
            );
            this.c.liftLeft.setPower(powerLeft);

            double powerRight = this.craneStabilizer.calculate(
                    this.c.liftRight.motor.getCurrentPosition(),
                    this.position
            );
            this.c.liftRight.setPower(powerRight);
        }

        if (Math.abs(this.c.liftLeft.motor.getCurrentPosition() - this.c.liftRight.motor.getCurrentPosition()) >= CRANE_DIFFERENCE_FAIL_SAFE) {
            throw new RuntimeException("Difference between left and right lifts is too high!! Stopping!");
        }
    }
}
