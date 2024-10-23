package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Example Easy Autonomous")
public class ExampleEasyAuto extends EasyAuto {
    @Override
    public void run() {
        rotationalMovement(90,2);
        sleep(1);
        rotationalMovement(-90,2);
        sleep(2);
        rotationalMovement(45,2);
        sleep(1);
        rotationalMovement(-45,2);
    }
}
