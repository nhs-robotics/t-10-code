package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import intothedeep.Constants;
import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.ArmRotationCapabilities;
import intothedeep.capabilities.ClawCapabilities;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.auto.AutoAction;
import t10.auto.MoveToAction;
import t10.auto.SequentialAction;
import t10.geometry.Point;
import t10.geometry.Pose;
import t10.localizer.AprilTagLocalizer;
import t10.metrics.Metric;
import t10.vision.SampleAlignmentProcessor;

@Autonomous
public class CompetitionAuto2 extends EasyAuto {
	private SequentialAction autoSequence;

	@Metric
	public Pose pose;

	public CompetitionAuto2() {
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
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(yCoord, 48.5, 90, AngleUnit.DEGREES),
						1.5,
						1.25,
						60, 90
				),
				simultaneously(
						new MoveToAction(
								this.localizer,
								this.driver,
								new Pose(yCoord, 48.5, -90, AngleUnit.DEGREES),
								1.5,
								1.25,
								60, 90
						),
						sequentially(
								armExtension(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED),
								armRotation(613),
								armExtension(-1330),
								claw(ClawCapabilities.ClawPreset.UP, false, true)
						)
				),
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(yCoord, 32, -90, AngleUnit.DEGREES),
						5,
						2,
						30,
						60

				),
				claw(ClawCapabilities.ClawPreset.UP, true, true),
				simultaneously(
						new MoveToAction(
								this.localizer,
								this.driver,
								new Pose(yCoord, 38,-90, AngleUnit.DEGREES),
								2,
								4,
								60,
								80
						),
						claw(ClawCapabilities.ClawPreset.DOWN, false, false)
				)
		);
	}

	private AutoAction shuffleAllGroundSamples() {
		return sequentially(
				// go from start to shuffle first one
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(36, 26, -90, AngleUnit.DEGREES),
						5,
						3,
						50, 60
				),

				// position precisely behind first one
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(45, 16, -90, AngleUnit.DEGREES),
						3,
						3,
						50, 60
				),

				// shuffle first one into observation zone
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(45, 48, -90, AngleUnit.DEGREES),
						5,
						3,
						50, 60
				),

				// move out to middle again to shuffle second one
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(45, 26, -90, AngleUnit.DEGREES),
						5,
						3,
						50, 60
				),

				// position precisely behind second one
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(55, 16, -90, AngleUnit.DEGREES),
						3,
						3,
						50, 60
				),

				// shuffle second one into observation zone
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(55, 48, -90, AngleUnit.DEGREES),
						5,
						3,
						50, 60
				)
		);
	}

	private AutoAction collectHumanPlayerSpecimen() {
		return sequentially(
				simultaneously(
						new MoveToAction(
								this.localizer,
								this.driver,
								new Pose(40, 44, -90, AngleUnit.DEGREES),
								1.75,
								2.0,
								60, 100
						),
						sequentially(
								armExtension(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED),
								armRotation(250)
						),
						claw(ClawCapabilities.ClawPreset.FORWARD, true, true)
				),
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(40, 44, 90, AngleUnit.DEGREES),
						1.75,
						2.0,
						60, 100
				),
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(40, 56, 90, AngleUnit.DEGREES),
						4,
						100,
						30, 0
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
				moveTo(new Pose(58, 58, -90, AngleUnit.DEGREES)),
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
