package intothedeep.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import java.util.List;

@TeleOp(name = "Preset Finder")
public class PresetFinderTeleop extends CompetitionTeleOp {
    public List<DcMotorEx> motors;
    private Telemetry.Item selectedMotorTelemetry;

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
        this.selectedMotorTelemetry = this.telemetry.addData("Selected Motor ", selectedMotorIndex);
    }

    @Override
    public void loop() {
        super.loop();
        int motorPosition = motors.get(selectedMotorIndex).getCurrentPosition();
        this.selectedMotorTelemetry.setValue(selectedMotorIndex + ": " + motorPosition);
    }
}
