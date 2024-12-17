package t10.motion.profile;

import t10.utils.MathUtils;

/**
 * Represents a trapezoidal motion profile, which is a type of motion profile that has a trapezoidal shape.
 * The profile consists of three phases: acceleration, cruise, and deceleration.
 *
 * DISTANCE determines the direction; initial and end velocity should be properly signed, target velocity/acceleration/lookahead should be positive
 */
public class TrapezoidalMotionProfile implements MotionProfile {
    private final double accelerateEndDistance;
    private final double cruiseEndDistance;
    private final double initialVelocity;
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
            double initialVelocity,
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
        if(Math.abs(distance) < Math.abs(MathUtils.solveDisplacement(endVelocity,initialVelocity,acceleration * Math.signum(endVelocity - initialVelocity))))
        {
            throw new RuntimeException("parameters have no solution - I need more space to accelerate");
        }
        this.initialVelocity = initialVelocity;
        this.direction = Math.signum(distance);
        this.distance = distance;
        if(Math.abs(initialVelocity) > maxSpeed && Math.signum(initialVelocity) == direction)
        {
            firstAcceleration = acceleration * direction * -1;
        }
        else {
            firstAcceleration = acceleration * direction;
        }
        secondAcceleration = acceleration * direction * -1;

        double fullAccelerationDistance = Math.abs(MathUtils.solveDisplacement(maxSpeed, initialVelocity, firstAcceleration)) * direction;
        double targetToEndVelocityDistance = Math.abs(MathUtils.solveDisplacement(endVelocity, maxSpeed, secondAcceleration)) * direction;

        if (Math.abs(fullAccelerationDistance + targetToEndVelocityDistance) < distance) {
            // WILL cruise.
            this.peakVelocity = maxSpeed * direction;
            this.accelerateEndDistance = fullAccelerationDistance;
            this.cruiseEndDistance = distance - fullAccelerationDistance - targetToEndVelocityDistance;
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
            this.peakVelocity = Math.sqrt((2 * this.distance * this.firstAcceleration + this.initialVelocity * this.initialVelocity + endVelocity * endVelocity) / 2) * direction;
            this.accelerateEndDistance = MathUtils.solveDisplacement(peakVelocity, initialVelocity, firstAcceleration);
            this.cruiseEndDistance = 0;
        }

        this.endVelocity = endVelocity;
        this.lookAhead = lookAhead;
        if(Math.signum(initialVelocity) != direction) {
            changedDirection = false;
        }
        deltaDistMin = MathUtils.solveDisplacement(0,initialVelocity,firstAcceleration);
    }


    /**
     * @param deltaDistance Should be signed - doesn't matter which way is positive, as long as you stick to it
     */
    @Override
    public double getVelocity(double deltaDistance) {
        if(!changedDirection)
        {
            deltaDistance += lookAhead * Math.signum(initialVelocity);
        }
        else {
            deltaDistance += lookAhead * direction;
        }

        if (Math.abs(deltaDistance) < Math.abs(this.accelerateEndDistance) || Math.signum(deltaDistance * accelerateEndDistance) == -1) {
            state = "accelerating";
            // Acceleration phase
            if(!changedDirection)
            {
                if(Math.abs(deltaDistance) < Math.abs(deltaDistMin) && Math.signum(deltaDistance * deltaDistMin) == 1)
                {
                    return MathUtils.solveVelocity(initialVelocity,firstAcceleration,deltaDistance) * direction * -1;
                }
                else {
                    changedDirection = true;
                    return MathUtils.solveVelocity(initialVelocity,firstAcceleration,deltaDistance) * direction;
                }
            }
            else {
                return MathUtils.solveSpeed(initialVelocity,firstAcceleration,deltaDistance) * direction;
            }
        } else if (Math.abs(deltaDistance) < Math.abs(this.cruiseEndDistance + accelerateEndDistance)) {
            // Cruise phase
            state = "cruising";
            return this.peakVelocity;
        }
        else {
            deltaDistance -= lookAhead * direction;
            if (Math.abs(deltaDistance) < Math.abs(this.distance)) {
                // Deceleration phase
                state = "decelerating";
                return MathUtils.solveVelocity(this.peakVelocity, secondAcceleration, deltaDistance - (accelerateEndDistance + cruiseEndDistance));
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