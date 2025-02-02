package t10.gamepad.input;

import t10.Loop;
import t10.gamepad.GController;

/**
 * A general input type.
 */
public interface GInput extends Loop {
	/**
	 * Returns the {@link GController} that this input belongs to.
	 *
	 * @return The {@link GController} that this input belongs to.
	 */
	GController ok();
}
