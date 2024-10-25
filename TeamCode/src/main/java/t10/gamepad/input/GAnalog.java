package t10.gamepad.input;

/**
 * An input that has an analog value. For example, a joystick ({@link t10.gamepad.input.types.GJoystick}).
 * @param <T> This type.
 */
public interface GAnalog<T> {
    /**
     * Bind a {@link Runnable} to be run when this input is moved.
     * @param runnable The {@link Runnable} to run when this input is moved.
     * @return This {@link GAnalog}
     */
    T onMove(Runnable runnable);
}
