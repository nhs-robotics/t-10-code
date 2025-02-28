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
	public TrapMotionInLine veloX, veloY;
	public TrapMotionRotate veloH;
	MovementVector initialVelocity;
	MovementVector maxVelocity;
	MovementVector endVelocity;
	MovementVector maxAcceleration;
	Pose initialPose;
	Pose currentPose;
	Pose finalPose;
	double lookAhead;
	Integer triggers = 0;
	public int calcs = 0;
	String[] state = {"0","0","0","0"};
	public double[] peakVelocity = {0,0,0};


	@Override
	public MovementVector calculate(MovementVector initialVelocity, MovementVector maxVelocity, MovementVector minVelocity, MovementVector endVelocity, MovementVector maxAcceleration, Pose initialPose, Pose currentPose, MovementVector currentVelocity, Pose finalPose, double lookAhead) {
		updateProfiles(initialVelocity, maxVelocity, endVelocity, maxAcceleration, initialPose, finalPose, lookAhead);


		double vx = veloX.getVelocity(currentPose.getX() - initialPose.getX(), minVelocity.getHorizontal());
		double vy = veloY.getVelocity(currentPose.getY() - initialPose.getY(), minVelocity.getVertical());
		double vh = veloH.getRotateVelo(currentPose.getHeading(AngleUnit.RADIANS), minVelocity.getAngleUnit().toRadians(minVelocity.getRotation()), AngleUnit.RADIANS);

		state[0] = veloY.getState();
		state[1] = veloX.getState();
		state[2] = veloH.getState();
		state[3] = triggers.toString();

		peakVelocity[0] = veloY.peakVelocity;
		peakVelocity[1] = veloX.peakVelocity;
		peakVelocity[2] = veloH.peakVelocity;
		calcs++;

		return new MovementVector(vy,vx,vh, null);
	}

	public String[] getState() {return state;}

	public Double[] getPeakVelos() {
		 Double[] velos = {veloY.peakVelocity, veloX.peakVelocity, veloH.peakVelocity};
		 return velos;
	}

	public Double[] getCruiseDists() {
		Double[] dists = {veloY.cruiseDistance, veloX.cruiseDistance, veloH.cruiseDistance};
		return dists;
	}

	public Double[] getAccelDists() {
		Double[] dists = {veloY.accelerateDistance, veloX.accelerateDistance, veloH.accelerateDistance};
		return dists;
	}

	private void updateProfiles(MovementVector initialVelocity, MovementVector maxVelocity, MovementVector endVelocity, MovementVector maxAcceleration, Pose initialPose, Pose finalPose, double lookAhead) {
		if (triggers == 0 || !(this.initialVelocity.equals(initialVelocity) && this.maxVelocity.equals(maxVelocity) && this.endVelocity.equals(endVelocity) && this.maxAcceleration.equals(maxAcceleration) && this.initialPose.equals(initialPose) && this.finalPose.equals(finalPose) && this.lookAhead == lookAhead)) {
			triggers++;
			veloX = new TrapMotionInLine(initialVelocity.getHorizontal(), maxVelocity.getHorizontal(), endVelocity.getHorizontal(), maxAcceleration.getHorizontal(), initialPose.getX(), finalPose.getX(), lookAhead);
			veloY = new TrapMotionInLine(initialVelocity.getVertical(), maxVelocity.getVertical(), endVelocity.getVertical(), maxAcceleration.getVertical(), initialPose.getY(), finalPose.getY(), lookAhead);

			veloH = new TrapMotionRotate(
					initialVelocity.getAngleUnit().toRadians(initialVelocity.getRotation()),
					maxVelocity.getAngleUnit().toRadians(maxVelocity.getRotation()),
					endVelocity.getAngleUnit().toRadians(endVelocity.getRotation()),
					maxAcceleration.getRotation(),
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
	public double accelerateDistance;
	public double cruiseDistance;
	public double initialVelocity;
	public double acceleration;
	public double distance;
	public double peakVelocity;
	public double lookAhead;
	public double direction;
	public boolean finished;
	public double deltaDistMin = 0;
	public double finalVelocity;
	public String state = "init";
	private double time = (double) System.currentTimeMillis() / 1000;

	public double getPeakVelocity() {
		return peakVelocity;
	}

	 public TrapMotionInLine(
			double initialVelocity,
			double maxSpeed,
			double endVelocity,
			double acceleration,
			double start,
			double end,
			double lookAhead
	) {

		finished = false;
		 // Calculate profile
		 this.initialVelocity = initialVelocity;
		 this.distance = end - start;
		 this.direction = Math.signum(distance);
		 this.finalVelocity = endVelocity;
		 this.acceleration = Math.abs(acceleration);


		 double fullAccelerationDistance = MathUtils.solveDisplacement(maxSpeed, initialVelocity, this.acceleration * direction) * direction;
		 double targetToEndVelocityDistance = MathUtils.solveDisplacement(endVelocity, maxSpeed, this.acceleration * direction * -1) * direction;

		 if (Math.abs(fullAccelerationDistance + targetToEndVelocityDistance) < Math.abs(distance)) {
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
			 this.peakVelocity = Math.sqrt((2 * Math.abs(this.distance) * this.acceleration + this.initialVelocity * this.initialVelocity + finalVelocity * finalVelocity) / 2) * direction;
			 this.accelerateDistance = MathUtils.solveDisplacement(peakVelocity, initialVelocity, this.acceleration * direction);
			 this.cruiseDistance = 0;
		 }
		 this.lookAhead = lookAhead;
	 }


	/*public double getVelocity(double deltaDistance) {
		if (!changedDirection) {
			deltaDistance += lookAhead * Math.signum(initialVelocity);
		} else {
			deltaDistance += lookAhead * direction;
		}

		if (Math.abs(deltaDistance) < Math.abs(this.accelerateDistance) || Math.signum(deltaDistance * accelerateDistance) == -1) {
			state = "accelerating";
			// Acceleration phase
			if (!changedDirection) {
				if (Math.abs(deltaDistance) < Math.abs(deltaDistMin) && Math.signum(deltaDistance * deltaDistMin) == 1) {
					return MathUtils.solveVelocity(initialVelocity, acceleration, deltaDistance) * direction * -1;
				} else {
					changedDirection = true;
					return MathUtils.solveVelocity(initialVelocity, acceleration, deltaDistance) * direction;
				}
			} else {
				return MathUtils.solveSpeed(initialVelocity, acceleration, deltaDistance) * direction;
			}
		} else if (Math.abs(deltaDistance) < Math.abs(this.cruiseDistance + accelerateDistance)) {
			// Cruise phase
			state = "cruising";
			return this.peakVelocity;
		} else {
			deltaDistance -= lookAhead * direction;
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

	/**
	 *
	 * @param currentVelo Current velocity, in inches per second
	 * @param deltaDistance Should be signed
	 */

	public double getCruiseDistance() {
		return cruiseDistance;
	}

	public double getVelocity(double deltaDistance, double minSpeed) {
		state = "Starting to Get Velocity";
		if(deltaDistance == 0) { deltaDistance += 0.001;}

		double minDeltaDist = MathUtils.solveDisplacement(minSpeed,initialVelocity,acceleration);

		if (peakVelocity > 0) {
			if (deltaDistance < accelerateDistance) {
				if(Math.abs(deltaDistance) < Math.abs(minDeltaDist))
				{
					state = "below min";
					return Math.abs(minSpeed);
				}
				state = "accelerating";
				return MathUtils.solveVelocity(initialVelocity,acceleration,deltaDistance);

			} else if (cruiseDistance != 0 && accelerateDistance < deltaDistance && deltaDistance < accelerateDistance + cruiseDistance) {
				state = "cruising";
				return peakVelocity;
			} else if (deltaDistance > cruiseDistance + accelerateDistance && deltaDistance + lookAhead < distance) {
				state = "decelerating";
				return MathUtils.solveVelocity(peakVelocity, -Math.abs(acceleration), deltaDistance - (cruiseDistance + accelerateDistance) + lookAhead);
			} else {
				state = "finished";
				finished = true;
				return finalVelocity;
			}

		} else if (peakVelocity < 0) {
			if (deltaDistance > accelerateDistance) {
				if(Math.abs(deltaDistance) < Math.abs(minDeltaDist)){
					state = "Below Min";
					return -Math.abs(minSpeed);
				}
				state = "Accelerating";
				return direction * MathUtils.solveVelocity(initialVelocity,-Math.abs(acceleration), deltaDistance);
			} else if (cruiseDistance != 0 && accelerateDistance > deltaDistance && deltaDistance > accelerateDistance + cruiseDistance) {
				state = "Cruising";
				return peakVelocity;
			} else if (deltaDistance < cruiseDistance + accelerateDistance && deltaDistance - lookAhead > distance) {
				state = "Decelerating";
				return direction * MathUtils.solveVelocity(peakVelocity, Math.abs(acceleration), deltaDistance - (cruiseDistance + accelerateDistance) + lookAhead);
			} else {
				state = "Finished";
				finished = true;
				return finalVelocity;
			}
		}
		else {
			state = "Neither Triggered";
			finished = true;
			return finalVelocity;
		}
	}


	public boolean isDone() {
		return finished;
	}

	public String getState() {return state;}
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

		super(initialAngularVelocity, maxAngularVelocity, endAngularVelocity, angularAcceleration, initialAngle, finalAngle, (lookAhead * (Math.PI / 180)));

		this.initialAngle = initialAngle;
		this.circleRadius = wheelDistFromCenter;
	}

	public double getRotateVelo(double currentAngle, double minVelo, AngleUnit angleUnit)
	{
		currentAngle = angleUnit.toRadians(currentAngle);
		double angularVelo = super.getVelocity((currentAngle),minVelo);

		this.state = state + ", " + String.valueOf(acceleration) + ", " + String.valueOf(distance);
	//because velocity is angular velocity times radius
		return angularVelo * circleRadius;
	}
}