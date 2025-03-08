package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.ClawCapabilities;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import java.security.InvalidParameterException;

import intothedeep.capabilities.CraneCapabilities;

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
	private VoltageSensor voltageSensor;
	@Metric
	public double voltage;

	public CompetitionAuto3() {
		super(new Pose(36, 62, -90, AngleUnit.DEGREES));
	}

	@Override
	public void init() {
		super.init();
		voltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");
		this.autoSequence = sequentially(
				transitionToShuffle(),
				shuffleSamples(0),
				shuffleSamples(12),
				shuffleSamples(17),
				getSpecimenFromObservationZone(),
				scoreSpecimenFromOver(12),
				getSpecimenFromObservationZone(),
				scoreSpecimenFromOver(10),
				getSpecimenFromObservationZone(),
				scoreSpecimenFromOver(8),
				park()
		);
	}

	private AutoAction transitionToShuffle() {
		return simultaneously(
			armExtension(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED),
			claw(ClawCapabilities.ClawPreset.FORWARD, true, false)
//			new MoveToAction(
//					this.localizer,
//					this.driver,
//					new Pose(36, 40, -90, AngleUnit.DEGREES),
//					5,
//					3,
//					50,
//					60
//			)
		);
	}

	private AutoAction shuffleSamples(int yOffset) {
		return sequentially(
				// go from start to shuffle first one
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(38+yOffset, 18, -90, AngleUnit.DEGREES),
						3,
						3,
						100, 60
				),

//				// position precisely behind first one
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(45 + yOffset, 20, -90, AngleUnit.DEGREES),
						2,
						3,
						35, 60
				),
//
//				// shuffle first one into observation zone
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(45 + yOffset, 53, -90, AngleUnit.DEGREES),
						3,
						3,
						50, 60
				),

				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(45 + yOffset, 40, -90, AngleUnit.DEGREES),
						2,
						3,
						50, 60
				)
		);
	}

	private AutoAction park() {
		return simultaneously(
				armExtension(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED),
				claw(ClawCapabilities.ClawPreset.FORWARD, true, false),
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(50, 57, -90, AngleUnit.DEGREES),
						3,
						3,
						80,
						50
				),
				armRotation(0)
		);
	}

	private AutoAction getSpecimenFromObservationZone() {
		return sequentially(
				simultaneously(
					armExtension(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED),
					claw(ClawCapabilities.ClawPreset.FORWARD, true, true),
					new MoveToAction(
							this.localizer,
							this.driver,
							new Pose(37, 49, 90, AngleUnit.DEGREES),
							4,
							2,
							40,
							60
					),
					armRotation(380)
				),
				claw(ClawCapabilities.ClawPreset.FORWARD, true, true),
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(37, 59, 90, AngleUnit.DEGREES),
						4,
						3,
						15, 60
				),
				claw(ClawCapabilities.ClawPreset.FORWARD, false, true)
//				new MoveToAction(
//						this.localizer,
//						this.driver,
//						new Pose(37, 49, 0, AngleUnit.DEGREES),
//						4,
//						3,
//						15, 60
//				)
		);
	}

	private AutoAction scoreSpecimen(double yPosition) {
		return sequentially(
				armRotation(628),
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(yPosition, 54, -90, AngleUnit.DEGREES),
						2,
						100,
						30,
						50
				),
				armExtension(-1530),
				claw(ClawCapabilities.ClawPreset.UP, false, true),
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(yPosition, 31.5, -90, AngleUnit.DEGREES),
						3,
						100,
						30,
						50
				),
				claw(ClawCapabilities.ClawPreset.UP, true, true)
		);
	}

	private AutoAction scoreSpecimenFromOver(double yPosition) {
		return sequentially(
				armRotation(887),
				simultaneously(
					new MoveToAction(
							this.localizer,
							this.driver,
							new Pose(yPosition, 52, -90, AngleUnit.DEGREES),
							2,
							100,
							50,
							50
					),
					armExtension(ArmExtensionCapabilities.POSITION_FULLY_EXTENDED),
					claw(ClawCapabilities.ClawPreset.FORWARD, false, true)
				),
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(yPosition, 41, -90, AngleUnit.DEGREES),
						3,
						100,
						30,
						50
				),
				claw(ClawCapabilities.ClawPreset.DOWN, false, true),
				simultaneously(
						claw(ClawCapabilities.ClawPreset.DOWN, true, true),
						new MoveToAction(
								this.localizer,
								this.driver,
								new Pose(yPosition, 45, -90, AngleUnit.DEGREES),
								3,
								100,
								50,
								50
						)
				)
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
		this.voltage = this.voltageSensor.getVoltage();
	}
}
