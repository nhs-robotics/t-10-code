// Auto-Generated Autonomous Opmode DiagonalTestAuto Created by AutoBuilder TeleOp
package intothedeep.auto.generatedAutos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import intothedeep.auto.EasyAuto;
import t10.utils.Alliance;

@Autonomous(name = "DiagonalTestAuto")
public class DiagonalTestAuto extends EasyAuto {
	public DiagonalTestAuto() {
		super(Alliance.RED);
	}

	@Override
	public void run() {
		diagonalMovement(14.60885670162091, 16.971634592039475);
		diagonalMovement(13.575551000247476, -20.714318097201208);
		diagonalMovement(-13.523516637043734, 20.774933560938372);
		diagonalMovement(-15.972128436709943, -17.950518099589303);
	}
}
