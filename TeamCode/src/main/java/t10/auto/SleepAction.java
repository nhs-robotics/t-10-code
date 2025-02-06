package t10.auto;

/**
 * An {@link AutoAction} that waits for a specified amount of time.
 */
public class SleepAction implements AutoAction {
	/**
	 * Number of milliseconds to wait.
	 */
	private final long ms;

	/**
	 * Internal ending time in milliseconds since the epoch to wait until.
	 */
	private long endMs;

	/**
	 * Creates a {@link SleepAction}.
	 *
	 * @param ms The number of milliseconds to wait.
	 */
	public SleepAction(long ms) {
		this.ms = ms;
	}

	@Override
	public void init() {
		this.endMs = System.currentTimeMillis() + this.ms;
	}

	@Override
	public boolean isComplete() {
		return System.currentTimeMillis() > this.endMs;
	}

	@Override
	public void loop() {
	}
}
