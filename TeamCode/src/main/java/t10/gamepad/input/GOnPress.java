package t10.gamepad.input;

/**
 * An input that can be pressed. For example, a button ({@link t10.gamepad.input.types.GButton}).
 * @param <T> This type.
 */
public interface GOnPress<T> {
    /**
     * Bind a {@link Runnable} to be run when this input is pressed down, but only once (i.e. not continuously while down).
     * @param runnable The {@link Runnable} to run when this input is pressed.
     * @return This {@link GOnPress}
     */
    T onPress(Runnable runnable);
}
