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

import t10.auto.AutoAction;
import t10.auto.FollowPathAction;
import t10.auto.MoveToAction;
import t10.auto.SequentialAction;
import t10.auto.SimultaneousAction;
import t10.bootstrap.AutonomousOpMode;
import t10.geometry.Pose;
import t10.localizer.Localizer;
import t10.localizer.odometry.OdometryLocalizer;
import t10.motion.mecanum.MecanumDriver;
import t10.motion.path.PurePursuitPathFollower;

public abstract class EasyAuto extends AutonomousOpMode {
	private final Pose startPose;
	public MecanumDriver driver;
	public OdometryLocalizer odometry;
	public SnowballConfig config;
	public ArmExtensionCapabilities armExtension;
	public ArmRotationCapabilities armRotation;
	public ClawCapabilities claw;
	public CraneCapabilities crane;
	public Localizer localizer;

	public EasyAuto(Pose startPose) {
		this.startPose = startPose;
	}

	@Override
	public void initialize() {
		this.config = new SnowballConfig(this.hardwareMap);

		// Driving & Localization
		this.driver = config.createMecanumDriver();
		this.odometry = config.createOdometry();
		this.localizer = new Localizer(null, this.odometry, this.startPose);

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
	}

	@Override
	public void loop() {
		this.odometry.update();
		this.armRotation.update();
		this.armExtension.update();
		this.crane.update();
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

	public MoveToAction moveTo(Pose destinationPose, double speed) {
		return new MoveToAction(this.localizer, this.driver, destinationPose, speed);
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
}
