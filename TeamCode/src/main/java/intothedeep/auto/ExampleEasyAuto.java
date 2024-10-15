package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Example Easy Autonomous")
public class ExampleEasyAuto extends EasyAuto {
    @Override
    public void run() {
        lateralMovement(24,0,2);
        sleep(10);
        lateralMovement(-24,0,2);
        sleep(10);
        lateralMovement(0,24,2);
        sleep(10);
        lateralMovement(0,-24,2);
        sleep(10);
        lateralMovement(24,24,2);
        sleep(10);
        lateralMovement(-24,-24,2);
        sleep(20);
        rotationalMovement(90,2);
        sleep(10);
        rotationalMovement(-90,2);
    }
}
