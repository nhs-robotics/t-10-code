package t10.motion.profile;

import t10.utils.MathUtils;

/**
 * Represents a trapezoidal motion profile, which is a type of motion profile that has a trapezoidal shape.
 * The profile consists of three phases: acceleration, cruise, and deceleration.
 *
 * DISTANCE determines the direction; initial and end velocity should be properly signed, target velocity/acceleration/lookahead should be positive
 */
public class TrapezoidalMotionProfile implements MotionProfile {
    private final double accelerateDistance;
    private final double cruiseDistance;
    private final double firstAcceleration, secondAcceleration;
    private final double distance;
    private final double peakVelocity;
    private final double endVelocity;
    private final double lookAhead;
    private final double direction;
    private boolean finished = false;
    public boolean changedDirection = true;
    private double deltaDistMin = 0;
    public String state = "init";



    public TrapezoidalMotionProfile(
            double maxSpeed,
            double endVelocity,
            double acceleration,
            double distance,
            double lookAhead
    ) {
        //check for bad inputs
        if(maxSpeed < 0 || acceleration < 0 || lookAhead < 0) {
            throw new IllegalArgumentException("check that everything that should be positive is positive");
        }
        if(maxSpeed < Math.abs(endVelocity))
        {
            throw new IllegalArgumentException();
        }
        //check minimal viability
        if(Math.abs(distance) < Math.abs(MathUtils.solveDisplacement(endVelocity,0,acceleration * Math.signum(endVelocity))))
        {
            throw new RuntimeException("parameters have no solution - I need more space to accelerate");
        }
        this.direction = Math.signum(distance);
        this.distance = distance;

        firstAcceleration = acceleration * direction;

        secondAcceleration = acceleration * direction * -1;

        double fullAccelerationDistance = Math.abs(MathUtils.solveDisplacement(maxSpeed, 0, firstAcceleration)) * direction;
        double targetToEndVelocityDistance = Math.abs(MathUtils.solveDisplacement(endVelocity, maxSpeed, secondAcceleration)) * direction;

        if (Math.abs(fullAccelerationDistance + targetToEndVelocityDistance) < distance) {
            // WILL cruise.
            this.peakVelocity = maxSpeed * direction;
            this.accelerateDistance = fullAccelerationDistance;
            this.cruiseDistance = distance - fullAccelerationDistance - targetToEndVelocityDistance;
        } else {
            // Will NOT cruise.


            // Only triggers if the V-X graph can look like a triangle, so the accelerations will be in opposite directions and the math below checks out.
            // How this is derived:
            //
            // acceleration distance (da) = (peakVelocity^2 - initialVelocity^2) / (2 * acceleration)
            // deceleration distance (dd) = (endVelocity^2 - peakVelocity^2) / (2 * -acceleration)
            // da + dd = distance (d)
            // (peakVelocity^2 - initialVelocity^2) / (2 * acceleration) + (endVelocity^2 - peakVelocity^2) / (2 * -acceleration) = d
            // Solve for peakVelocity:
            this.peakVelocity = Math.sqrt((2 * this.distance * this.firstAcceleration + endVelocity * endVelocity) / 2) * direction;
            this.accelerateDistance = MathUtils.solveDisplacement(peakVelocity, 0, firstAcceleration);
            this.cruiseDistance = 0;
        }

        this.endVelocity = endVelocity;
        this.lookAhead = lookAhead;
    }


    /**
     * @param deltaDistance Should be signed - doesn't matter which way is positive, as long as you stick to it
     */
    @Override
    public double getVelocity(double deltaDistance) {

        deltaDistance += lookAhead * direction;


        if (Math.abs(deltaDistance) < Math.abs(this.accelerateDistance)) {
            state = "accelerating";
            // Acceleration phase

            return MathUtils.solveSpeed(0, firstAcceleration, deltaDistance) * direction;

        } else if (Math.abs(deltaDistance) < Math.abs(this.cruiseDistance + accelerateDistance)) {
            // Cruise phase
            state = "cruising";
            return this.peakVelocity;
        }
        else {
            deltaDistance -= lookAhead * direction * ((Math.abs(distance) - Math.abs(deltaDistance - lookAhead * direction)) / Math.abs(distance - accelerateDistance - cruiseDistance));

            if (Math.abs(deltaDistance) < Math.abs(this.distance)) {
                // Deceleration phase
                state = "decelerating";
                return MathUtils.solveVelocity(this.peakVelocity, secondAcceleration, deltaDistance - (accelerateDistance + cruiseDistance));
            } else {
                // After the motion profile is complete
                finished = true;
                return 0;
            }
        }
    }


    public boolean isDone()
    {
        return finished;
    }
}