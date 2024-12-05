package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import t10.utils.Alliance;

@Autonomous(name = "Odometry Test Auto")
public class OdometryTestAuto extends EasyAuto {
    public OdometryTestAuto() {
        super(Alliance.RED);
    }

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
