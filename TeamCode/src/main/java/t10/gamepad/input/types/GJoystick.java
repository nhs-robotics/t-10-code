package t10.gamepad.input.types;

import java.util.function.Supplier;

import t10.gamepad.GController;
import t10.gamepad.input.GAnalog;
import t10.gamepad.input.GInput;

public class GJoystick implements GInput, GAnalog<GJoystick> {

    private final GController controller;
    private Runnable onMove;
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
        if (xValueSupplier.get() != lastX || yValueSupplier.get() != lastY) {
            lastX = xValueSupplier.get();
            lastY = yValueSupplier.get();
            if (onMove != null) {
                onMove.run();
            }
        }
    }

    @Override
    public GController ok() {
        return this.controller;
    }

    @Override
    public GJoystick onMove(Runnable runnable) {
        this.onMove = runnable;
        return this;
    }

    public float getX() {
        return this.xValueSupplier.get();
    }

    public float getY() {
        return this.yValueSupplier.get();
    }
}
