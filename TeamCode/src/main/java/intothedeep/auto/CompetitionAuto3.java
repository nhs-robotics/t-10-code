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
				shuffleSamples(10),
				shuffleSamples(17),
				getSpecimenFromObservationZone(),
				scoreSpecimenFromOver(9),
				getSpecimenFromObservationZone(),
				scoreSpecimenFromOver(8),
				getSpecimenFromObservationZone(),
				scoreSpecimenFromOver(7),
				getSpecimenFromObservationZone(),
				scoreSpecimenFromOver(6),
				park()
		);
	}

	private AutoAction transitionToShuffle() {
		return simultaneously(
			armExtension(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED),
			claw(ClawCapabilities.ClawPreset.FORWARD, true, false),
			new MoveToAction(
					this.localizer,
					this.driver,
					new Pose(38, 26, -90, AngleUnit.DEGREES),
					5,
					100,
					100, 60
			)
		);
	}

	private AutoAction shuffleSamples(int yOffset) {
		return sequentially(
				// go to shuffle position
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(38 + yOffset, 26, -90, AngleUnit.DEGREES),
						3.5,
						100,
						80, 60
				),

				// position roughly behind the target sample
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(45 + yOffset, 16, -90, AngleUnit.DEGREES),
						3,
						100,
						60, 60
				),

				// shuffle target sample into observation zone
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(45 + yOffset, 48, -90, AngleUnit.DEGREES),
						5,
						100,
						80, 60
				)

//				new MoveToAction(
//						this.localizer,
//						this.driver,
//						new Pose(45 + yOffset, 40, -90, AngleUnit.DEGREES),
//						5,
//						3,
//						50, 60
//				)
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
							10,
							40,
							60
					),
					armRotation(380)
				),
				claw(ClawCapabilities.ClawPreset.FORWARD, true, true),
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(35, 58, 90, AngleUnit.DEGREES),
						2,
						3,
						30, 60
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
							new Pose(yPosition, 47, -90, AngleUnit.DEGREES),
							3,
							10,
							60,
							80
					),
					armExtension(ArmExtensionCapabilities.POSITION_FULLY_EXTENDED),
					claw(ClawCapabilities.ClawPreset.FORWARD, false, true)
				),
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(yPosition, 40.5, -90, AngleUnit.DEGREES),
						3,
						100,
						30,
						50
				),
				claw(ClawCapabilities.ClawPreset.DOWN, false, true),
				simultaneously(
						new MoveToAction(
								this.localizer,
								this.driver,
								new Pose(yPosition, 45, -90, AngleUnit.DEGREES),
								3,
								100,
								50,
								50
						),
						armExtension(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED),
						claw(ClawCapabilities.ClawPreset.DOWN, true, true)
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
