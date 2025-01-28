package t10.gamepad.input.types;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import t10.gamepad.GController;
import t10.gamepad.input.GAnalog;
import t10.gamepad.input.GInput;

public class GJoystick implements GInput, GAnalog<GJoystick> {
	private final GController controller;
	private BiConsumer<Float, Float> onMove;
	private final Supplier<Float> xValueSupplier;
	private final Supplier<Float> yValueSupplier;

	private float lastX;
	private float lastY;

	public GJoystick(GController controller, Supplier<Float> xValueSupplier, Supplier<Float> yValueSupplier) {
		this.controller = controller;
		this.xValueSupplier = xValueSupplier;
		this.yValueSupplier = yValueSupplier;
	}

	@Override
	public void update() {
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
