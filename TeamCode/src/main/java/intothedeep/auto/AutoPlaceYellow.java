package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import intothedeep.Constants;

@Autonomous
public class AutoPlaceYellow extends EasyAuto {

    @Override
    public void run() {

        verticalMovement(5);
        System.out.println(this.odometry.getRelativeVelocity(0,1));
        horizontalMovement(33.5 + Constants.Robot.ROBOT_WIDTH_IN / 2);
        verticalMovement(5);
        // place specimen
        verticalMovement(-5);
        horizontalMovement(-33.5 + Constants.Robot.ROBOT_WIDTH_IN / 2);

        turnTo(-90);
        horizontalMovement(5);
        for (int i = 0; i < 3; i++) {
            verticalMovement(40 - Constants.Robot.ROBOT_WIDTH_IN / 2);
            // grab samples
            // Note: first sample is around 5" in front of roboto, with each being 10" farther than the next
            verticalMovement(-(40 - Constants.Robot.ROBOT_WIDTH_IN / 2));
            horizontalMovement(-(11.31 + 5));
            verticalMovement(11.31 - 5);
            turnTo(-45);
            // place in top basket
            turnTo(-90);
            verticalMovement(11.31 - 5);
            horizontalMovement(11.31 + 5);
        }

        verticalMovement(50 - 5);
        horizontalMovement(-20);
    }
}