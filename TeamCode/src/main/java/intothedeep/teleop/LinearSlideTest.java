package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import intothedeep.Constants;
import t10.bootstrap.Hardware;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.novel.NovelMotor;
import t10.novel.mecanum.MecanumDriver;

@TeleOp(name = "SlideTestCrude")
public class LinearSlideTest extends TeleOpOpMode {
    private GController gamepadController;
    private Telemetry.Item Speed, Direction, Encoder;
    @Hardware(name = "LinearSlideLeft")
    public NovelMotor linearSlideLeft;

    @Hardware(name = "LinearSlideRight")
    public NovelMotor linearSlideRight;
    private int power = 0;
    private double speed = 0.1;

    @Override
    public void initialize() {
        Speed = this.telemetry.addData("Speed: ", speed);
        Direction = this.telemetry.addData("Direction: ", power);
        Encoder = this.telemetry.addData("Encoder Value: ", 0);
        this.gamepadController = new GController(this.gamepad1)
                .y.onPress(() -> togglePower()).ok()
                .b.onPress(() -> reverseDirection()).ok()
                //power is positive, so right is positive and left is negative
                .rightBumper.whileDown(() -> runSlide(linearSlideRight,power)).onRelease(() -> stopSlide(linearSlideRight)).ok()
                .leftBumper.whileDown(() -> runSlide(linearSlideLeft,-power)).onRelease(() -> stopSlide(linearSlideLeft)).ok()
                .dpadUp.onPress(() -> speed += 0.05).ok()
                .dpadDown.onPress(() -> speed -= 0.05).ok();
    }

    @Override
    public void loop() {
        runBoth(linearSlideRight,linearSlideLeft,gamepadController.leftJoystick.getX());
        this.Speed.setValue(speed);
        this.Direction.setValue(power);
        this.Encoder.setValue(linearSlideRight.motor.getCurrentPosition());
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

    private void runSlide(NovelMotor slide, double power)
    {
        slide.setPower(power * speed);
    }

    private void runBoth(NovelMotor slide1, NovelMotor slide2, double amount)
    {
        runSlide(slide1,amount * power * speed);
        runSlide(slide2,amount * power * -speed);
    }

    private void stopSlide(NovelMotor slide) { slide.setPower(0); }

    private void reverseDirection() { power = -power; }
}
