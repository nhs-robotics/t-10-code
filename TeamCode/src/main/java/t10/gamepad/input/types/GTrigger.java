package t10.gamepad.input.types;

import java.util.function.Consumer;
import java.util.function.Supplier;

import t10.gamepad.GController;
import t10.gamepad.input.GInput;
import t10.gamepad.input.GIsPressed;
import t10.gamepad.input.GOnPress;
import t10.gamepad.input.GOnRelease;

/**
 * Allows more granular input for a gamepad trigger. There are only two. You can apply a variable amount of pressure to them.
 */
public class GTrigger implements GInput, GOnPress<GTrigger>, GOnRelease<GTrigger>, GIsPressed {
	/**
	 * Internal. Returns the pressure applied to the trigger [0, 1].
	 */
	private final Supplier<Float> valueGetter;

	/**
	 * Internal. Reference to parent controller.
	 */
	private final GController controller;

	/**
	 * Bindings to trigger events.
	 */
	private Runnable onPress, onRelease;

	/**
	 * Called while the trigger is held down with the pressure applied to the trigger [0, 1].
	 */
	private Consumer<Float> whileDown;

	/**
	 * Tracks the pressure value of the last call.
	 */
	private float lastValue;

	/**
	 * Initializes the {@link GTrigger}. Internal use mainly.
	 *
	 * @param controller  The reference to the parent controller.
	 * @param valueGetter Must return the pressure value of the trigger.
	 */
	public GTrigger(GController controller, Supplier<Float> valueGetter) {
		this.valueGetter = valueGetter;
		this.controller = controller;
	}

	@Override
	public void loop() {
		float value = this.valueGetter.get();

		if (this.onPress != null && value != 0 && this.lastValue == 0) {
			this.onPress.run();
		}

		if (this.onRelease != null && value == 0 && this.lastValue != 0) {
			this.onRelease.run();
		}

		if (this.whileDown != null && value != 0) {
			this.whileDown.accept(value);
		}

		this.lastValue = value;
	}

	@Override
	public GTrigger onPress(Runnable runnable) {
		this.onPress = runnable;
		return this;
	}

	@Override
	public GTrigger onRelease(Runnable runnable) {
		this.onRelease = runnable;
		return this;
	}

	public GTrigger whileDown(Consumer<Float> runnable) {
		this.whileDown = runnable;
		return this;
	}

	@Override
	public GController ok() {
		return this.controller;
	}

	@Override
	public boolean isPressed() {
		return this.valueGetter.get() != 0;
	}
}
