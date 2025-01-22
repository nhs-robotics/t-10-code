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

        crane.setTargetPosition(1500); //TODO: too high
        while(!crane.isAtTargetPosition()) {
            sleep(0.1);
            telemetry.clearAll();
            telemetry.addLine("Waiting for crane");
            telemetry.addLine(Integer.toString(crane.getPositionLeft()));
            telemetry.update();
        }

        verticalMovement(14);

        crane.setTargetPosition(652);
        armExtension.setTargetPosition((int)Math.ceil(ArmExtensionCapabilities.POSITION_FULLY_EXTENDED * 0.65));
        while(!crane.isAtTargetPosition()) {
            sleep(0.1);
            telemetry.clearAll();
            telemetry.addLine("Waiting for crane down");
            telemetry.addLine(Integer.toString(crane.getPositionLeft()));
            telemetry.update();
        }
        claw.setOpen(true);
        crane.setTargetPosition(CraneCapabilities.POSITION_BOTTOM);
        armExtension.setTargetPosition(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED);
        verticalMovement(-20);
        horizontalMovement(48);
    }
}