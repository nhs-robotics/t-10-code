package t10.gamepad.input;

import t10.gamepad.GController;

/**
 * A general input type.
 */
public interface GInput {
	/**
	 * Update the underlying state of this input type and call bindings.
	 */
	void update();

	/**
	 * Returns the {@link GController} that this input belongs to.
	 *
	 * @return The {@link GController} that this input belongs to.
	 */
	GController ok();
}
