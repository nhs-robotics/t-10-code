package t10.auto;

import java.util.Arrays;
import java.util.List;

public class SequentialAction implements AutoAction {
	private final List<AutoAction> actionSequence;
	private AutoAction currentAction;
	private int currentIdx = -1;

	public SequentialAction(List<AutoAction> actionSequence) {
		this.actionSequence = actionSequence;
	}

	public SequentialAction(AutoAction... actionSequence) {
		this.actionSequence = Arrays.asList(actionSequence);
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

	public boolean hasStarted() {
		return this.currentIdx < 0;
	}
}
