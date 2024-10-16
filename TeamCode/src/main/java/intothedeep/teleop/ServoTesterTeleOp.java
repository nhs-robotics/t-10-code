package intothedeep.teleop;

import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.List;

@TeleOp(name = "Servo Tester")
public class ServoTesterTeleOp extends TeleOpOpMode {
    private GController gamepadController;
    public List<CRServo> servos;
    private Telemetry.Item selectedServoTelemetry;

    private int selectedServoIndex = 0;
    private float speed = 0;

    @Override
    public void initialize() {
        servos = hardwareMap.getAll(CRServo.class);

        this.gamepadController = new GController(this.gamepad1)
                .rightBumper.onPress(() -> {
                    selectedServoIndex = (selectedServoIndex + 1) % (this.servos.size());
                }).ok()
                .leftBumper.onPress(() -> {
                    selectedServoIndex -= 1;
                    if (selectedServoIndex < 0) {
                        selectedServoIndex = (this.servos.size() - 1);
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
        this.selectedServoTelemetry = this.telemetry.addData("Selected Servo ", selectedServoIndex);
    }

    @Override
    public void loop() {
        this.gamepadController.update();
        this.servos.get(selectedServoIndex).setPower(speed);
        this.selectedServoTelemetry.setValue(selectedServoIndex);
    }
}
