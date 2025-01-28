package t10.gamepad.input.types;

import java.util.function.Consumer;
import java.util.function.Supplier;

import t10.gamepad.GController;
import t10.gamepad.input.GInput;
import t10.gamepad.input.GIsPressed;
import t10.gamepad.input.GOnPress;
import t10.gamepad.input.GOnRelease;

public class GTrigger implements GInput, GOnPress<GTrigger>, GOnRelease<GTrigger>, GIsPressed {
	private final Supplier<Float> valueGetter;
	private final GController controller;
	private Runnable onPress, onRelease;
	private Consumer<Float> whileDown;
	private float lastValue;

	public GTrigger(GController controller, Supplier<Float> valueGetter) {
		this.valueGetter = valueGetter;
		this.controller = controller;
	}

	@Override
	public void update() {
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
