package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import intothedeep.KevinRobotConfiguration;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.novel.mecanum.MecanumDriver;
import t10.novel.odometry.NovelOdometry;

@TeleOp
public class OdometryTestingTeleOp extends TeleOpOpMode {
    private MecanumDriver driver;
    private GController gamepadController;
    private KevinRobotConfiguration c;
    private NovelOdometry odometry;
    private KevinRobotConfiguration config;

    @Override
    public void initialize() {
        this.c = new KevinRobotConfiguration(this.hardwareMap);
        this.driver = this.c.createMecanumDriver();
        this.gamepadController = new GController(this.gamepad1)
                .x.initialToggleState(true).ok();  // micro-movement

        this.odometry = c.createOdometry();

        this.telemetry.setNumDecimalPlaces(0, 4);
    }

    @Override
    public void loop() {
        this.gamepadController.update();

        double changeX = 0;
        double changeY = 0;
        double changeRotation = 0;
        double speed = this.gamepadController.x.isToggled() ? 4 : 1;

        if (this.gamepadController.dpadLeft.isToggled())
            changeX -= 1;
        if (this.gamepadController.dpadRight.isToggled())
            changeX += 1;
        if (this.gamepadController.dpadDown.isToggled())
            changeY -= 1;
        if (this.gamepadController.dpadUp.isToggled())
            changeY += 1;
        if (this.gamepadController.leftBumper.isToggled())
            changeRotation -= 1;
        if (this.gamepadController.rightBumper.isToggled())
            changeRotation += 1;

        this.driver.setVelocity(new Vector3D(changeX, changeY, changeRotation).scalarMultiply(speed));
        this.telemetry.update();
        this.odometry.update();
        this.telemetry.addData("Odometry X: ", this.odometry.getRelativePose().getX());
        this.telemetry.addData("Odometry Y: ", this.odometry.getRelativePose().getX());
        this.telemetry.addData("Odometry Rot: ", this.odometry.getRelativePose().getHeading(AngleUnit.DEGREES));
    }
}
