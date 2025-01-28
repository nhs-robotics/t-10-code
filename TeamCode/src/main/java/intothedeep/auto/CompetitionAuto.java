package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.auto.SequentialAction;
import t10.geometry.Pose;
import t10.motion.path.PurePursuitPathFollower;

@Autonomous
public class CompetitionAuto extends EasyAuto {
	private SequentialAction autoSequence;

	public CompetitionAuto() {
		super(new Pose(0.08, 66.53, 90, AngleUnit.DEGREES));
	}

	@Override
	public void initialize() {
		super.initialize();

		this.autoSequence = sequentially(
				simultaneously(
						followPath(
								new PurePursuitPathFollower.Builder()
										.addPoint(67.13, 0.20)
										.addPoint(47.95, 36.40)
										.addPoint(0.04, 35.56)
										.addPoint(0.16, 48.11)
										.addPoint(61.82, 48.23)
										.setSpeed(25)
										.setLookaheadDistance(4)
										.setLocalizer(this.localizer)
										.build()
						),
						armRotation(0)
				),
				followPath(
						new PurePursuitPathFollower.Builder()
								.addPoint(61.82, 48.23)
								.addPoint(0.04, 47.99)
								.addPoint(-0.08, 59.45)
								.setSpeed(25)
								.setLookaheadDistance(4)
								.setLocalizer(this.localizer)
								.build()
				),
				followPath(
						new PurePursuitPathFollower.Builder()
								.addPoint(-0.08, 59.09)
								.addPoint(61.94, 59.33)
								.setSpeed(25)
								.setLookaheadDistance(4)
								.setLocalizer(this.localizer)
								.build()
				),
				followPath(
						new PurePursuitPathFollower.Builder()
								.addPoint(61.46, 59.57)
								.addPoint(0.16, 61.98)
								.addPoint(0.04, 68.62)
								.setSpeed(25)
								.setLookaheadDistance(4)
								.setLocalizer(this.localizer)
								.build()
				),
				followPath(
						new PurePursuitPathFollower.Builder()
								.addPoint(-0.08, 68.14)
								.addPoint(58.57, 67.54)
								.setSpeed(25)
								.setLookaheadDistance(4)
								.setLocalizer(this.localizer)
								.build()
				),
				followPath(
						new PurePursuitPathFollower.Builder()
								.addPoint(58.81, 68.02)
								.addPoint(39.98, 62.95)
								.addPoint(42.52, 1.17)
								.addPoint(38.53, 0.68)
								.setSpeed(25)
								.setLookaheadDistance(4)
								.setLocalizer(this.localizer)
								.build()
				)
		);
//		this.autoSequence = sequentially(
//				simultaneously(
//						followPath(
//								new PurePursuitPathFollower.Builder()
//										.addPoint(-0.04, 71.84)
//										.addPoint(30.80, 48.35)
//										.addPoint(41.16, 0.16)
//										.addPoint(48.03, -0.08)
//										.setSpeed(30)
//										.setLookaheadDistance(12)
//										.setLocalizer(this.localizer)
//										.build()
//						),
//						armRotation(0)
//				),
//				moveTo(
//						new Pose(
//								56.06, 48.03, 90, AngleUnit.DEGREES
//						),
//						25
//				),
//				followPath(
//						new PurePursuitPathFollower.Builder()
//								.addPoint(48.75, 58.35)
//								.addPoint(48.39, 0.28)
//								.addPoint(58.75, -0.08)
//								.setSpeed(25)
//								.setLookaheadDistance(12)
//								.setLocalizer(this.localizer)
//								.build()
//				),
//				moveTo(
//						new Pose(
//								58.95, 59.23, 90, AngleUnit.DEGREES
//						),
//						25
//				),
//				followPath(
//						new PurePursuitPathFollower.Builder()
//								.addPoint(48.15, 63.05)
//								.addPoint(48.63, 32.45)
//								.addPoint(0.32, 46.18)
//								.setSpeed(25)
//								.setLookaheadDistance(12)
//								.setLocalizer(this.localizer)
//								.build()
//				),
//				simultaneously(
//						moveTo(
//								new Pose(46.18, 0.32, 0, AngleUnit.DEGREES),
//								25
//						),
//						crane(CraneCapabilities.POSITION_HIGH_CHAMBER),
//						armExtension(ArmExtensionCapabilities.POSITION_FULLY_EXTENDED)
//				),
//				moveTo(
//						new Pose(33.53, 0.32, 0, AngleUnit.DEGREES),
//						10
//				),
//				claw(true),
//				simultaneously(
//						followPath(
//								new PurePursuitPathFollower.Builder()
//										.addPoint(-0.16, 33.53)
//										.addPoint(0.08, 47.99)
//										.addPoint(48.39, 65.33)
//										.setSpeed(15)
//										.setLookaheadDistance(12)
//										.setLocalizer(this.localizer)
//										.build()
//						),
//						sequentially(
//								armExtension(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED),
//								crane(CraneCapabilities.POSITION_BOTTOM)
//						)
//				)
//		);
	}

	@Override
	public void run() {
	}

	@Override
	public void loop() {
		super.loop();

		if (this.autoSequence == null) return;

		this.autoSequence.loop();
	}
}
