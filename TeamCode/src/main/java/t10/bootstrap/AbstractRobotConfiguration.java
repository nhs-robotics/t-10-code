package t10.bootstrap;

import t10.novel.NovelEncoder;
import t10.novel.NovelMotor;
import t10.novel.odometry.NovelOdometry;
import t10.novel.mecanum.MecanumDriver;
import t10.vision.Webcam;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.lang.reflect.Field;

/**
 * Sets fields marked with @{@link Hardware} to their initialized value based on the {@link OpMode#hardwareMap}.
 * <p>
 * Example where a webcam and wheel are auto initialized using @{@link Hardware}. You would be able to use these
 * variables just like normal. RoboBase does the initialization for you. The example class:
 *
 * <pre>{@code
 * public class MyRobotConfiguration extends AbstractRobotConfiguration {
 *      @Hardware(name = "Webcam")
 *      public Webcam webcam;
 *
 *      @Hardware(
 *          name = "Chain",
 *          wheelDiameterCm = 9.6,
 *          ticksPerRevolution = 500,
 *          zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
 *      )
 *      public NovelMotor chainWheel;
 * }
 * }</pre>
 *
 * <b>Notes:</b>
 * <ul>
 *     <li>Fields must be {@code public} and not {@code final} or {@code private}.</li>
 *     <li>Hardware not found will throw an exception; all hardware defined in the configuration must be present.</li>
 * </ul>
 */
public abstract class AbstractRobotConfiguration {
    public AbstractRobotConfiguration(HardwareMap hardwareMap) {
        try {
            for (Field field : this.getClass().getFields()) {
                Hardware hardware = field.getAnnotation(Hardware.class);

                if (hardware != null) {
                    String configName = hardware.name();
                    Class<?> type = field.getType();
                    Object o;

                    if (type.equals(Webcam.class)) {
                        o = new Webcam(hardwareMap, configName);
                    } else if (type.equals(NovelMotor.class)) {
                        o = new NovelMotor(hardwareMap.get(DcMotorEx.class, configName), hardware.ticksPerRevolution(), hardware.diameterIn(), hardware.gearRatio());
                        ((NovelMotor) o).motor.setZeroPowerBehavior(hardware.zeroPowerBehavior());
                    } else if (type.equals(NovelEncoder.class)) {
                        o = new NovelEncoder(hardwareMap.get(DcMotor.class, configName), hardware.diameterIn(), hardware.ticksPerRevolution());
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
     * Creates a Mecanum driver using this configuration. This method specifies what coefficients to use.
     * This method may throw an exception if it is not implemented/available for this configuration.
     * @return A Mecanum driver using this configuration
     */
    public abstract MecanumDriver createMecanumDriver();

    /**
     * Creates an odometry instance. This method specifies what coefficients to use.
     * This method may throw an exception if it is not implemented/available for this configuration.
     * @return An odometry instance using this configuration
     */
    public abstract NovelOdometry createOdometry();
}
