package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import intothedeep.IntoTheDeepRobotConfiguration;
import t10.bootstrap.AbstractRobotConfiguration;
import t10.bootstrap.Hardware;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.geometry.Pose;
import t10.localizer.odometry.OdometryLocalizer;
import t10.motion.NovelMotor;
import t10.motion.mecanum.MecanumDriver;

@TeleOp
public class ArmLiftTestTeleOp extends TeleOpOpMode {
    private IntoTheDeepRobotConfiguration c;
    private MecanumDriver driver;
    private OdometryLocalizer localizer;
    private GController gamepadController1;
    private Telemetry.Item x, y, r;

    @Override
    public void initialize() {
        this.x = this.telemetry.addData("x_novel: ", "0");
        this.y = this.telemetry.addData("y_novel: ", "0");
        this.r = this.telemetry.addData("r_novel: ", "0");
        this.c = new IntoTheDeepRobotConfiguration(this.hardwareMap);
        this.driver = this.c.createMecanumDriver();
        this.localizer = this.c.createOdometry();
        this.gamepadController1 = new GController(this.gamepad1)
                .x.initialToggleState(true).ok();  // micro-movement
    }

    @Override
    public void loop() {
        this.x.setValue(this.localizer.getFieldCentricPose().getX());
        this.y.setValue(this.localizer.getFieldCentricPose().getY());
        this.r.setValue(this.localizer.getFieldCentricPose().getHeading(AngleUnit.DEGREES));

        this.runLift(this.gamepad2.left_stick_y);
        this.rotateArm(500 * this.gamepad2.right_stick_y);

        if (this.gamepad2.dpad_up) {
            this.extendArm(1);
        } else if (this.gamepad2.dpad_down) {
            this.extendArm(-1);
        } else {
            this.extendArm(0);
        }

        this.driver.useGamepad(this.gamepad1,this.gamepadController1.x.isToggled() ? 0.25 : 1);
        localizer.update();
        telemetry.update();
    }

    private void runLift(double power) {
        this.c.liftRight.setPower(power);
        this.c.liftLeft.setPower(power);
    }

    private void rotateArm(double velocity) {
        this.c.armRotation.motor.setVelocity(velocity);
    }

    private void extendArm(double power) {
        this.c.armExtension.setPower(power);
    }
}


