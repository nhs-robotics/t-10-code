package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.ClawCapabilities;

import t10.auto.AutoAction;
import t10.auto.LinearMoveToAction;
import t10.auto.MoveToAction;
import t10.auto.RotateAction;
import t10.auto.SequentialAction;
import t10.geometry.Pose;
import t10.metrics.Metric;

@Autonomous
public class LinearMoveAuto extends EasyAuto {
	private SequentialAction autoSequence;

	@Metric
	public Pose pose;

	public LinearMoveAuto() {
		super(new Pose(36, 62, -90, AngleUnit.DEGREES));
	}

	@Override
	public void init() {
		super.init();

		this.autoSequence = sequentially(
				shuffleAllGroundSamples(),
				collectHumanPlayerSpecimen(),
				placeSpecimenHighChamber(0),
				collectHumanPlayerSpecimen(),
				placeSpecimenHighChamber(1.5),
				collectHumanPlayerSpecimen(),
				placeSpecimenHighChamber(3),
				park(),
				sleep(2500),
				claw(ClawCapabilities.ClawPreset.DOWN),
				armExtension(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED),
				armRotation(0)
//				collectHumanPlayerSpecimen(),
//				placeSpecimenHighChamber(),
//				park()
		);
	}

	private AutoAction placeSpecimenHighChamber(double yCoord) {
		return sequentially(
				new LinearMoveToAction(
						this.localizer,
						this.driver,
						new Pose(yCoord, 48.5, 90, AngleUnit.DEGREES),
						1.5,
						20,
						60, 3
				),
				simultaneously(
						sequentially(
								new RotateAction(
										this.localizer,
										this.driver,
										new Pose(yCoord, 48.5, -90, AngleUnit.DEGREES),
										1.25,
										90, 3
								),
								new MoveToAction(
										this.localizer,
										this.driver,
										new Pose(yCoord, 48.5, -90, AngleUnit.DEGREES),
										1.5,
										1.25,
										60, 90
								)
						),
						sequentially(
								armExtension(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED),
								armRotation(613),
								armExtension(-1330),
								claw(ClawCapabilities.ClawPreset.UP, false, true)
						)
				),
				new RotateAction(
						this.localizer,
						this.driver,
						new Pose(yCoord, 32, -90, AngleUnit.DEGREES),
						2,
						60,
						3

				),
				new LinearMoveToAction(
						this.localizer,
						this.driver,
						new Pose(yCoord, 32, -90, AngleUnit.DEGREES),
						5,
						20,
						30,
						3

				),
				claw(ClawCapabilities.ClawPreset.UP, true, true),
				simultaneously(
						new LinearMoveToAction(
								this.localizer,
								this.driver,
								new Pose(yCoord, 38,-90, AngleUnit.DEGREES),
								2,
								20,
								60,
								3
						),
						claw(ClawCapabilities.ClawPreset.DOWN, false, false)
				)
		);
	}

	private AutoAction shuffleAllGroundSamples() {
		return sequentially(
				// go from start to shuffle first one
				new LinearMoveToAction(
						this.localizer,
						this.driver,
						new Pose(36, 26, -90, AngleUnit.DEGREES),
						5,
						20,
						50, 3
				),

				// position precisely behind first one
				new LinearMoveToAction(
						this.localizer,
						this.driver,
						new Pose(45, 16, -90, AngleUnit.DEGREES),
						3,
						20,
						50, 3
				),

				// shuffle first one into observation zone
				new LinearMoveToAction(
						this.localizer,
						this.driver,
						new Pose(45, 48, -90, AngleUnit.DEGREES),
						5,
						20,
						50, 3
				),

				// move out to middle again to shuffle second one
				new LinearMoveToAction(
						this.localizer,
						this.driver,
						new Pose(45, 26, -90, AngleUnit.DEGREES),
						5,
						20,
						50, 3
				),

				// position precisely behind second one
				new LinearMoveToAction(
						this.localizer,
						this.driver,
						new Pose(55, 16, -90, AngleUnit.DEGREES),
						3,
						20,
						50, 3
				),

				// shuffle second one into observation zone
				new LinearMoveToAction(
						this.localizer,
						this.driver,
						new Pose(55, 48, -90, AngleUnit.DEGREES),
						5,
						20,
						50, 3
				)
		);
	}

	private AutoAction collectHumanPlayerSpecimen() {
		return sequentially(
				simultaneously(
						sequentially(
								new RotateAction(
										this.localizer,
										this.driver,
										new Pose(40, 44, -90, AngleUnit.DEGREES),
										2.0,
										100,
										3),

								new LinearMoveToAction(
										this.localizer,
										this.driver,
										new Pose(40, 44, -90, AngleUnit.DEGREES),
										1.75,
										20,
										60, 3
								)
						),
						sequentially(
								armExtension(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED),
								armRotation(250)
						),
						claw(ClawCapabilities.ClawPreset.FORWARD, true, true)
				),
				new RotateAction(
						this.localizer,
						this.driver,
						new Pose(40, 44, 90, AngleUnit.DEGREES),
						2.0,
						100, 3
				),
				new LinearMoveToAction(
						this.localizer,
						this.driver,
						new Pose(40, 44, 90, AngleUnit.DEGREES),
						1.75,
						20,
						60, 3
				),
				new LinearMoveToAction(
						this.localizer,
						this.driver,
						new Pose(40, 56, 90, AngleUnit.DEGREES),
						4,
						20,
						30, 3
				),
				claw(ClawCapabilities.ClawPreset.FORWARD, false, true),
				armRotation(325)
//				armExtension(-750),
//				claw(ClawCapabilities.ClawPreset.FORWARD, false, true),
//				armRotation(ArmRotationCapabilities.getTargetPositionAngle(25))
//				moveTo(new Pose(35.25, 48.5, -90, AngleUnit.DEGREES))
		);
	}

	private AutoAction park() {
		return simultaneously(
				new LinearMoveToAction(
						this.localizer,
						this.driver,
						new Pose(58, 58, -90, AngleUnit.DEGREES),
						1.25,
						20,
						30, 3),
				sequentially(
						armExtension(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED),
						armRotation(0)
				),
				claw(ClawCapabilities.ClawPreset.FORWARD, true, false)
		);
	}

	@Override
	public void init_loop() {
		super.init_loop();

		this.armRotation.loop();
		this.armExtension.loop();
		this.crane.loop();
		this.claw.loop();
		this.localizer.loop();
	}

	@Override
	public void loop() {
		super.loop();

		this.localizer.loop();
		this.autoSequence.loop();
		this.armRotation.loop();
		this.armExtension.loop();
		this.crane.loop();
		this.claw.loop();
		this.pose = this.localizer.getFieldCentric();
	}
}
