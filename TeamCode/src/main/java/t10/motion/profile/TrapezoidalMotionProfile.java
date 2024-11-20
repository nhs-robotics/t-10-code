package t10.motion.profile;

import t10.utils.MathUtils;

/**
 * Represents a trapezoidal motion profile, which is a type of motion profile that has a trapezoidal shape.
 * The profile consists of three phases: acceleration, cruise, and deceleration.
 */
public class TrapezoidalMotionProfile implements MotionProfile {
    private final double accelerateEndDistance;
    private final double cruiseEndDistance;
    private final double initialVelocity;
    private final double acceleration;
    private final double distance;
    private final double peakVelocity;

    public TrapezoidalMotionProfile(
            double initialVelocity,
            double targetVelocity,
            double endVelocity,
            double acceleration,
            double distance
    ) {
        this.initialVelocity = initialVelocity;
        this.acceleration = acceleration;
        this.distance = distance;
        double fullAccelerationDistance = MathUtils.solveDisplacement(targetVelocity, initialVelocity, acceleration);
        double targetToEndVelocityDistance = MathUtils.solveDisplacement(endVelocity, targetVelocity, -1 * acceleration);

        if (fullAccelerationDistance + targetToEndVelocityDistance < distance) {
            // WILL cruise.
            this.peakVelocity = targetVelocity;
            this.accelerateEndDistance = fullAccelerationDistance;
            this.cruiseEndDistance = distance - fullAccelerationDistance - targetToEndVelocityDistance;
        } else {
            // Will NOT cruise.

            // How this is derived:
            //
            // acceleration distance (da) = (peakVelocity^2 - initialVelocity^2) / (2 * acceleration)
            // deceleration distance (dd) = (endVelocity^2 - peakVelocity^2) / (2 * -acceleration)
            // da + dd = distance (d)
            // (peakVelocity^2 - initialVelocity^2) / (2 * acceleration) + (endVelocity^2 - peakVelocity^2) / (2 * -acceleration) = d
            // Solve for peakVelocity:
            this.peakVelocity = Math.sqrt((2 * this.distance * this.acceleration + this.initialVelocity * this.initialVelocity + endVelocity * endVelocity) / 2);
            this.accelerateEndDistance = MathUtils.solveDisplacement(peakVelocity, initialVelocity, acceleration);
            this.cruiseEndDistance = 0;
        }
    }

    @Override
    public double getVelocity(double position) {
        if (position < this.accelerateEndDistance) {
            // Acceleration phase
            return MathUtils.solveVelocity(this.initialVelocity, this.acceleration, position);
        } else if (position < this.cruiseEndDistance) {
            // Cruise phase
            return this.peakVelocity;
        } else if (position < this.distance) {
            // Deceleration phase
            return MathUtils.solveVelocity(this.peakVelocity, -1 * this.acceleration, this.distance - this.cruiseEndDistance - position);
        } else {
            // After the motion profile is complete
            return 0;
        }
    }
}