package t10.motion.hardware;

import com.qualcomm.robotcore.hardware.DcMotorEx;

/**
 * Enables manipulation of the velocity of a motor.
 */
public class Motor {
	public final DcMotorEx motor;
	public final double ticksPerRevolution;
	public final double wheelDiameterInches;
	public final int gearRatio;

	/**
	 * The underlying encoder on this motor.
	 */
	public final MotorEncoder encoder;

	/**
	 * Creates a Motor, allowing velocity to be manipulated in in/s. <strong>AN ENCODER MUST BE CONNECTED TO THIS MOTOR FOR VELOCITY SUPPORT.</strong>
	 *
	 * @param motor               The motor that this Motor should control.
	 * @param ticksPerRevolution  The number of ticks per revolution of the motor.
	 * @param wheelDiameterInches The diameter of the wheel in inches.
	 * @param gearRatio           The gear ratio of this motor.
	 */
	public Motor(DcMotorEx motor, double ticksPerRevolution, double wheelDiameterInches, int gearRatio) {
		this.motor = motor;
		this.encoder = new MotorEncoder(motor, wheelDiameterInches * gearRatio, ticksPerRevolution);
		this.ticksPerRevolution = ticksPerRevolution;
		this.wheelDiameterInches = wheelDiameterInches;
		this.gearRatio = gearRatio;
	}

	/**
	 * Set the velocity of this motor in in/s.
	 *
	 * @param inchesPerSecond Velocity of the motor in in/s.
	 */
	public void setVelocity(double inchesPerSecond) {
		double encoderTicksPerSecond = ((inchesPerSecond / Math.PI) * this.ticksPerRevolution) / this.wheelDiameterInches;

		this.motor.setVelocity(encoderTicksPerSecond);
	}

	public void setPower(double power) {
		this.motor.setPower(power);
	}

	/**
	 * @return The velocity of the motor, in in/s.
	 */
	public double getVelocity() {
		return (this.wheelDiameterInches * this.motor.getVelocity() * Math.PI) / this.ticksPerRevolution;
	}
}
