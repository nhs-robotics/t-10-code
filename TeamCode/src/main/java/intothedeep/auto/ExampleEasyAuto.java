package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Example Easy Autonomous")
public class ExampleEasyAuto extends EasyAuto {
    @Override
    public void run() {
        horizontalMovement(20);
    }
}
