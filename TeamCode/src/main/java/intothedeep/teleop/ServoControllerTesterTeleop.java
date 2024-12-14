package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.List;

import intothedeep.Constants;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.motion.hardware.PositionalMotor;

@TeleOp(name = "DCMotor Servo Teleop")
public class ServoControllerTesterTeleop extends TeleOpOpMode {
    private GController gamepadController;
    public List<PositionalMotor> servos = new ArrayList<>();
    private Telemetry.Item servoTelemetry;

    @Override
    public void initialize() {
        // TEMP SPEED OF 0.1 FOR TESTING BECAUSE THINGS BROKE
        servos.add(new PositionalMotor(hardwareMap.get(DcMotorEx.class, "linearSlide1"), Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT, 0, 1, 0.1, 1));
        servos.add(new PositionalMotor(hardwareMap.get(DcMotorEx.class, "linearSlide2"), Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT, 0, 1, 0.1, -1));

        this.gamepadController = new GController(this.gamepad1)
                .x.onPress(() -> {
                    servos.get(0).setPosition(1);
                    servos.get(1).setPosition(1);
                }).ok();
        this.servoTelemetry = this.telemetry.addData("Servos positions ", servos.get(0).getPosition() + ", " + servos.get(1).getPosition());
    }

    @Override
    public void loop() {
        this.gamepadController.update();
        this.servos.get(0).update();
        this.servos.get(1).update();
        this.servoTelemetry.setValue("Servos positions ", servos.get(0).getPosition() + ", " + servos.get(1).getPosition());
    }
}
