package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import intothedeep.Constants;
import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.ArmRotationCapabilities;
import intothedeep.capabilities.ClawCapabilities;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import intothedeep.capabilities.CraneCapabilities;

import t10.auto.AutoAction;
import t10.auto.MoveToAction;
import t10.auto.SequentialAction;
import t10.gamepad.GController;
import t10.gamepad.input.types.GButton;
import t10.geometry.Point;
import t10.geometry.Pose;
import t10.localizer.AprilTagLocalizer;
import t10.metrics.Metric;
import t10.vision.SampleAlignmentProcessor;

@TeleOp
public class DemoAuto extends EasyAuto {
	private SequentialAction autoSequence;
	private GController controller;

	@Metric
	public Pose pose;

	public DemoAuto() {
		super(new Pose(0, 0, 0, AngleUnit.DEGREES));
	}

	@Override
	public void init() {
		super.init();
		this.controller = new GController(this.gamepad1);

		this.autoSequence = sequentially(

				waitForButton(controller.a),
				claw(ClawCapabilities.ClawPreset.DOWN, true, true),
				armRotation(ArmRotationCapabilities.POSITION_INSPECTION),
				sleep(500),
				armRotation(ArmRotationCapabilities.POSITION_FULLY_DOWNWARDS),
				sleep(500),
				armRotation(ArmRotationCapabilities.POSITION_INSPECTION),
				sleep(500),
				armRotation(ArmRotationCapabilities.POSITION_FULLY_DOWNWARDS),
				waitForButton(controller.a),
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(20, 0, -90, AngleUnit.DEGREES),
						1.5,
						1.25,
						60, 90),
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(20, 20, 180, AngleUnit.DEGREES),
						1.5,
						1.25,
						60, 90),
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(0, 20, 90, AngleUnit.DEGREES),
						1.5,
						1.25,
						60, 90),
				new MoveToAction(
						this.localizer,
						this.driver,
						new Pose(0, 0, 0, AngleUnit.DEGREES),
						1.5,
						1.25,
						60, 90),
				waitForButton(controller.a),
				simultaneously(
						armExtension(ArmExtensionCapabilities.POSITION_FULLY_EXTENDED),
						armRotation(ArmRotationCapabilities.POSITION_INSPECTION),
						crane(CraneCapabilities.POSITION_LOW_BASKET)),
				waitForButton(controller.a),
				simultaneously(
						armExtension(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED),
						armRotation(ArmRotationCapabilities.POSITION_FULLY_DOWNWARDS),
						crane(CraneCapabilities.POSITION_BOTTOM))
		);
	}

	@Override
	public void init_loop() {
		super.init_loop();

		this.armRotation.loop();
		this.armExtension.loop();
		this.crane.loop();
		this.claw.loop();
		this.localizer.loop();
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
