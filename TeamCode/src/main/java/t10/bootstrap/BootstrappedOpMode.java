package t10.bootstrap;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import t10.metrics.MetricsServer;

public abstract class BootstrappedOpMode extends OpMode {
	private static BootstrappedOpMode instance;
	protected MetricsServer metrics;
	protected ExecutorService multithreadingService;
	protected volatile boolean isRunning;

	@Override
	public void init() {
		instance = this;
		this.isRunning = true;
		this.metrics = new MetricsServer(this);
		this.metrics.start();
		this.multithreadingService = Executors.newCachedThreadPool();
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

	@Override
	public void stop() {
		super.stop();
		this.isRunning = false;
		this.multithreadingService.shutdown();
	}

	public static BootstrappedOpMode getInstance() {
		return instance;
	}
}
