package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Odometry Test Auto")
public class OdometryTestAuto extends EasyAuto {
    @Override
    public void run() {
        verticalMovement(20);
        horizontalMovement(20);
        verticalMovement(-20);
        horizontalMovement(-20);

        sleep(2);

        turnTo(90);
        verticalMovement(20);
        horizontalMovement(20);
        verticalMovement(-20);
        horizontalMovement(-20);
    }
}
