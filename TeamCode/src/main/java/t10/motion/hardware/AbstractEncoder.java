package t10.motion.hardware;

/**
 *
 */
public abstract class AbstractEncoder {

	public abstract int getCurrentTicks();

	public abstract double getCurrentInches();
}