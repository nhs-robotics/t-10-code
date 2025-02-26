package t10.motion.hardware;

public interface Encoder {
	/**
	 * @return The current position of the motor in ticks.
	 */
	int getCurrentTicks();

	/**
	 * @return The current position of the motor in inches.
	 */
	double getCurrentInches();

	/**
	 * Resets the underlying encoder.
	 */
    void reset();
}
