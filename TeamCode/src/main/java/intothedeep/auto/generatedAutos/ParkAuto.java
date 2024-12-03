// Auto-Generated Autonomous Opmode ParkAuto Created by AutoBuilder TeleOp
package intothedeep.auto.generatedAutos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import intothedeep.auto.EasyAuto;
import t10.utils.Alliance;

@Autonomous(name = "ParkAuto")
public class ParkAuto extends EasyAuto {
	public ParkAuto() {
		super(Alliance.RED);
	}

	@Override
	public void run() {
		horizontalMovement(41.95063256450591);
	}
}
