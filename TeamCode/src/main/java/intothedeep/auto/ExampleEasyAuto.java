package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import t10.utils.Alliance;

@Autonomous(name = "Example Easy Autonomous")
public class ExampleEasyAuto extends EasyAuto {
    public ExampleEasyAuto() {
        super(Alliance.RED);
    }

    @Override
    public void run() {
        horizontalMovement(20);
    }
}
