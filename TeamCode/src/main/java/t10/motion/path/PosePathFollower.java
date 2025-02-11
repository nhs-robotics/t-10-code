package t10.motion.path;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.geometry.MovementVector;
import t10.geometry.Pose;
import t10.localizer.Localizer;
import t10.motion.mecanum.MecanumDriver;
import t10.utils.MathUtils;

// TODO: UNFINISHED, FINISH THIS. It is marked as deprecated because it is not finished yet.
@Deprecated
public class PosePathFollower {
	private final Pose[] poses;
	private final Localizer<Pose> localizer;
	private int targetPoseIdx;
	private Pose target;

	public PosePathFollower(Pose[] poses, Localizer<Pose> localizer) {
		this.poses = poses;
		this.localizer = localizer;
		this.targetPoseIdx = -1;
	}

	public boolean follow(MecanumDriver driver) {
		if (this.poses.length <= this.targetPoseIdx) {
			return true;
		}

		Pose currentPose = this.localizer.getFieldCentric();
		double dx = this.target.getX() - currentPose.getX();
		double dy = this.target.getY() - currentPose.getY();
		double dr = MathUtils.angleDifference(this.target.getHeading(AngleUnit.DEGREES), currentPose.getHeading(AngleUnit.DEGREES), AngleUnit.DEGREES);
		boolean hasArrived = Math.abs(dx) < 2 && Math.abs(dy) < 2 && Math.abs(dr) < 4;

		if (hasArrived) {
			boolean isComplete = nextTarget();

			if (isComplete) {
				return true;
			}

			driver.setVelocityFieldCentric(currentPose, new MovementVector(0, 0, 0, AngleUnit.DEGREES)); // TODO: Actually set velocity
		}

		return false;
	}

	private boolean nextTarget() {
		this.targetPoseIdx++;

		if (this.poses.length <= this.targetPoseIdx) {
			return true;
		}

		this.target = this.poses[this.targetPoseIdx];

		return false;
	}
}
