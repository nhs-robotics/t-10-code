package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.CraneCapabilities;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.auto.SequentialAction;
import t10.geometry.Pose;
import t10.metrics.Metric;

@Autonomous
public class CompetitionAuto extends EasyAuto {
	private SequentialAction autoSequence;

	@Metric
	public Pose pose;

	public CompetitionAuto() {
		super(new Pose(12, 64, -90, AngleUnit.DEGREES));
	}

	@Override
	public void init() {
		super.init();

		this.autoSequence = sequentially(
				moveTo(new Pose(36, 44, -90, AngleUnit.DEGREES)),
				moveTo(new Pose(36, 16, -90, AngleUnit.DEGREES)),
				moveTo(new Pose(44, 16, -90, AngleUnit.DEGREES)),
				moveTo(new Pose(44, 58, -90, AngleUnit.DEGREES)),
				moveTo(new Pose(44, 16, -90, AngleUnit.DEGREES)),
				moveTo(new Pose(54, 16, -90, AngleUnit.DEGREES)),
				moveTo(new Pose(54, 58, -90, AngleUnit.DEGREES)),
				moveTo(new Pose(54, 16, -90, AngleUnit.DEGREES)),
				moveTo(new Pose(59, 16, -90, AngleUnit.DEGREES)),
				moveTo(new Pose(61, 58, -90, AngleUnit.DEGREES)),
				simultaneously(
						moveTo(new Pose(0, 46, -90, AngleUnit.DEGREES)),
						crane(CraneCapabilities.POSITION_HIGH_CHAMBER),
						armRotation(0)
				),
				armExtension(ArmExtensionCapabilities.POSITION_FULLY_EXTENDED),
				simultaneously(
						armExtension(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED),
						crane(CraneCapabilities.POSITION_BOTTOM)
				),
				moveTo(new Pose(54, 58, 0, AngleUnit.DEGREES))
		);
	}

	@Override
	public void loop() {
		super.loop();

		this.pose = this.localizer.getFieldCentric();

		if (this.autoSequence != null) {
			this.autoSequence.loop();
		}
	}
}
