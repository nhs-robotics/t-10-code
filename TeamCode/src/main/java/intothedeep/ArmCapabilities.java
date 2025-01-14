package intothedeep;

import t10.motion.hardware.Motor;
import t10.utils.PIDController;

public class ArmCapabilities {
    private final SnowballConfig config;
    private final PIDController armRotationStabilizer;
    private int armRotationTarget;
    private boolean runningRotation = false;
    public final int HORIZONTAL_TICKS = 0;
    public final int INSPECTION_TICKS = 0;
    public final int MIN_ROTATION = -50; //TODO: find better value
    public final int MAX_ROTATION = 788; //Fully Up
    public final int MAX_EXTENSION = 0; //Fully Retracted
    public final int MIN_EXTENSION = -6330; //Fully Extended
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

    public void rotateSafe(double power, boolean ignoreBounds)
    {
        int currentPosition = config.armRotation.motor.getCurrentPosition();
        if(ignoreBounds) {
            runRotation(power);
        }
        else if (currentPosition < MIN_ROTATION && power < 0) {
            //Do nothing
        }
        else if (currentPosition > MAX_ROTATION && power > 0) {
            //Do nothing
        }
        else {
            runRotation(power);
        }
    }

    public void rotateTo(int TargetTicks)
    {
        double dist = TargetTicks - config.armRotation.motor.getCurrentPosition();
        while(Math.abs(dist) < minError)
        {
            runRotation(Math.signum(dist));
            dist = TargetTicks - config.armRotation.encoder.getCurrentTicks();
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

    public void extendSafe(double power, boolean ignoreBounds)
    {
        int currentPosition = config.armExtension.motor.getCurrentPosition();
        if(ignoreBounds) {
            extendArm(power);
        }
        else if (currentPosition < MIN_EXTENSION && power < 0) {
            //Do nothing
        }
        else if (currentPosition > MAX_EXTENSION && power > 0) {
            //Do nothing
        }
        else {
            extendArm(power);
        }
    }
}
