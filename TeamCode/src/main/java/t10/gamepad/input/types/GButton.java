package t10.gamepad.input.types;

import java.util.function.Consumer;
import java.util.function.Supplier;

import t10.gamepad.GController;
import t10.gamepad.input.GInput;
import t10.gamepad.input.GIsPressed;
import t10.gamepad.input.GOnPress;
import t10.gamepad.input.GOnRelease;
import t10.gamepad.input.GOnToggle;

/**
 * Allows more granular input for a gamepad button. Gamepad buttons include: A, B, X, Y, joystick buttons, bumpers, dpad.
 */
public class GButton implements GInput, GOnPress<GButton>, GOnRelease<GButton>, GOnToggle<GButton>, GIsPressed {
	/**
	 * Internal: Returns if this {@link GButton} is currently being pressed down.
	 */
	private final Supplier<Boolean> isDown;

	/**
	 * Internal: Reference to the {@link GController} object that this {@link GButton} is related to.
	 */
	private final GController controller;

	/**
	 * Runnables that bind various gamepad events.
	 */
	private Runnable onPress, onRelease, whileDown, onToggleOn, onToggleOff;

	/**
	 * Event that is called when the button is toggled on/off.
	 */
	private Consumer<Boolean> onToggle;

	/**
	 * Tracks if the button was down last call.
	 */
	private boolean wasDownLast;

	/**
	 * Tracks the current toggle state of the gamepad button. This is used for onToggle.
	 */
	private boolean toggleState;

	/**
	 * Initializes a {@link GButton}. Internal use mainly.
	 *
	 * @param controller The parent {@link GController} object.
	 * @param isDown     Returns the current press state of the gamepad button.
	 */
	public GButton(GController controller, Supplier<Boolean> isDown) {
		this.isDown = isDown;
		this.controller = controller;
	}

	@Override
	public void loop() {
		boolean isDown = this.isDown.get();

		if (this.onPress != null && isDown && !this.wasDownLast) {
			this.onPress.run();
		}

		if (this.onRelease != null && !isDown && this.wasDownLast) {
			this.onRelease.run();
		}

		if (this.whileDown != null && isDown) {
			this.whileDown.run();
		}

		// Toggle
		if (isDown && !this.wasDownLast) {
			this.toggleState = !this.toggleState;

			if (this.onToggle != null) {
				this.onToggle.accept(this.toggleState);
			}

			if (this.onToggleOff != null && !this.toggleState) {
				this.onToggleOff.run();
			}

			if (this.onToggleOn != null && this.toggleState) {
				this.onToggleOn.run();
			}
		}

		this.wasDownLast = isDown;
	}

	@Override
	public GController ok() {
		return this.controller;
	}

	@Override
	public GButton onPress(Runnable runnable) {
		this.onPress = runnable;
		return this;
	}

	@Override
	public GButton onRelease(Runnable runnable) {
		this.onRelease = runnable;
		return this;
	}

	public GButton whileDown(Runnable runnable) {
		this.whileDown = runnable;
		return this;
	}

	@Override
	public GButton onToggleOn(Runnable runnable) {
		this.onToggleOn = runnable;
		return this;
	}

	@Override
	public GButton onToggleOff(Runnable runnable) {
		this.onToggleOff = runnable;
		return this;
	}

	@Override
	public GButton onToggle(Consumer<Boolean> withNewState) {
		this.onToggle = withNewState;
		return this;
	}

	@Override
	public GButton initialToggleState(boolean toggled) {
		this.toggleState = toggled;
		return this;
	}

	@Override
	public boolean isToggled() {
		return this.toggleState;
	}

	@Override
	public boolean isPressed() {
		return this.isDown.get();
	}
}
