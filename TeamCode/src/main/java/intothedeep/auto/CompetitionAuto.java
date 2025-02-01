package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.auto.SequentialAction;
import t10.geometry.Pose;
import t10.metrics.Metric;

@Autonomous
public class CompetitionAuto extends EasyAuto {
	private SequentialAction autoSequence;
	private Telemetry.Item x, y, rot;

	@Metric
	public Pose pose;

	public CompetitionAuto() {
		super(new Pose(0, 0, 0, AngleUnit.DEGREES));
	}

	@Override
	public void initialize() {
		super.initialize();
		this.x = this.telemetry.addData("x", 0);
		this.y = this.telemetry.addData("y", 0);
		this.rot = this.telemetry.addData("r", 0);

		this.autoSequence = sequentially(
				moveTo(new Pose(12, -24, 180, AngleUnit.DEGREES)),
				moveTo(new Pose(0, 0, 0, AngleUnit.DEGREES)),
				moveTo(new Pose(12, -24, 180, AngleUnit.DEGREES)),
				moveTo(new Pose(0, 0, 0, AngleUnit.DEGREES)),
				moveTo(new Pose(12, -24, 180, AngleUnit.DEGREES)),
				moveTo(new Pose(0, 0, 0, AngleUnit.DEGREES))
		);
	}

	@Override
	public void run() {
	}

	@Override
	public void loop() {
		super.loop();
		this.metrics.loop();

		this.localizer.loop();
		Pose fieldCentricPose = this.localizer.getFieldCentric();
		this.pose = fieldCentricPose;
		this.x.setValue(fieldCentricPose.getX());
		this.y.setValue(fieldCentricPose.getY());
		this.rot.setValue(fieldCentricPose.getHeading(AngleUnit.DEGREES));
		this.telemetry.update();

		if (this.autoSequence != null) {
			this.autoSequence.loop();
		}
	}
}
