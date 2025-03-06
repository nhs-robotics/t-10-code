package t10.localizer;

import t10.Loop;
import t10.geometry.Point;

// TODO: Implement velocity and acceleration.
public interface Localizer<T extends Point> extends Loop {
	/**
	 * Sets the reference.
	 *
	 * @param pose The reference pose.
	 */
	void setFieldCentric(T pose);

	/**
	 * @return The that represents where the robot is in relation to the field.
	 */
	T getFieldCentric();

	boolean isDoneInitializing();
}
