package t10.gamepad.input.types;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import t10.gamepad.GController;
import t10.gamepad.input.GAnalog;
import t10.gamepad.input.GInput;

/**
 * Allows more granular input for a gamepad joystick. Represents a joystick on the gamepad. The two joysticks are the two movable knobs.
 */
public class GJoystick implements GInput, GAnalog<GJoystick> {
	/**
	 * Internal: Reference to the {@link GController} object that this {@link GButton} is related to.
	 */
	private final GController controller;

	/**
	 * Called when the joystick position updates with (x, y) position of the joystick.
	 */
	private BiConsumer<Float, Float> onMove;

	/**
	 * Internal: Returns the x-value position the joystick.
	 */
	private final Supplier<Float> xValueSupplier;

	/**
	 * Internal: Returns the y-value position the joystick.
	 */
	private final Supplier<Float> yValueSupplier;

	/**
	 * Tracks the x-value position of the joystick on the last call.
	 */
	private float lastX;

	/**
	 * Tracks the y-value position of the joystick on the last call.
	 */
	private float lastY;

	/**
	 * Initializes a {@link GJoystick}. Internal use mainly.
	 *
	 * @param controller     The parent {@link GController} object.
	 * @param xValueSupplier Must return the x-value of the position of the joystick.
	 * @param yValueSupplier Must return the y-value of the position of the joystick.
	 */
	public GJoystick(GController controller, Supplier<Float> xValueSupplier, Supplier<Float> yValueSupplier) {
		this.controller = controller;
		this.xValueSupplier = xValueSupplier;
		this.yValueSupplier = yValueSupplier;
	}

	@Override
	public void loop() {
		float currentX = this.xValueSupplier.get();
		float currentY = this.yValueSupplier.get();

		if (currentX != this.lastX || currentY != this.lastY) {
			this.lastX = currentX;
			this.lastY = currentY;

			if (this.onMove != null) {
				this.onMove.accept(currentX, currentY);
			}
		}
	}

	@Override
	public GController ok() {
		return this.controller;
	}

	@Override
	public GJoystick onMove(BiConsumer<Float, Float> onMove) {
		this.onMove = onMove;
		return this;
	}
}
