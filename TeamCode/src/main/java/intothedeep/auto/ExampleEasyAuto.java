package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Example Easy Autonomous")
public class ExampleEasyAuto extends EasyAuto {
    @Override
    public void run() {
        turnRelative(90);
        sleep(1);
        turnRelative(-90);
        sleep(1);
        turnRelative(45);
        sleep(1);
        turnRelative(-45);
    }
}
