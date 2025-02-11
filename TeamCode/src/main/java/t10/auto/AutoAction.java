package t10.auto;

import t10.Loop;

/**
 * An {@link AutoAction} is an action that can be performed by the robot during auto. You can use {@link AutoAction}s
 * to create auto sequences using {@link SequentialAction}. Custom {@link AutoAction}s should implement {@link AutoAction}
 * for each capability of the robot.
 */
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
