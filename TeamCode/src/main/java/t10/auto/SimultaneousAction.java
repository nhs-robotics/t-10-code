package t10.auto;

import java.util.ArrayList;
import java.util.List;

public class SimultaneousAction implements AutoAction {
    private final List<AutoAction> actions;

    public SimultaneousAction() {
        this.actions = new ArrayList<>();
    }

    public SimultaneousAction(AutoAction... actions) {
        this();

        for (AutoAction action : actions) {
            this.add(action);
        }
    }

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
