package intothedeep;

import t10.utils.PIDController;

public class ArmCapabilities {
    private final SnowballConfig c;
    private final PIDController armRotationStabilizer;
    private int armRotationTarget;
    private int craneTarget;
    private boolean runningCrane = false;
    private boolean runningRotation = false;

    public ArmCapabilities(SnowballConfig c) {
        this.c = c;
        this.armRotationStabilizer = new PIDController(0.05, 0, 0);
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
