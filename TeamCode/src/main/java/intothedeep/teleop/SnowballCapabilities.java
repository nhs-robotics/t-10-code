package intothedeep.teleop;

import android.os.SystemClock;

import com.qualcomm.robotcore.hardware.DcMotor;

import intothedeep.SnowballConfiguration;

public class SnowballCapabilities {
    public static final int UP_LIFT_MAX = 10000; //real value unknown
    public static final int UP_LIFT_MIN = 0;
    public static final int OUT_LIFT_MAX = 10000; //real value unknown
    public static final int OUT_LIFT_MIN = 0;

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

    public void upLiftRetract(double power) {
        this.c.upSlideRight.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        this.c.upSlideRight.setPower(-Math.abs(power));
        this.c.upSlideLeft.setPower(Math.abs(power));
    }

    public void upLiftExtend(double power) {
        this.c.upSlideRight.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        this.c.upSlideRight.setPower(Math.abs(power));
        this.c.upSlideLeft.setPower(-Math.abs(power));
    }

    public void stopUpLift() {
        this.c.upSlideLeft.setPower(0);
        this.c.upSlideRight.setPower(0);
    }

    public void horizontalLiftRetract(double power) {
        this.c.horizontalSlide.setPower(Math.abs(power));
    }

    public void horizontalLiftExtend(double power) {
        this.c.horizontalSlide.setPower(-Math.abs(power));
    }

    public void stopHorizontalLift() {
        this.c.horizontalSlide.setPower(0);
    }

    //public void moveLiftToPosition(int position, double power) {}

    //public void extendLiftFully() {}

    //public void retractLiftFully() {}

    public void closeClaw() {
    }

    public void openClaw() {
    }


    public void update() {
        double upSlideEncoderAvg = this.c.upSlideRight.motor.getCurrentPosition();
        double horizontalSlideEncoderAvg = this.c.horizontalSlide.motor.getCurrentPosition();

        if (upSlideEncoderAvg <= 0 || upSlideEncoderAvg >= UP_LIFT_MAX) {
            this.stopUpLift();
        }
        else if (horizontalSlideEncoderAvg <= 0 || horizontalSlideEncoderAvg >= OUT_LIFT_MAX)
        {
            this.stopHorizontalLift();
        }
    }
}
