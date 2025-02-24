package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import intothedeep.Constants;
import intothedeep.capabilities.ClawCapabilities;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.geometry.Pose;
import t10.metrics.Metric;
import t10.vision.SampleAlignmentProcessor;

@Autonomous
public class AlignAuto extends EasyAuto {
	private SampleAlignmentProcessor proc;
	@Metric
	public double trueDifference_h;
	@Metric
	public double trueDifference_w;
	@Metric
	public int center;

	@Metric
	public int difference;

	@Metric
	public Pose pose;

	public AlignAuto() {
		super(new Pose(0, 0, 90, AngleUnit.DEGREES));
	}

	@Override
	public void init() {
		super.init();
		this.proc = new SampleAlignmentProcessor(SampleAlignmentProcessor.SampleColor.BLUE);
		this.config.webcam.start(this.proc);
		this.metrics.streamWebcam(this.config.webcam);
		this.claw.setPreset(ClawCapabilities.ClawPreset.FORWARD, false);
		this.claw.setOpen(true);
		this.armRotation.setTargetPosition(185);
		this.armExtension.setTargetPosition(0);
	}

	@Override
	public void loop() {
		this.localizer.loop();

		if (this.proc.detectedSpecimen != null) {
			this.center = this.proc.detectedSpecimen.x + this.proc.detectedSpecimen.height;
			this.difference = center - SampleAlignmentProcessor.CENTER_X_POSITION;
			this.pose = this.localizer.getFieldCentric();
			this.trueDifference_h = (SampleAlignmentProcessor.SAMPLE_HEIGHT * Constants.Webcam.C270_FOCAL_LENGTH_Y) / this.proc.detectedSpecimen.height;
			this.trueDifference_w = (SampleAlignmentProcessor.SAMPLE_WIDTH * Constants.Webcam.C270_FOCAL_LENGTH_X) / this.proc.detectedSpecimen.width;
		}
	}
}
