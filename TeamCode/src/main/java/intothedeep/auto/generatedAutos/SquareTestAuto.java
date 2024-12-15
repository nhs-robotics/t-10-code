// Auto-Generated Autonomous Opmode SquareTestAuto Created by AutoBuilder TeleOp
package intothedeep.auto.generatedAutos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import intothedeep.auto.EasyAuto;
import t10.utils.Alliance;

@Autonomous(name = "SquareTestAuto")
public class SquareTestAuto extends EasyAuto {
	public SquareTestAuto() {
		super(Alliance.NULL);
	}

	@Override
	public void run() {
		verticalMovement(10);
		horizontalMovement(-10);
		verticalMovement(-10);
		horizontalMovement(10);
	}
}
