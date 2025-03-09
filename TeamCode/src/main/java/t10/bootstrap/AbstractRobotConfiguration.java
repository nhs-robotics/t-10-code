package t10.bootstrap;

import java.lang.reflect.Field;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import t10.localizer.Localizer;
import t10.motion.hardware.Motor;
import t10.motion.hardware.MotorEncoder;
import t10.motion.mecanum.MecanumDriver;

/**
 * <p>Sets fields marked with @{@link Hardware} to their initialized value based on the {@link OpMode#hardwareMap}.</p>
 */
public abstract class AbstractRobotConfiguration {
	/**
	 * Initializes this {@link AbstractRobotConfiguration} with the {@code hardwareMap}.
	 *
	 * @param hardwareMap The {@link HardwareMap} to load this {@link AbstractRobotConfiguration} with.
	 */
	public AbstractRobotConfiguration(HardwareMap hardwareMap) {
		try {
			for (Field field : this.getClass().getFields()) {
				Hardware hardware = field.getAnnotation(Hardware.class);

				if (hardware != null) {
					String configName = hardware.name();
					Class<?> type = field.getType();
					Object o;

					if (type.equals(Motor.class)) {
						o = new Motor(hardwareMap.get(DcMotorEx.class, configName), hardware.ticksPerRevolution(), hardware.diameterIn(), hardware.gearRatio());
						((Motor) o).motor.setZeroPowerBehavior(hardware.zeroPowerBehavior());
					} else if (type.equals(MotorEncoder.class)) {
						o = new MotorEncoder(hardwareMap.get(DcMotor.class, configName), hardware.diameterIn(), hardware.ticksPerRevolution());
					} else {
						o = hardwareMap.get(field.getType(), configName);
					}

					field.setAccessible(true);
					field.set(this, o);
				}
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException("could not create AbstractRobotConfiguration");
		}
	}

	/**
	 * @return The mecanum driver to use with this robot configuration.
	 */
	public abstract MecanumDriver createMecanumDriver();

	/**
	 * @return The localizer to use with this robot configuration.
	 */
	public abstract Localizer<?> createLocalizer();
}
