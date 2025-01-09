// Auto-Generated Autonomous Opmode ParkAutoBlue Created by AutoBuilder TeleOp
package intothedeep.auto.generatedAutos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import intothedeep.auto.EasyAuto;
import t10.utils.Alliance;

@Autonomous(name = "ParkAutoBlue")
public class ParkAutoBlue extends EasyAuto {
	public ParkAutoBlue() {
		super(Alliance.BLUE, 4.0);
	}

	@Override
	public void run() {
		horizontalMovement(46.28034728854876);
	}
}
