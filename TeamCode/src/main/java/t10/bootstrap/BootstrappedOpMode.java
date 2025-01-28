package t10.bootstrap;

import java.io.IOException;

import android.os.SystemClock;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import t10.metrics.MetricsServer;

/**
 * <p>"Bootstrapping" is the preparing another program to initialize.
 * This OpMode allows RoboCore to initialize. Upon initialization of RoboCore,
 * - {@link System#out} and {@link System#err} become available</p>
 *
 * <p><strong>IMPORTANT!</strong> When extending this class and overriding {@link BootstrappedOpMode#init()},
 * make sure that {@code super.init()} is called!</p>
 *
 * @author youngermax
 * @see OpMode
 * @see System#out
 * @see System#err
 */
public abstract class BootstrappedOpMode extends OpMode {
    protected MetricsServer metrics;

    /**
     * Sets {@link System#out} and {@link System#err} to an instance of {@link RobotDebugPrintStream}.
     * This allows {@link System#out} and {@link System#err} to be used for printing debug messages.
     *
     * @author youngermax
     * @see System#out
     * @see System#err
     */
    private void configureSystemOut() {
        System.setOut(new RobotDebugPrintStream(this.telemetry));
        System.setErr(new RobotDebugPrintStream(this.telemetry));
    }

    private void configureMetrics() {
        this.metrics = new MetricsServer(this);
        this.metrics.start();
    }

    /**
     * Waits a desired number of seconds.
     * <strong>{@link Thread#sleep(long)} does not work, use this instead!</strong>
     *
     * @param seconds Number of seconds to wait
     */
    public void sleep(double seconds) {
        SystemClock.sleep((long) (1000L * seconds));
    }

    @Override
    public void init() {
        this.configureSystemOut();
        this.configureMetrics();
    }

    @Override
    public void stop() {
        super.stop();

        try {
            this.metrics.stop();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
