package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import t10.bootstrap.AbstractRobotConfiguration;
import t10.bootstrap.Hardware;
import t10.bootstrap.TeleOpOpMode;
import t10.localizer.odometry.OdometryLocalizer;
import t10.motion.NovelMotor;
import t10.motion.mecanum.MecanumDriver;
import t10.utils.PIDController;

@TeleOp
public class ArmLiftTestTeleOp extends TeleOpOpMode {
    private Config c;
    private PIDController armLift;
    private int armTarget;

    @Override
    public void initialize() {
        this.c = new Config(this.hardwareMap);
        this.armLift = new PIDController(0.05, 0, 0);
    }

    @Override
    public void loop() {
        this.runLift(this.gamepad1.left_stick_y);

        if (Math.abs(this.gamepad1.right_stick_y) > 0.1) {
            this.c.armRotation.motor.setPower(this.gamepad1.right_stick_y);
            this.armTarget = this.c.armRotation.motor.getCurrentPosition();
        } else {
            double pwr = this.armLift.calculate(this.c.armRotation.motor.getCurrentPosition(), this.armTarget);
            this.c.armRotation.motor.setPower(pwr);
        }

        if (this.gamepad1.dpad_up) {
            this.extendArm(1);
        } else if (this.gamepad1.dpad_down) {
            this.extendArm(-1);
        } else {
            this.extendArm(0);
        }
    }

    private void runLift(double power) {
        this.c.lift1.setPower(power);
        this.c.lift2.setPower(power);
    }

    private void extendArm(double power) {
        this.c.armExtension.setPower(power);
    }

    public static class Config extends AbstractRobotConfiguration {
        @Hardware(name = "Lift1")
        public NovelMotor lift1;

        @Hardware(name = "Lift2")
        public NovelMotor lift2;

        @Hardware(name = "ArmExtension")
        public NovelMotor armExtension;

        @Hardware(name = "ArmRotation")
        public NovelMotor armRotation;

        public Config(HardwareMap hardwareMap) {
            super(hardwareMap);
        }

        @Override
        public MecanumDriver createMecanumDriver() {
            return null;
        }

        @Override
        public OdometryLocalizer createOdometry() {
            return null;
        }
    }
}
