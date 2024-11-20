// Auto-Generated Autonomous Opmode SquareTestAuto Created by AutoBuilder TeleOp
package intothedeep.auto.generatedAutos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import intothedeep.auto.EasyAuto;

@Autonomous(name = "SquareTestAuto")
public class SquareTestAuto extends EasyAuto {
	@Override
	public void run() {
		verticalMovement(10);
		horizontalMovement(10);
		verticalMovement(-10);
		horizontalMovement(-10);
	}
}
