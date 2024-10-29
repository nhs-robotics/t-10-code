package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import intothedeep.Constants;

@Autonomous(name = "Place Yellow Autonomous")
public class AutoPlaceYellow extends EasyAuto {

    @Override
    public void run() {
        verticalMovement(5, 1);
        horizontalMovement(33.5 + Constants.Robot.ROBOT_WIDTH_IN / 2, 8);
        verticalMovement(5, 1);
        // place specimen
        verticalMovement(-5, 1);
        horizontalMovement(-33.5 + Constants.Robot.ROBOT_WIDTH_IN / 2, 8); // MIGHT BE + INSTEAD ??

        rotationalMovement(-90, 2);
        verticalMovement(-5, 1);
        for (int i = 0; i < 3; i++) {
            horizontalMovement(40 - Constants.Robot.ROBOT_WIDTH_IN / 2, 8);
            // grab samples
            // Note: first sample is around 5" in front of roboto, with each being 10" farther than the next
            horizontalMovement(-(40 - Constants.Robot.ROBOT_WIDTH_IN / 2), 8);
            verticalMovement(11.31 + 5, 3);
            horizontalMovement(11.31 - 5, 1);
            rotationalMovement(45, 1);
            // place in top basket
            rotationalMovement(-45, 1);
            horizontalMovement(-(11.31 - 5), 1);
            verticalMovement(-(11.31 + 5), 3);
        }

        horizontalMovement(50 - 5, 9);
        verticalMovement(20, 4);
    }
}