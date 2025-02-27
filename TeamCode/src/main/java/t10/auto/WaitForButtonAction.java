package t10.auto;

import t10.gamepad.input.types.GButton;

/**
 * An {@link AutoAction} that waits for a button to be pushed.
 */
public class WaitForButtonAction implements AutoAction {
	/**
	 * The button that needs to be pushed to continue.
	 */
	private final GButton button;

	/**
	 * Creates a {@link WaitForButtonAction}.
	 *
	 * @param button The button to wait for.
	 */
	public WaitForButtonAction(GButton button) {
		this.button = button;
	}

	@Override
	public void init() {
	}

	@Override
	public boolean isComplete() {
		return this.button.isPressed();
	}

	@Override
	public void loop() {
	}
}
