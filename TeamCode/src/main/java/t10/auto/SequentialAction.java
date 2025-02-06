package t10.auto;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * An {@link AutoAction} that completes several other {@link AutoAction}s in a linear sequence (one after the other).
 */
public class SequentialAction implements AutoAction {
	/**
	 * The order of {@link AutoAction}s to complete.
	 */
	private final List<AutoAction> actionSequence;

	/**
	 * The {@link AutoAction} that is currently being executed.
	 */
	private AutoAction currentAction;

	/**
	 * The index of the {@link AutoAction} in {@link SequentialAction#actionSequence} that is being executed.
	 */
	private int currentIdx = -1;

	/**
	 * Creates a {@link SequentialAction}.
	 *
	 * @param actionSequence The sequence of {@link AutoAction}s to complete
	 */
	public SequentialAction(List<AutoAction> actionSequence) {
		this.actionSequence = new LinkedList<>();
		this.actionSequence.addAll(actionSequence);
	}

	/**
	 * Creates a {@link SequentialAction}.
	 *
	 * @param actionSequence The sequence of {@link AutoAction}s to complete
	 */
	public SequentialAction(AutoAction... actionSequence) {
		this(Arrays.asList(actionSequence));
	}

	/**
	 * Adds {@link AutoAction}s to the end of the sequence.
	 *
	 * @param autoActions The {@link AutoAction}s to add to the end of the sequence
	 * @return This {@link SequentialAction} to be chained.
	 */
	public SequentialAction add(AutoAction... autoActions) {
		this.actionSequence.addAll(Arrays.asList(autoActions));
		return this;
	}

	@Override
	public void init() {
	}

	@Override
	public void loop() {
		if (this.currentAction == null || this.currentAction.isComplete()) {
			this.currentIdx++;

			if (this.isComplete()) {
				return;
			}

			this.currentAction = this.actionSequence.get(this.currentIdx);
			this.currentAction.init();
		}

		this.currentAction.loop();
	}

	@Override
	public boolean isComplete() {
		return this.currentIdx >= this.actionSequence.size();
	}

	/**
	 * @return True if the action has started executing, false if it has not started executing.
	 */
	public boolean hasStarted() {
		return this.currentIdx < 0;
	}
}
