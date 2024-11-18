package t10.motion.profile;

public interface MotionProfile {
    /**
     * Calculates the velocity at a given position
     *
     * @param position Position at which to calculate the velocity
     * @return Velocity at the given position
     */
    double getVelocity(double position);
}
