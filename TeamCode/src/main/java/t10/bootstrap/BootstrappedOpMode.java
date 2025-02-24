package t10.bootstrap;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.lynx.commands.LynxMessage;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import t10.metrics.MetricsServer;

/**
 * An {@link OpMode} that automatically loads capabilities of T-10's library.
 */
public abstract class BootstrappedOpMode extends OpMode {
	private static BootstrappedOpMode instance;

	/**
	 * True if the robot is running in a real, live competition, false otherwise.
	 */
	private final boolean isProductionMode;
	protected MetricsServer metrics;
	protected ExecutorService multithreadingService;
	protected volatile boolean isRunning;
	private List<LynxModule> hubs;

	/**
	 * Initializes a {@link BootstrappedOpMode}.
	 *
	 * @param isProductionMode True if the robot is running in a real, live competition, false otherwise.
	 */
	public BootstrappedOpMode(boolean isProductionMode) {
		this.isProductionMode = isProductionMode;
	}

	/**
	 * Initializes a {@link BootstrappedOpMode}.in developer mode.
	 */
	public BootstrappedOpMode() {
		this(false);
	}

	@Override
	public void init() {
		instance = this;

		this.isRunning = true;
		this.multithreadingService = Executors.newCachedThreadPool();

		// Only run the MetricsServer when NOT in production mode
		if (!this.isProductionMode) {
			this.metrics = new MetricsServer(this);
			this.metrics.start();

			this.multithreadingService.execute(() -> {
				while (this.isRunning) {
					this.metrics.loop();
				}

				try {
					this.metrics.stop();
				} catch (IOException | InterruptedException e) {
					throw new RuntimeException(e);
				}
			});
		}

		// Perform one bulk read for optimization
		this.hubs = hardwareMap.getAll(LynxModule.class);

		for (LynxModule hub : this.hubs) {
			hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
		}
	}

	@Override
	public void stop() {
		super.stop();

		this.isRunning = false;
		this.multithreadingService.shutdown();
	}

	@Override
	public void init_loop() {
		// Clear bulk read cache - optimization
		for (LynxModule hub : this.hubs) {
			hub.clearBulkCache();
		}
	}

	@Override
	public void loop() {
		// Clear bulk read cache - optimization
		for (LynxModule hub : this.hubs) {
			hub.clearBulkCache();
		}
	}

	/**
	 * @return The OpMode that is currently running. You can access this from anywhere.
	 */
	public static BootstrappedOpMode getInstance() {
		return instance;
	}
}
