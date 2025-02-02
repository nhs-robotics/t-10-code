package t10.auto;

import t10.motion.mecanum.MecanumDriver;
import t10.motion.path.PurePursuitPathFollower;

public class FollowPathAction implements AutoAction {
    private final PurePursuitPathFollower pathFollower;
    private final MecanumDriver driver;
    private boolean isComplete;

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
