package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import t10.bootstrap.AbstractRobotConfiguration;
import t10.bootstrap.Hardware;
import t10.bootstrap.TeleOpOpMode;
import t10.localizer.odometry.OdometryLocalizer;
import t10.motion.NovelMotor;
import t10.motion.mecanum.MecanumDriver;

@TeleOp
public class ArmLiftTestTeleOp extends TeleOpOpMode {
    private Config c;

    @Override
    public void initialize() {
        this.c = new Config(this.hardwareMap);
    }

    @Override
    public void loop() {
        this.runLift(this.gamepad1.left_stick_y);
        this.rotateArm(500 * this.gamepad1.right_stick_y);

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

    private void rotateArm(double velocity) {
        this.c.armRotation.motor.setVelocity(velocity);
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
