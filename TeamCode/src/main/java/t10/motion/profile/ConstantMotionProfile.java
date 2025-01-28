package t10.motion.profile;

public class ConstantMotionProfile implements MotionProfile {
	private final double velocity;

	public ConstantMotionProfile(double velocity) {
		this.velocity = velocity;
	}

	@Override
	public double getVelocity(double proportion) {
		return this.velocity;
	}
}
