package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.ClawCapabilities;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.auto.AutoAction;
import t10.auto.MoveToAction;
import t10.auto.SequentialAction;
import t10.geometry.Pose;
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
				getNextAfterScore(),
				scoreFirst(6),
				park()
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
						40,
						50
				),
				armRotation(0)

		);
	}

	private AutoAction getNextAfterScore() {
		return sequentially(
				claw(ClawCapabilities.ClawPreset.FORWARD, true, false),
				armExtension(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED),
				simultaneously(
						new MoveToAction(
								this.localizer,
								this.driver,
								new Pose(56, 61, 0, AngleUnit.DEGREES),
								3,
								3,
								40,
								50
						),
						armRotation(266)
				),
				sleep(100),
				claw(ClawCapabilities.ClawPreset.FORWARD, false, false)
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
						25,
						50
				),
				armExtension(-1400),
				claw(ClawCapabilities.ClawPreset.UP, false, true),
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(y, 34, -90, AngleUnit.DEGREES),
						2,
						100,
						25,
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
		this.autoSequence.loop();
		this.armRotation.loop();
		this.armExtension.loop();
		this.crane.loop();
		this.claw.loop();
		this.pose = this.localizer.getFieldCentric();
	}
}
