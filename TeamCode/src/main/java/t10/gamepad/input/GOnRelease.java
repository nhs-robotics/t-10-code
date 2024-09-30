package t10.gamepad.input;

/**
 * An input that can be released. For example, a button ({@link t10.gamepad.input.types.GButton}).
 * @param <T> This type.
 */
public interface GOnRelease<T> {
    /**
     * Bind a {@link Runnable} to be run when this input is released.
     * @param runnable The {@link Runnable} to run when this input is released.
     * @return This {@link GOnRelease}
     */
    T onRelease(Runnable runnable);
}
