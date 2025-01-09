package intothedeep.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import java.util.List;

@TeleOp(name = "Preset Finder")
public class PresetFinderTeleop extends CompetitionTeleOp {
    public List<DcMotorEx> motors;
    private Telemetry.Item selectedMotorTelemetry, diffMotor, extension, arm, craneLeft, craneRight;

    private int selectedMotorIndex = 0;
    @Override
    public void initialize() {
        super.initialize();

        motors = hardwareMap.getAll(DcMotorEx.class);

        this.g2.rightBumper.onPress(() -> {
                    selectedMotorIndex = (selectedMotorIndex + 1) % (this.motors.size());
                }).ok()
                .leftBumper.onPress(() -> {
                    selectedMotorIndex -= 1;
                    if (selectedMotorIndex < 0) {
                        selectedMotorIndex = (this.motors.size() - 1);
                    }
                }).ok();
        //this.selectedMotorTelemetry = this.telemetry.addData("Selected Motor ", selectedMotorIndex);
        this.extension = this.telemetry.addData("Extension: ", 0);
        this.arm = this.telemetry.addData("Rotation: ", 0);
        this.craneLeft = this.telemetry.addData("Crane Left: ", 0);
        this.craneRight = this.telemetry.addData("Crane Right: ", 0);
        this.diffMotor = this.telemetry.addData("Motor Difference: ", 0);
    }

    @Override
    public void loop() {
        super.loop();
        /*
        int motorPosition = motors.get(selectedMotorIndex).getCurrentPosition();
        this.selectedMotorTelemetry.setValue(selectedMotorIndex + ": " + motorPosition);
         */
        this.extension.setValue(config.armExtension.motor.getCurrentPosition());
        this.arm.setValue(config.armRotation.motor.getCurrentPosition());
        this.craneLeft.setValue(config.liftLeft.motor.getCurrentPosition());
        this.craneRight.setValue(config.liftRight.motor.getCurrentPosition());
        this.diffMotor.setValue(config.liftRight.motor.getCurrentPosition() - config.liftLeft.motor.getCurrentPosition());
    }
}
