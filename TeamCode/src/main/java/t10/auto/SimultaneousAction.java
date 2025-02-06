package t10.auto;

import java.util.ArrayList;
import java.util.List;

/**
 * Runs several {@link AutoAction}s at the same time.
 */
public class SimultaneousAction implements AutoAction {
	/**
	 * The {@link AutoAction}s to run simultaneously.
	 */
    private final List<AutoAction> actions;

	/**
	 * Creates a SimultaneousAction. You'll want to use {@link SimultaneousAction#add(AutoAction)} to add actions to
	 * run simultaneously.
	 */
    public SimultaneousAction() {
        this.actions = new ArrayList<>();
    }

	/**
	 * Creates a SimultaneousAction.
	 *
	 * @param actions The actions to complete simultaneously.
	 */
    public SimultaneousAction(AutoAction... actions) {
        this();

        for (AutoAction action : actions) {
            this.add(action);
        }
    }

	/**
	 * Adds an action to complete simultaneously as the others in this {@link SimultaneousAction}.
	 *
	 * @param action The additional action to complete simultaneously.
	 * @return This {@link SimultaneousAction} to be chained.
	 */
    public SimultaneousAction add(AutoAction action) {
		if (!action.getClass().equals(SequentialAction.class)) {
			for (AutoAction a : this.actions) {
				if (a.getClass().getName().equals(action.getClass().getName())) {
					throw new IllegalArgumentException("You can't add multiple of the same type of Action to SimultaneousAction.");
				}
			}
		}

        this.actions.add(action);

        return this;
    }

    @Override
    public void init() {
		for (AutoAction action : this.actions) {
			action.init();
		}
    }

    @Override
    public void loop() {
        for (AutoAction action : this.actions) {
            if (action.isComplete()) {
                continue;
            }

            action.loop();
        }
    }

    @Override
    public boolean isComplete() {
        for (AutoAction action : this.actions) {
            if (!action.isComplete()) {
                return false;
            }
        }

        return true;
    }
}
