package intothedeep.auto;

import android.os.SystemClock;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import intothedeep.Constants;
import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.ArmRotationCapabilities;
import intothedeep.capabilities.CraneCapabilities;
import t10.utils.Alliance;

@Autonomous
public class CompetitionAuto extends EasyAuto {
    public CompetitionAuto() {
        super(null);
    }

    @Override
    public void run() {
        verticalMovement(13);
        telemetry.clearAll();
        telemetry.addLine("Moved");
        telemetry.update();
        armExtension.setTargetPosition(ArmExtensionCapabilities.POSITION_FULLY_EXTENDED);
        while(!armExtension.isAtTargetPosition()) {
            sleep(0.1);
            telemetry.clearAll();
            telemetry.addLine("Waiting for extension");
            telemetry.addLine(Integer.toString(armExtension.getPosition()));
            telemetry.update();
        }

        crane.setTargetPosition(2010); //TODO: too high
        while(!crane.isAtTargetPosition()) {
            sleep(0.1);
            telemetry.clearAll();
            telemetry.addLine("Waiting for extension");
            telemetry.addLine(Integer.toString(crane.getPositionLeft()));
            telemetry.update();
        }

        verticalMovement(17);

        crane.setTargetPosition(1152);
        while(!crane.isAtTargetPosition()) {
            sleep(0.1);
            telemetry.clearAll();
            telemetry.addLine("Waiting for extension");
            telemetry.addLine(Integer.toString(crane.getPositionLeft()));
            telemetry.update();
        }
        claw.setOpen(true);
        crane.setTargetPosition(CraneCapabilities.POSITION_BOTTOM);
        armExtension.setTargetPosition(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED);
        verticalMovement(-30);
        horizontalMovement(48);
    }
}