package t10.motion.hardware;

public abstract class AbstractEncoder {
	/**
	 * @return The current position of the motor in ticks.
	 */
	public abstract int getCurrentTicks();

	/**
	 * @return The current position of the motor in inches.
	 */
	public abstract double getCurrentInches();
}
