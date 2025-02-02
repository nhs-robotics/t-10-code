package intothedeep.auto;

import intothedeep.SnowballConfig;
import intothedeep.auto.actions.ArmExtensionAction;
import intothedeep.auto.actions.ArmRotationAction;
import intothedeep.auto.actions.ClawAction;
import intothedeep.auto.actions.CraneAction;
import intothedeep.capabilities.ArmExtensionCapabilities;
import intothedeep.capabilities.ArmRotationCapabilities;
import intothedeep.capabilities.ClawCapabilities;
import intothedeep.capabilities.CraneCapabilities;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import t10.auto.AutoAction;
import t10.auto.FollowPathAction;
import t10.auto.MoveToAction;
import t10.auto.SequentialAction;
import t10.auto.SimultaneousAction;
import t10.auto.SleepAction;
import t10.bootstrap.AutonomousOpMode;
import t10.geometry.Pose;
import t10.localizer.Localizer;
import t10.motion.mecanum.MecanumDriver;
import t10.motion.path.PurePursuitPathFollower;

import java.util.ArrayList;

public abstract class EasyAuto extends AutonomousOpMode {
	private final Pose startPose;
	public MecanumDriver driver;
	public SnowballConfig config;
	public ArmExtensionCapabilities armExtension;
	public ArmRotationCapabilities armRotation;
	public ClawCapabilities claw;
	public CraneCapabilities crane;
	public Localizer<Pose> localizer;

	public EasyAuto(Pose startPose) {
		this.startPose = startPose;
	}

	@Override
	public void init() {
		super.init();

		this.config = new SnowballConfig(this.hardwareMap);

		// Driving & Localization
		this.driver = this.config.createMecanumDriver();
		this.localizer = this.config.createLocalizer();
		this.localizer.setFieldCentric(this.startPose);

		// Capabilities
		this.armExtension = new ArmExtensionCapabilities(config);
		this.armRotation = new ArmRotationCapabilities(config);
		this.claw = new ClawCapabilities(config);
		this.crane = new CraneCapabilities(config);

		// Configure robot's initial state
		this.armRotation.setTargetPosition(ArmRotationCapabilities.POSITION_INSPECTION);
		this.armExtension.setTargetPosition(ArmExtensionCapabilities.POSITION_FULLY_RETRACTED);
		this.crane.setTargetPosition(CraneCapabilities.POSITION_BOTTOM);
		this.claw.setOpen(false);  // Keep closed to grasp a block for auto

		this.multithreadingService.execute(() -> {
			while (this.isRunning) {
				this.localizer.loop();
			}
		});

		this.multithreadingService.execute(() -> {
			while (this.isRunning) {
				this.armRotation.loop();
				this.armExtension.loop();
				this.crane.loop();
			}
		});
	}

	@Override
	public void loop() {
		this.telemetry.update();
	}

	@Override
	public void stop() {
		super.stop();
		this.driver.halt();
		this.config.armRotation.setPower(0);
		this.config.armExtension.setPower(0);
		this.config.liftRight.setPower(0);
		this.config.liftLeft.setPower(0);
	}

	public MoveToAction moveTo(Pose destinationPose) {
		return new MoveToAction(this.localizer, this.driver, destinationPose);
	}

	public ArmExtensionAction armExtension(int position) {
		return new ArmExtensionAction(this.armExtension, position);
	}

	public ArmRotationAction armRotation(int position) {
		return new ArmRotationAction(this.armRotation, position);
	}

	public CraneAction crane(int position) {
		return new CraneAction(this.crane, position);
	}

	public ClawAction claw(boolean isOpen) {
		return new ClawAction(this.claw, isOpen);
	}

	public SimultaneousAction simultaneously(AutoAction... actions) {
		return new SimultaneousAction(actions);
	}

	public FollowPathAction followPath(PurePursuitPathFollower pathFollower) {
		return new FollowPathAction(pathFollower, this.driver);
	}

	public SequentialAction sequentially(AutoAction... actions) {
		return new SequentialAction(actions);
	}

	public SleepAction sleep(long ms) {
		return new SleepAction(ms);
	}
}
