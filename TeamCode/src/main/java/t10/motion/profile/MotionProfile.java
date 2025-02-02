package t10.motion.profile;

public interface MotionProfile {
	/**
	 * Calculates the proportion of the max velocity at a given position.
	 *
	 * @param proportion Proportion of the total distance, between 0 and 1.
	 * @return Proportion of the max velocity.
	 */
	double getVelocity(double proportion);
}
