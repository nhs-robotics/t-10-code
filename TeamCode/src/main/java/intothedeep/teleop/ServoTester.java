package intothedeep.teleop;

import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp(name = "Servo Tester")
public class ServoTester extends TeleOpOpMode {
    private GController gamepadController;
    private ServoTesterRobotConfiguration c;

    private Telemetry.Item selectedServoTelemetry;

    private int selectedServo = 0;
    private float speed = 0;

    @Override
    public void initialize() {
        this.c = new ServoTesterRobotConfiguration(this.hardwareMap);

        this.gamepadController = new GController(this.gamepad1)
                .rightBumper.onPress(() -> {
                    selectedServo = (selectedServo + 1) % (this.c.servos.size());
                }).ok()
                .leftBumper.onPress(() -> {
                    selectedServo -= 1;
                    if (selectedServo < 0) {
                        selectedServo = (this.c.servos.size() - 1);
                    }
                }).ok()
                .x.onPress(() -> {
                    speed = 1;
                }).onRelease(() -> {
                    speed = 0;
                }).ok()
                .y.onPress(() -> {
                    speed = -1;
                }).onRelease(() -> {
                    speed = 0;
                }).ok();
        this.selectedServoTelemetry = this.telemetry.addData("Selected Servo ", selectedServo);
    }

    @Override
    public void loop() {
        this.gamepadController.update();
        this.c.servos.get(selectedServo).setPower(speed);
        this.selectedServoTelemetry.setValue(selectedServo);
    }
}
