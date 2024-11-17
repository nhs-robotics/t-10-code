package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import t10.bootstrap.AbstractRobotConfiguration;
import t10.bootstrap.Hardware;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.novel.NovelMotor;
import t10.novel.mecanum.MecanumDriver;
import t10.novel.odometry.NovelOdometry;

@TeleOp(name = "DoubleSlideTest")
public class DoubleSlideTest extends TeleOpOpMode {
    private GController gamepadController;
    private Telemetry.Item Speed, Direction, Encoder;

    private double power = 0;
    private double speed = 0.1;
    private double zero = 0.14;
    private TestConfig c;

    @Override
    public void initialize() {
        this.c = new TestConfig(hardwareMap);
        Speed = this.telemetry.addData("Speed: ", speed);
        Direction = this.telemetry.addData("Direction: ", power);
        Encoder = this.telemetry.addData("Encoder Value: ", 0);
        this.gamepadController = new GController(this.gamepad1)
                .y.onPress(() -> togglePower()).ok()
                .b.onPress(() -> reverseDirection()).ok()
                //power is positive, so right is positive and left is negative
                .rightBumper.onPress(() -> runSlide(c.linearSlideRight,power)).onRelease(() -> stopSlide(c.linearSlideRight)).ok()
                .leftBumper.onPress(() -> runSlide(c.linearSlideLeft,-power)).onRelease(() -> stopSlide(c.linearSlideLeft)).ok()
                .dpadUp.onPress(() -> speed += 0.05).ok()
                .dpadDown.onPress(() -> speed -= 0.05).ok()
                .a.onPress(() -> runBoth(c.linearSlideRight, c.linearSlideLeft, speed)).onRelease(() -> runBoth(c.linearSlideRight,c.linearSlideLeft,zero)).ok();
        c.linearSlideLeft.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        c.linearSlideRight.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void loop() {

        this.Speed.setValue(speed * power);
        this.Direction.setValue(power);
        //this.Encoder.setValue(c.linearSlideRight.motor.getCurrentPosition());
        //encoderMax = _____?
        telemetry.update();
        this.gamepadController.update();
    }
    private void togglePower()
    {
        power = Math.abs(power);
        power -= 1;
        power = Math.abs(power);
    }

    private void runSlide(NovelMotor slide, double powerLocal)
    {
        slide.setPower(powerLocal * speed * power);
    }

    private void runBoth(NovelMotor slide1, NovelMotor slide2, double amount)
    {
        runSlide(slide1,amount * power * speed);
        runSlide(slide2,amount * power * -speed);
    }

    private void stopSlide(NovelMotor slide) { slide.setPower(zero); }

    private void reverseDirection() { power = -power; }

    public static class TestConfig extends AbstractRobotConfiguration {
        @Hardware(name = "LinearSlideLeft")
        public NovelMotor linearSlideLeft;

        @Hardware(name = "LinearSlideRight")
        public NovelMotor linearSlideRight;

        public TestConfig(HardwareMap hardwareMap) {
            super(hardwareMap);
            linearSlideLeft.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            linearSlideRight.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        @Override
        public MecanumDriver createMecanumDriver() {
            return null;
        }

        @Override
        public NovelOdometry createOdometry() {
            return null;
        }
    }
}
