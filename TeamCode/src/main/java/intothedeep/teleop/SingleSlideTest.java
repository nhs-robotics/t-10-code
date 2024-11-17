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

@TeleOp(name = "SingleSlideTest")
public class SingleSlideTest extends TeleOpOpMode {
    private GController gamepadController;
    private Telemetry.Item Speed, Direction, PowerStatus;

    private double power = 0;
    private double speed = 0.1;
    private double zero = 0;
    private TestConfig c;

    @Override
    public void initialize() {
        this.c = new TestConfig(hardwareMap);
        Speed = this.telemetry.addData("Speed: ", speed);
        Direction = this.telemetry.addData("Direction: ", power);
        PowerStatus = this.telemetry.addData("Power: ", "off");
        this.gamepadController = new GController(this.gamepad1)
                .y.onPress(() -> togglePower()).ok()
                .b.onPress(() -> reverseDirection()).ok()
                //power is positive, so right is positive and left is negative
                .a.onPress(() -> runSlide(c.linearSlide)).onRelease(() -> stopSlide(c.linearSlide)).ok()
                .dpadUp.onPress(() -> speed += 0.1).ok()
                .dpadDown.onPress(() -> speed -= 0.1).ok();
        c.linearSlide.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    @Override
    public void loop() {

        this.Speed.setValue(speed * power);
        this.Direction.setValue(power);
        //this.PowerStatus.setValue(c.linearSlideRight.motor.getCurrentPosition());
        //encoderMax = _____?
        telemetry.update();
        this.gamepadController.update();
    }
    private void togglePower()
    {
        if(power != 0)
        {
            PowerStatus.setValue("Off");
        }
        else
        {
            PowerStatus.setValue("Up");
        }
        power = Math.abs(power);
        power -= 1;
        power = Math.abs(power);
    }

    private void runSlide(NovelMotor slide)
    {
        slide.setPower(speed * power);
    }

    private void stopSlide(NovelMotor slide) { slide.setPower(zero); }

    private void reverseDirection() {
        if(power == 1)
        {
            PowerStatus.setValue("Down");
        }
        else if (power == -1)
        {
            PowerStatus.setValue("Up");
        }
        power = -power;
    }

    public static class TestConfig extends AbstractRobotConfiguration {
        @Hardware(name = "LinearSlide")
        public NovelMotor linearSlide;

        public TestConfig(HardwareMap hardwareMap) {
            super(hardwareMap);
            linearSlide.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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
