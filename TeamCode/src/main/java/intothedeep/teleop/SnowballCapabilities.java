package intothedeep.teleop;

import android.os.SystemClock;

import com.qualcomm.robotcore.hardware.DcMotor;

import intothedeep.SnowballConfiguration;

public class SnowballCapabilities {
    public static final int LIFT_FULLY_EXTENDED_ENCODER_POS = 1500;
    public final SnowballConfiguration c;

    public SnowballCapabilities(SnowballConfiguration c) {
        this.c = c;
        /**
         * For encoder-enabled driving
         */
        this.c.upSlideRight.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.c.upSlideLeft.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.c.pivot.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.c.horizontalSlide.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void downLift(double power) {
        this.c.upSlideRight.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        this.c.upSlideRight.setPower(-Math.abs(power));
        this.c.upSlideLeft.setPower(Math.abs(power));
    }

    public void upLift(double power) {
        this.c.upSlideRight.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        this.c.upSlideRight.setPower(Math.abs(power));
        this.c.upSlideLeft.setPower(-Math.abs(power));
    }

    public void stopUpLift() {
        this.c.upSlideLeft.setPower(0);
        this.c.upSlideRight.setPower(0);
    }

    public void moveLiftToPosition(int position, double power) {
        this.c.linearSlideLeft.setPower(-Math.abs(power));
        this.c.linearSlideRight.setPower(-Math.abs(power));
        while(c.linearSlideRight.motor.getCurrentPosition() < 3500)
        {
            //do nothing
        }
        this.c.linearSlideLeft.setPower(0);
        this.c.linearSlideRight.setPower(0);
    }

    public void extendLiftFully() {
        this.moveLiftToPosition(LIFT_FULLY_EXTENDED_ENCODER_POS, 1);
    }

    public void retractLiftFully() {
        this.moveLiftToPosition(0, 1);
    }

    public void gripPixels() {
        System.out.println("grip");
        this.c.containerPixelHolder.setPosition(-1);
    }

    public void releasePixelGrip() {
        System.out.println("release");
        this.c.containerPixelHolder.setPosition(1);
    }

    public void runIntake() {
        this.c.roller.setPower(0.5);//Constants.ROLLER_INTAKE_SPEED);
        this.c.spinningIntake.setPower(0.5);//Constants.SPINNER_INTAKE_SPEED);
    }

    public void runRoller() {
        this.c.roller.setPower(-0.5);//Constants.ROLLER_SPEED * -1);
    }

    public void stopRoller() {
        this.c.roller.setPower(0);
    }

    public void runOuttake() {
        this.c.roller.setPower(-0.5);//-Constants.ROLLER_OUTTAKE_SPEED);
        this.c.spinningIntake.setPower(-0.5);//-Constants.SPINNER_OUTTAKE_SPEED);
    }

    public void stopIntakeOuttake() {
        this.c.roller.setPower(0);
        this.c.spinningIntake.setPower(0);
    }

    public void launchAirplane() {
        new Thread(() -> {
            this.c.airplaneLauncher.setPosition(0.5);
            SystemClock.sleep(1000);
            this.c.airplaneLauncher.setPosition(-1);
            SystemClock.sleep(1000);
            this.c.airplaneLauncher.setPosition(0.5);
        }).start();
    }

    /**
     * 1 = fully up
     * -1 = down
     */
    public void rotateContainer(double position) {
        this.c.containerRotationLeft.setPosition(position);
        this.c.containerRotationRight.setPosition(-position);
    }

    public void update() {
        double slideEncoderAvg = this.c.linearSlideRight.motor.getCurrentPosition();

        if (slideEncoderAvg <= 0) {
            this.stopLift();
        }
    }
    public void dropPixel()
    {
        this.c.spinningIntake.setPower(-0.2);
        SystemClock.sleep(250);
        this.c.spinningIntake.setPower(0);
    }
}
