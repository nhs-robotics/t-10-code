package t10.gamepad.input.types;

import java.util.function.Consumer;
import java.util.function.Supplier;

import t10.gamepad.GController;
import t10.gamepad.input.GInput;
import t10.gamepad.input.GIsPressed;
import t10.gamepad.input.GOnPress;
import t10.gamepad.input.GOnRelease;
import t10.gamepad.input.GOnToggle;

public class GButton implements GInput, GOnPress<GButton>, GOnRelease<GButton>, GOnToggle<GButton>, GIsPressed {
	private final Supplier<Boolean> isDown;
	private final GController controller;
	private Runnable onPress, onRelease, whileDown, onToggleOn, onToggleOff;
	private Consumer<Boolean> onToggle;
	private boolean wasDownLast;
	private boolean toggleState;

	public GButton(GController controller, Supplier<Boolean> isDown) {
		this.isDown = isDown;
		this.controller = controller;
	}

	@Override
	public void update() {
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
