package intothedeep;

import t10.utils.PIDController;

public class CraneCapabilities {
    private final IntoTheDeepRobotConfiguration c;
    private final PIDController armRotationStabilizer;
    private final PIDController craneStabilizer;
    private int armRotationTarget;
    private int craneTarget;

    public CraneCapabilities(IntoTheDeepRobotConfiguration c) {
        this.c = c;
        this.armRotationStabilizer = new PIDController(0.05, 0, 0);
        this.craneStabilizer = new PIDController(0.01, 0, 0);
    }

    public void runCrane(double power) {
        this.c.liftLeft.setPower(power);
        this.c.liftRight.setPower(power);
        this.craneTarget = this.c.liftLeft.motor.getCurrentPosition();
    }

    public void runRotation(double power) {
        this.c.armRotation.setPower(power);
        this.armRotationTarget = this.c.armRotation.motor.getCurrentPosition();
    }

    public void update() {
        // Crane
        double powerForCrane = this.craneStabilizer.calculate(this.c.liftLeft.motor.getCurrentPosition(), this.craneTarget);
        this.runCrane(powerForCrane);

        // Rotation
        double powerForRotationMotor = this.armRotationStabilizer.calculate(this.c.armRotation.motor.getCurrentPosition(), this.armRotationTarget);
        this.c.armRotation.setPower(powerForRotationMotor);
    }

    public void extendArm(double power) {
        this.c.armExtension.setPower(power);
    }
}
