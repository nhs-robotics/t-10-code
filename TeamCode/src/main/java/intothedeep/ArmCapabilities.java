package intothedeep;

import t10.utils.PIDController;

public class ArmCapabilities {
    private final SnowballConfig config;
    private final PIDController armRotationStabilizer;
    private int armRotationTarget;
    private boolean runningRotation = false;
    public final int HORIZONTAL_TICKS = 0;
    public final int INSPECTION_TICKS = 0;
    private final int minError = 10;

    public ArmCapabilities(SnowballConfig configuration) {
        this.config = configuration;
        this.armRotationStabilizer = new PIDController(0.05, 0, 0);
    }



    public void runRotation(double power) {
        this.config.armRotation.setPower(power);
        this.armRotationTarget = this.config.armRotation.motor.getCurrentPosition();
        if(power != 0)
        {
            runningRotation = true;
        }
    }

    public void turnTo(int TargetTicks)
    {
        double dist = config.armRotation.encoder.getCurrentTicks() - TargetTicks;
        while(Math.abs(dist) < minError)
        {
            runRotation(Math.signum(dist));
            dist = config.armRotation.encoder.getCurrentTicks() - TargetTicks;
        }
        runRotation(0);
    }

    public void update() {
        // Rotation
        if(!runningRotation) {
            double powerForRotationMotor = this.armRotationStabilizer.calculate(this.config.armRotation.motor.getCurrentPosition(), this.armRotationTarget);
            this.config.armRotation.setPower(powerForRotationMotor);
        }
        runningRotation = false;
    }

    public void extendArm(double power) {
        this.config.armExtension.setPower(power);
    }
}
