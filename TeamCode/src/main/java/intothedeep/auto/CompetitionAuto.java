package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.CraneCapabilities;

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

        crane.setTargetPosition(1350);
        while(!crane.isAtTargetPosition()) {
            sleep(0.1);
            telemetry.clearAll();
            telemetry.addLine("Waiting for crane");
            telemetry.addLine(Integer.toString(crane.getPositionLeft()));
            telemetry.update();
        }

        verticalMovement(9);

        crane.setTargetPosition(720);
        armExtension.setTargetPosition((int)Math.ceil(ArmExtensionCapabilities.POSITION_FULLY_EXTENDED * 0.45));
        while(!crane.isAtTargetPosition()) {
            sleep(0.1);
            telemetry.clearAll();
            telemetry.addLine("Waiting for crane down");
            telemetry.addLine(Integer.toString(crane.getPositionLeft()));
            telemetry.update();
        }
        claw.openClaw();
        crane.setTargetPosition(CraneCapabilities.POSITION_BOTTOM);
        armExtension.setTargetPosition(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED);
        verticalMovement(-16);
        horizontalMovement(48);
        armRotation.setTargetPosition(0);
        while(!armRotation.isAtTargetPosition()) {
            sleep(0.1);
            telemetry.clearAll();
            telemetry.addLine("Waiting for rotation down");
            telemetry.update();
        }
        isDone = true;
    }
}