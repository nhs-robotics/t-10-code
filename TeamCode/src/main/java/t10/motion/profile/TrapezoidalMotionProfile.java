package t10.motion.profile;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import intothedeep.Constants;

import t10.geometry.MovementVector;
import t10.geometry.Pose;
import t10.utils.MathUtils;

/**
 * Represents a trapezoidal motion profile, which is a type of motion profile that has a trapezoidal shape.
 * The profile consists of three phases: acceleration, cruise, and deceleration.
 * <p>
 * DISTANCE determines the direction; initial and end velocity should be properly signed, target velocity/acceleration/lookahead should be positive
 */
public class TrapezoidalMotionProfile implements IMotionProfile {
	private boolean initialized = false;
	private TrapMotionInLine veloX, veloY;
	private TrapMotionRotate veloH;
	MovementVector initialVelocity;
	MovementVector maxVelocity;
	MovementVector endVelocity;
	MovementVector maxAcceleration;
	Pose initialPose;
	Pose currentPose;
	Pose finalPose;
	double lookAhead;


	@Override
	public MovementVector calculate(MovementVector initialVelocity, MovementVector maxVelocity, MovementVector endVelocity, MovementVector maxAcceleration, Pose initialPose, Pose currentPose, Pose finalPose, double lookAhead) {
		updateProfiles(initialVelocity, maxVelocity, endVelocity, maxAcceleration, initialPose, finalPose, lookAhead);

		double vx = veloX.getVelocity(currentPose.getX() - initialPose.getX());
		double vy = veloY.getVelocity(currentPose.getY() - initialPose.getY());
		double vh = veloH.getRotateVelo(currentPose.getHeading(AngleUnit.RADIANS),AngleUnit.RADIANS);
		return new MovementVector(vx,vy,vh, null);
	}

	private void updateProfiles(MovementVector initialVelocity, MovementVector maxVelocity, MovementVector endVelocity, MovementVector maxAcceleration, Pose initialPose, Pose finalPose, double lookAhead) {
		if (this.initialVelocity == initialVelocity && this.maxVelocity == maxVelocity && this.endVelocity == endVelocity && this.maxAcceleration == maxAcceleration && this.initialPose == initialPose && this.finalPose == finalPose && this.lookAhead == lookAhead) {

		}
		else {
			veloX = new TrapMotionInLine(initialVelocity.getHorizontal(), maxVelocity.getHorizontal(), endVelocity.getHorizontal(), maxAcceleration.getHorizontal(), initialPose.getX(), finalPose.getX(), lookAhead);
			veloY = new TrapMotionInLine(initialVelocity.getVertical(), maxVelocity.getVertical(), endVelocity.getVertical(), maxAcceleration.getVertical(), initialPose.getY(), finalPose.getY(), lookAhead);

			veloH = new TrapMotionRotate(
					initialVelocity.getAngleUnit().toRadians(initialVelocity.getRotation()),
					maxVelocity.getAngleUnit().toRadians(maxVelocity.getRotation()),
					endVelocity.getAngleUnit().toRadians(endVelocity.getRotation()),
					maxAcceleration.getAngleUnit().toRadians(maxAcceleration.getRotation()),
					initialPose.getHeading(AngleUnit.RADIANS),
					finalPose.getHeading(AngleUnit.RADIANS),
					lookAhead,
					Constants.Robot.ROBOT_WHEEL_DIST_FROM_CENTER /*todo: fix this number*/
			);
			this.initialVelocity = initialVelocity;
			this.maxVelocity = maxVelocity;
			this.endVelocity = endVelocity;
			this.maxAcceleration = maxAcceleration;
			this.initialPose = initialPose;
			this.finalPose = finalPose;
			this.lookAhead = lookAhead;
		}
	}
}

class TrapMotionInLine {
	private double accelerateEndDistance;
	private double cruiseEndDistance;
	private double initialVelocity;
	private double firstAcceleration, secondAcceleration;
	private double distance;
	private double peakVelocity;
	private double lookAhead;
	private double direction;
	private boolean finished = false;
	public boolean changedDirection = true;
	private double deltaDistMin = 0;
	public String state = "init";

	 public TrapMotionInLine(
			double initialVelocity,
			double maxSpeed,
			double endVelocity,
			double acceleration,
			double start,
			double end,
			double lookAhead
	) {
		// Validate inputs
		if (maxSpeed < 0 || acceleration < 0 || lookAhead < 0) {
			throw new IllegalArgumentException("check that everything that should be positive is positive");
		}

		if (maxSpeed < Math.abs(endVelocity)) {
			throw new IllegalArgumentException();
		}

		if (Math.abs(distance) < Math.abs(MathUtils.solveDisplacement(endVelocity, initialVelocity, acceleration * Math.signum(endVelocity - initialVelocity)))) {
			throw new RuntimeException("parameters have no solution - I need more distance to accelerate");
		}

		// Calculate profile
		this.initialVelocity = initialVelocity;
		this.direction = Math.signum(distance);
		this.distance = distance;

		if (Math.abs(initialVelocity) > maxSpeed && Math.signum(initialVelocity) == direction) {
			firstAcceleration = acceleration * direction * -1;
		} else {
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
		this.lookAhead = lookAhead;
		if (Math.signum(initialVelocity) != direction) {
			changedDirection = false;
		}
		deltaDistMin = MathUtils.solveDisplacement(0, initialVelocity, firstAcceleration);
	}


	/**
	 * @param deltaDistance Should be signed - doesn't matter which way is positive, as long as you stick to it
	 */
	public double getVelocity(double deltaDistance) {
		if (!changedDirection) {
			deltaDistance += lookAhead * Math.signum(initialVelocity);
		} else {
			deltaDistance += lookAhead * direction;
		}

		if (Math.abs(deltaDistance) < Math.abs(this.accelerateEndDistance) || Math.signum(deltaDistance * accelerateEndDistance) == -1) {
			state = "accelerating";
			// Acceleration phase
			if (!changedDirection) {
				if (Math.abs(deltaDistance) < Math.abs(deltaDistMin) && Math.signum(deltaDistance * deltaDistMin) == 1) {
					return MathUtils.solveVelocity(initialVelocity, firstAcceleration, deltaDistance) * direction * -1;
				} else {
					changedDirection = true;
					return MathUtils.solveVelocity(initialVelocity, firstAcceleration, deltaDistance) * direction;
				}
			} else {
				return MathUtils.solveSpeed(initialVelocity, firstAcceleration, deltaDistance) * direction;
			}
		} else if (Math.abs(deltaDistance) < Math.abs(this.cruiseEndDistance + accelerateEndDistance)) {
			// Cruise phase
			state = "cruising";
			return this.peakVelocity;
		} else {
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


	public boolean isDone() {
		return finished;
	}
}

class TrapMotionRotate extends TrapMotionInLine {
	private double rotateVelo;
	private double circleRadius;
	private double initialAngle;

	/**
	 *
	 * @param initialAngularVelocity In radians per second
	 * @param maxAngularVelocity In radians per second
	 * @param endAngularVelocity In radians per second
	 * @param angularAcceleration In radians per second per second
	 * @param initialAngle In radians
	 * @param finalAngle In radians
	 * @param lookAhead In radians
	 * @param wheelDistFromCenter In inches
	 */
	public TrapMotionRotate (
			double initialAngularVelocity,
			double maxAngularVelocity,
			double endAngularVelocity,
			double angularAcceleration,
			double initialAngle,
			double finalAngle,
			double lookAhead,
			double wheelDistFromCenter
	) {
		super(initialAngularVelocity, maxAngularVelocity, endAngularVelocity, angularAcceleration, initialAngle, finalAngle, lookAhead);

		this.initialAngle = initialAngle;
		this.circleRadius = wheelDistFromCenter;
	}

	public double getRotateVelo(double currentAngle, AngleUnit angleUnit)
	{
		currentAngle = angleUnit.toRadians(currentAngle);
		double angularVelo = (super.getVelocity(currentAngle - initialAngle));

		//because velocity is angular velocity times radius
		return angularVelo * circleRadius;
	}
}