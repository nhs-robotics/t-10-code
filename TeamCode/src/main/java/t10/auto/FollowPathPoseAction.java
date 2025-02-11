package t10.auto;

import t10.motion.mecanum.MecanumDriver;
import t10.motion.path.PosePathFollower;

public class FollowPathPoseAction implements AutoAction {
	private final PosePathFollower pathFollower;
	private final MecanumDriver driver;
	private boolean isComplete;

	public FollowPathPoseAction(PosePathFollower pathFollower, MecanumDriver driver) {
		this.pathFollower = pathFollower;
		this.driver = driver;
	}

	@Override
	public void init() {
	}

	@Override
	public boolean isComplete() {
		return this.isComplete;
	}

	@Override
	public void loop() {
		this.isComplete = this.pathFollower.follow(this.driver);
	}
}
