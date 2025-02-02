package t10.auto;

public class SleepAction implements AutoAction {
	private final long ms;
	private long endMs;

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
