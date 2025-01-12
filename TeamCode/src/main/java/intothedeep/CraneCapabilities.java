package intothedeep;

import t10.utils.PIDController;

public class CraneCapabilities {
    private final SnowballConfig c;
    private final PIDController armRotationStabilizer;
    private final PIDController craneStabilizer;
    private int armRotationTarget;
    private int craneTarget;
    private boolean runningCrane = false;
    private boolean runningRotation = false;

    public CraneCapabilities(SnowballConfig c) {
        this.c = c;
        this.armRotationStabilizer = new PIDController(0.05, 0, 0);
        this.craneStabilizer = new PIDController(0.01, 0, 0);
    }

    public void runCrane(double speed) {
        this.c.liftLeft.motor.setVelocity(speed*c.liftLeft.ticksPerRevolution);
        this.c.liftRight.setVelocity(-speed*c.liftRight.ticksPerRevolution);
        if(speed != 0)
        {
            runningCrane = true;
        }
    }

    public void runRotation(double power) {
        this.c.armRotation.setPower(power);
        this.armRotationTarget = this.c.armRotation.motor.getCurrentPosition();
        if(power != 0)
        {
            runningRotation = true;
        }
    }

    public void update() {
        // Crane
        /*
        if(!runningCrane) {
            double powerForCrane = this.craneStabilizer.calculate(this.c.liftLeft.motor.getCurrentPosition(), this.craneTarget);
            this.runCrane(powerForCrane);
        }
         */
        runningCrane = false;

        // Rotation
        if(!runningRotation) {
            double powerForRotationMotor = this.armRotationStabilizer.calculate(this.c.armRotation.motor.getCurrentPosition(), this.armRotationTarget);
            this.c.armRotation.setPower(powerForRotationMotor);
        }
        runningRotation = false;
    }

    public void extendArm(double power) {
        this.c.armExtension.setPower(power);
    }
}
