package t10.gamepad.input;

import java.util.function.BiConsumer;

/**
 * An input that can be moved. For example, a joystick ({@link t10.gamepad.input.types.GJoystick}).
 * @param <T> This type.
 */
public interface GAnalog<T> {
    /**
     * Bind a {@link Runnable} to be run when this input is moved.
     * @param onMove First argument is the X coordinate of the joystick, second is the Y.
     * @return This {@link GAnalog}
     */
    T onMove(BiConsumer<Float, Float> onMove);
}
