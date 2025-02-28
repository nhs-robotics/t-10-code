package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import intothedeep.Constants;
import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.ClawCapabilities;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import t10.auto.AutoAction;
import t10.auto.MoveToAction;
import t10.auto.SequentialAction;
import t10.geometry.Point;
import t10.geometry.Pose;
import t10.localizer.AprilTagLocalizer;
import t10.metrics.Metric;

@Autonomous
public class CompetitionAuto3 extends EasyAuto {
	@Metric
	public Pose pose;
	private SequentialAction autoSequence;

	public CompetitionAuto3() {
		super(new Pose(10, 62, -90, AngleUnit.DEGREES));
	}

	@Override
	public void init() {
		super.init();

		this.autoSequence = sequentially(
				scoreFirst(10),
				shuffleOne(),
				getNextAfterScore(),
				scoreFirst(4),
				park()
		);
	}

	private AutoAction shuffleOne() {

		return sequentially(
				claw(ClawCapabilities.ClawPreset.FORWARD, true, false),
				armExtension(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED),
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(36, 40, -90, AngleUnit.DEGREES),
						5,
						3,
						50,
						60
				),

				// go from start to shuffle first one
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(36, 20, -90, AngleUnit.DEGREES),
						3,
						3,
						50, 60
				),

				// position precisely behind first one
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(45, 16, -90, AngleUnit.DEGREES),
						2,
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

				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(45, 40, 0, AngleUnit.DEGREES),
						2,
						3,
						50, 60
				)
		);
	}

	private AutoAction park() {
		return sequentially(
				claw(ClawCapabilities.ClawPreset.FORWARD, true, false),
				armExtension(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED),
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(50, 57, -90, AngleUnit.DEGREES),
						3,
						3,
						60,
						50
				),
				armRotation(0)
		);
	}

	private AutoAction getNextAfterScore() {
		return sequentially(
				claw(ClawCapabilities.ClawPreset.FORWARD, true, true),
				armExtension(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED),
				simultaneously(
						new MoveToAction(
								this.localizer,
								this.driver,
								new Pose(45, 63, 0, AngleUnit.DEGREES),
								4,
								2,
								40,
								60
						),
						armRotation(400)
				),
				sleep(500),
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(56, 63, 0, AngleUnit.DEGREES),
						4,
						3,
						35, 60
				),
				claw(ClawCapabilities.ClawPreset.FORWARD, false, true)
		);
	}

	private AutoAction scoreFirst(double y) {
		return sequentially(
				armRotation(628),
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(y, 52, -90, AngleUnit.DEGREES),
						2,
						100,
						30,
						50
				),
				armExtension(-1400),
				claw(ClawCapabilities.ClawPreset.UP, false, true),
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(y, 31.5, -90, AngleUnit.DEGREES),
						3,
						100,
						30,
						50
				),
				claw(ClawCapabilities.ClawPreset.UP, true, true)
		);
	}

	@Override
	public void init_loop() {
		super.init_loop();

		this.armRotation.loop();
		this.armExtension.loop();
		this.crane.loop();
		this.claw.loop();
	}

	@Override
	public void loop() {
		super.loop();

		this.localizer.loop();
		this.pose = this.localizer.getFieldCentric();
		this.autoSequence.loop();
		this.armRotation.loop();
		this.armExtension.loop();
		this.crane.loop();
		this.claw.loop();
	}
}
