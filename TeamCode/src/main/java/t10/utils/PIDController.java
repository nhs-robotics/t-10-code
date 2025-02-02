package t10.utils;

public class PIDController {
	public final double kP;
	public final double kI;
	public final double kD;

	private double previousError = 0;
	private double integral = 0;
	private long lastTime;

	public PIDController(double kP, double kI, double kD) {
		this.kP = kP;
		this.kI = kI;
		this.kD = kD;
		this.lastTime = System.currentTimeMillis();
	}

	public double calculate(double currentPosition, double targetPosition) {
		// Calculate time delta
		long currentTime = System.currentTimeMillis();
		double dt = (currentTime - lastTime) / 1000.0; // Convert to seconds
		lastTime = currentTime;

		// Calculate error
		double error = targetPosition - currentPosition;

		// Calculate integral
		integral += error * dt;

		// Calculate derivative
		double derivative = dt > 0 ? (error - previousError) / dt : 0;

		// Save error for next iteration
		previousError = error;

		// Calculate final power
		double power = kP * error
				+ kI * integral
				+ kD * derivative;

		// Clamp power between -1 and 1
		return Math.max(-1, Math.min(1, power));
	}

	// Optional: Method to reset the controller
	public void reset() {
		previousError = 0;
		integral = 0;
		lastTime = System.currentTimeMillis();
	}
}
