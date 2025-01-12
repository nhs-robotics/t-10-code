// Auto-Generated Autonomous Opmode ParkAutoRed Created by AutoBuilder TeleOp
package intothedeep.auto.generatedAutos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import intothedeep.auto.EasyAuto;
import t10.utils.Alliance;

@Autonomous(name = "ParkAutoRed")
public class ParkAutoRed extends EasyAuto {
	public ParkAutoRed() {
		super(Alliance.RED, 4.0);
	}

	@Override
	public void run() {
		horizontalMovement(46.28034728854876);
	}
}
