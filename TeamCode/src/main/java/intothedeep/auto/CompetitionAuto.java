package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.ArmRotationCapabilities;
import intothedeep.capabilities.ClawCapabilities;
import intothedeep.capabilities.CraneCapabilities;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.auto.AutoAction;
import t10.auto.SequentialAction;
import t10.auto.SimultaneousAction;
import t10.geometry.MovementVector;
import t10.geometry.Pose;
import t10.metrics.Metric;
import t10.utils.MathUtils;
import t10.vision.SampleAlignmentProcessor;

@Autonomous
public class CompetitionAuto extends EasyAuto {
	private SequentialAction autoSequence;
	private SampleAlignmentProcessor processor;
	private int numPlacedSpecimen = 0;

	@Metric
	public Pose pose;

	public CompetitionAuto() {
		super(new Pose(12, 64, -90, AngleUnit.DEGREES));
	}

	@Override
	public void init() {
		super.init();
//		this.processor = new SampleAlignmentProcessor(SampleAlignmentProcessor.SampleColor.BLUE);
//		this.config.webcam.start(this.processor);

		this.autoSequence = sequentially(
				placeSpecimenHighChamber(),
				moveToFirstGroundSample(),
				shuffleAllGroundSamples(),
				collectHumanPlayerSpecimen(),
				placeSpecimenHighChamber(),
				collectHumanPlayerSpecimen(),
				placeSpecimenHighChamber(),
				collectHumanPlayerSpecimen(),
				placeSpecimenHighChamber(),
				collectHumanPlayerSpecimen(),
				placeSpecimenHighChamber(),
				park()
		);

//		this.metrics.streamWebcam(this.config.webcam);
	}

	private AutoAction placeSpecimenHighChamber() {
		numPlacedSpecimen++;
		return sequentially(
				simultaneously(
						moveTo(new Pose(-6 + 3 * numPlacedSpecimen, 48.5, -90, AngleUnit.DEGREES)),
						claw(ClawCapabilities.ClawPreset.FORWARD, false, false),
						armRotation(ArmRotationCapabilities.getTargetPositionAngle(50)),
						armExtension(ArmExtensionCapabilities.POSITION_FULLY_EXTENDED)
				),
				armRotation(ArmRotationCapabilities.getTargetPositionAngle(25)),
				sleep(550),
				simultaneously(
						claw(ClawCapabilities.ClawPreset.DOWN, true),
						armRotation(ArmRotationCapabilities.getTargetPositionAngle(45))
				),
				armExtension(ArmExtensionCapabilities.POSITION_FULLY_EXTENDED)
		);
	}

	private AutoAction moveToFirstGroundSample() {
		return simultaneously(
				sequentially(
						armExtension(0),
						armRotation(0)
				),
				moveTo(new Pose(36, 46, -90, AngleUnit.DEGREES))
		);
	}

	private AutoAction shuffleAllGroundSamples() {
		return sequentially(
				moveTo(new Pose(36, 16, -90, AngleUnit.DEGREES)),
				moveTo(new Pose(44, 16, -90, AngleUnit.DEGREES)),
				moveTo(new Pose(44, 52.5, -90, AngleUnit.DEGREES)),
				moveTo(new Pose(44, 16, -90, AngleUnit.DEGREES)),
				moveTo(new Pose(54, 16, -90, AngleUnit.DEGREES)),
				moveTo(new Pose(55, 52.5, -90, AngleUnit.DEGREES)),
				moveTo(new Pose(55, 16, -90, AngleUnit.DEGREES)),
				moveTo(new Pose(61, 14, -90, AngleUnit.DEGREES)),
				moveTo(new Pose(61, 52.5, -90, AngleUnit.DEGREES))
		);
	}

	private AutoAction collectHumanPlayerSpecimen() {
		return sequentially(
				simultaneously(
						moveTo(new Pose(35.25, 48.5, 90, AngleUnit.DEGREES)),
						sequentially(
								armExtension(0),
								armRotation(ArmRotationCapabilities.getTargetPositionAngle(15.7))
						),
						claw(ClawCapabilities.ClawPreset.FORWARD, true, true)
				),
				armExtension((int) (ArmExtensionCapabilities.POSITION_FULLY_EXTENDED * 0.56)),
//				moveTo(new Pose(35.25, 57, 90, AngleUnit.DEGREES)),
//				new AutoAction() {
//					@Override
//					public void init() {
//					}
//
//					@Override
//					public boolean isComplete() {
//						double detectionHorizontalCenter = CompetitionAuto.this.processor.detectedSpecimen.width + CompetitionAuto.this.processor.detectedSpecimen.height;
//						double difference = SampleAlignmentProcessor.CENTER_X_POSITION - detectionHorizontalCenter;
//						double diffVert = CompetitionAuto.this.processor.detectedSpecimen.height / SampleAlignmentProcessor.SAMPLE_HEIGHT;
//						double diffHoriz = CompetitionAuto.this.processor.detectedSpecimen.width / SampleAlignmentProcessor.SAMPLE_WIDTH;
//
//						return Math.abs(diffHoriz - diffVert) < 0.1 && Math.abs(difference) < 10;
//					}
//
//					@Override
//					public void loop() {
//						final double movementSpeed = 3;
//						final double rotationalSpeed = 20;
//						double detectionHorizontalCenter = CompetitionAuto.this.processor.detectedSpecimen.x + CompetitionAuto.this.processor.detectedSpecimen.width / 2d;
//						double difference = detectionHorizontalCenter - SampleAlignmentProcessor.CENTER_X_POSITION;
//
//						double vx = difference / 50;
//
//						double dh = 90 - CompetitionAuto.this.localizer.getFieldCentric().getHeading(AngleUnit.DEGREES);
//						double vh = rotationalSpeed / (1 + Math.pow(2.2 * Math.E, -dh)) - (rotationalSpeed / 2);
//
//						CompetitionAuto.this.driver.setVelocityFieldCentric(
//								CompetitionAuto.this.pose,
//								new MovementVector(vx, 0, vh, AngleUnit.DEGREES)
//						);
//					}
//				},
//				simultaneously(
//						armRotation(ArmRotationCapabilities.getTargetPositionAngle(19)),
//						moveTo(new Pose(33.25, 56, 90, AngleUnit.DEGREES))
//				),
//				new AutoAction() {
//					@Override
//					public void init() {
//					}
//
//					@Override
//					public boolean isComplete() {
//						CompetitionAuto.this.localizer.setFieldCentric(new Pose(34.5, 56, CompetitionAuto.this.localizer.getFieldCentric().getHeading(AngleUnit.DEGREES), AngleUnit.DEGREES));
//						return true;
//					}
//
//					@Override
//					public void loop() {
//					}
//				},
				claw(ClawCapabilities.ClawPreset.FORWARD, false, true),
				armRotation(ArmRotationCapabilities.getTargetPositionAngle(25))
		);
	}

	private AutoAction park() {
		return simultaneously(
				moveTo(new Pose(58, 58, -90, AngleUnit.DEGREES)),
				armRotation(0),
				armExtension(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED),
				claw(ClawCapabilities.ClawPreset.DOWN, true, true)
		);
	}

	@Override
	public void loop() {
		super.loop();

		this.localizer.loop();
		this.pose = this.localizer.getFieldCentric();
		this.autoSequence.loop();
	}
}
