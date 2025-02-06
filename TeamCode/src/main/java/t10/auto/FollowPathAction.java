package t10.auto;

import t10.motion.mecanum.MecanumDriver;
import t10.motion.path.PurePursuitPathFollower;

/**
 * An {@link AutoAction} that uses {@link PurePursuitPathFollower} to follow a path.
 */
public class FollowPathAction implements AutoAction {
	/**
	 * The path follower to use.
	 */
	private final PurePursuitPathFollower pathFollower;

	/**
	 * The driver to drive the robot along the path.
	 */
	private final MecanumDriver driver;

	/**
	 * Keeps track of whether the path has been fully followed or not.
	 */
	private boolean isComplete;

	/**
	 * Creates a {@link FollowPathAction}.
	 *
	 * @param pathFollower The path to use. <strong>Important: Make sure you set the localizer on this {@link PurePursuitPathFollower}!</strong>
	 * @param driver       The driver to drive the robot with along this path.
	 */
	public FollowPathAction(PurePursuitPathFollower pathFollower, MecanumDriver driver) {
		this.pathFollower = pathFollower;
		this.driver = driver;
		this.isComplete = false;
	}

	@Override
	public void init() {
	}

	@Override
	public void loop() {
		this.isComplete = this.pathFollower.follow(this.driver);
	}

	@Override
	public boolean isComplete() {
		return this.isComplete;
	}
}
