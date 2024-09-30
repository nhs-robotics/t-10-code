package t10.gamepad.input;

import java.util.function.Consumer;

/**
 * Represents a toggleable input to the gamepad.
 * @param <T> This type.
 */
public interface GOnToggle<T> {
    /**
     * Binds a {@link Runnable} to be called when this input is toggled ON.
     * @param runnable The {@link Runnable} to run when this input is toggled ON.
     * @return This {@link GOnToggle}.
     */
    T onToggleOn(Runnable runnable);

    /**
     * Binds a {@link Runnable} to be called when this input is toggled OFF.
     * @param runnable The {@link Runnable} to run when this input is toggled OFF.
     * @return This {@link GOnToggle}.
     */
    T onToggleOff(Runnable runnable);

    /**
     * Binds a {@link Runnable} to be called when this input is toggled ON OR OFF.
     * @param withNewState The {@link Consumer} to run when this input is toggled ON OR OFF. The new toggle state is passed to the consumer.
     * @return This {@link GOnToggle}.
     */
    T onToggle(Consumer<Boolean> withNewState);

    /**
     * Sets the initial toggle state of this {@link GOnToggle}.
     * @param toggled Toggled (true) or not toggled (false)
     * @return This {@link GOnToggle}.
     */
    T initialToggleState(boolean toggled);

    /**
     * Returns whether this is toggled or not.
     * @return Toggled (true) or not toggled (false).
     */
    boolean isToggled();
}
