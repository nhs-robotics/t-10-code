package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
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
		super(new Pose(0, 64, -90, AngleUnit.DEGREES));
	}

	@Override
	public void init() {
		super.init();

		this.autoSequence = sequentially(
				moveTo(new Pose(36, 44, 90, AngleUnit.DEGREES)),
				moveTo(new Pose(36, 16, 90, AngleUnit.DEGREES)),
				moveTo(new Pose(44, 16, 90, AngleUnit.DEGREES)),
				sleep(1000),
				moveTo(new Pose(44, 58, 90, AngleUnit.DEGREES))
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
