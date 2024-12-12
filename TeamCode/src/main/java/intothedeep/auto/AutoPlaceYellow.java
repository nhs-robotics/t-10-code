package intothedeep.auto;

import android.os.SystemClock;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import intothedeep.Constants;
import t10.utils.Alliance;

@Autonomous
public class AutoPlaceYellow extends EasyAuto {
    public AutoPlaceYellow() {
        super(Alliance.RED);
    }

    @Override
    public void run() {
        horizontalMovement(5);
        verticalMovement(23.5 + Constants.Robot.ROBOT_WIDTH_IN / 2);
        horizontalMovement(5);
        SystemClock.sleep(1000);
//        // place specimen
        horizontalMovement(-5);
        verticalMovement(-(38 + Constants.Robot.ROBOT_WIDTH_IN / 2));
//
        turnTo(135);
//        horizontalMovement(5);

        // Note: each sample is around 10" farther than the next
        for (int i = 0; i < 3; i++) {
//            angleCorrect();
            horizontalMovement(43 - Constants.Robot.ROBOT_WIDTH_IN / 2);
            verticalMovement(-(10 * i));
            // grab sample
            SystemClock.sleep(1000);
            verticalMovement(10 * i);
            horizontalMovement(-(43 - Constants.Robot.ROBOT_WIDTH_IN / 2));
//            angleCorrect();
//            turnTo(-45);
            // place in top basket
//            turnTo(-90);
        }
//        angleCorrect();
//        verticalMovement(50 - 5);
//        horizontalMovement(-20);
    }
}