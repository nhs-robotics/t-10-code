package t10.auto;

import t10.Loop;

public interface AutoAction extends Loop {
	/**
	 * Run before the loop starts for this action.
	 */
	void init();

	/**
	 * @return True if this action has finished completely and it is safe to move on to the next action.
	 */
	boolean isComplete();
}
