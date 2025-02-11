package t10.bootstrap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.qualcomm.robotcore.hardware.DcMotor;

import t10.motion.hardware.Motor;

/**
 * {@code @Hardware} annotations are used in robot configurations (see {@link AbstractRobotConfiguration}). In the following example, {@code webcam} will be set to the {@link t10.vision.Webcam} found in the hardware map with name "Webcam".
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Hardware {
	/**
	 * The name of the hardware in the hardware map.
	 */
	String name();

	// Optional parameters just for certain types of hardware

	/**
	 * MOTORS ONLY: The number of encoder ticks per revolution.
	 */
	double ticksPerRevolution() default -1;

	/**
	 * MOTORS ONLY: The zero power behavior of the motor.
	 */
	DcMotor.ZeroPowerBehavior zeroPowerBehavior() default DcMotor.ZeroPowerBehavior.BRAKE;

	/**
	 * MOTORS ONLY: The gear ratio of the motor.
	 */
	int gearRatio() default 1;

	/**
	 * MOTORS ONLY: The diameter of the wheel.
	 */
	double diameterIn() default -1;
}
