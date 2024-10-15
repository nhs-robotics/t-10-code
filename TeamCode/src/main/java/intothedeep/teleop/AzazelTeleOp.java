package intothedeep.teleop;

import intothedeep.IntoTheDeepRobotConfiguration;
import intothedeep.Constants;
import intothedeep.teleop.AzazelRobotCapabilities;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.novel.mecanum.MecanumDriver;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

@TeleOp(name = "Azazel Tele-Op")
public class AzazelTeleOp extends TeleOpOpMode {
    private AzazelRobotCapabilities capabilities;
    private MecanumDriver driver;
    private GController gamepadController;
    private Telemetry.Item telemetryItem;
    private AzazelRobotConfiguration c;
    private int position = 0;
    private static final double[] positions = {0.95, -0.748, -1};

    @Override
    public void initialize() {
        this.c = new AzazelRobotConfiguration(this.hardwareMap);
        this.capabilities = new AzazelRobotCapabilities(c);
        this.driver = new MecanumDriver(c.fl, c.fr, c.bl, c.br, c.imu, Constants.Coefficients.PRODUCTION_COEFFICIENTS);

        this.gamepadController = new GController(this.gamepad1)
                .x.ok()
                .y.onPress(this.capabilities::launchAirplane).ok()
                .a.onToggleOn(this.capabilities::gripPixels).onToggleOff(this.capabilities::releasePixelGrip).ok()
                .rightTrigger.whileDown(this.capabilities::upLift).onRelease(this.capabilities::stopLift).ok()
                .leftTrigger.whileDown(this.capabilities::downLift).onRelease(this.capabilities::stopLift).ok()
                .rightBumper.onPress(() -> position = (position + 1) % 3).ok()
                .leftBumper.onPress(() -> {
                    position--;

                    if (position < 0) {
                        position = positions.length-1;
                    }
                }).ok()
                .dpadUp.onPress(this.capabilities::runIntake).onRelease(this.capabilities::stopIntakeOuttake).ok()
                .dpadDown.onPress(this.capabilities::runOuttake).onRelease(this.capabilities::stopIntakeOuttake).ok();
        this.telemetryItem = this.telemetry.addData("angle ", 0);
    }

    @Override
    public void loop() {
        this.capabilities.rotateContainer(positions[position]);
        this.telemetryItem.setValue(this.c.linearSlideRight.motor.getCurrentPosition());
        this.capabilities.update();
        this.gamepadController.update();
        this.driver.useGamepad(this.gamepad1, this.gamepadController.x.isToggled() ? 0.1 : 0.05);

        YawPitchRollAngles angles = this.c.imu.getRobotYawPitchRollAngles();
        this.telemetryItem.setValue(angles.getYaw(AngleUnit.DEGREES));
    }
}
