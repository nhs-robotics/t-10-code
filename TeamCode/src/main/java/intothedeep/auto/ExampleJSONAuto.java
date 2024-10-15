package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Example JSON Auto")
public class ExampleJSONAuto extends JSONAuto {
    public ExampleJSONAuto() {
        super("exampleAuto.json");
    }
}