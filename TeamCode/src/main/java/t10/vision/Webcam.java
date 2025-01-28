package t10.vision;

import android.util.Size;
import com.qualcomm.robotcore.hardware.HardwareMap;
import intothedeep.Constants;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.openftc.easyopencv.OpenCvCamera;

/**
 * Abstracts away certain parts of {@link OpenCvCamera}.
 */
public class Webcam {
	/**
	 * The {@link HardwareMap} associated with this webcam.
	 */
	public final HardwareMap hardwareMap;

	/**
	 * The webcam device.
	 */
	public final WebcamName webcamDevice;
	public VisionPortal visionPortal;

	public Webcam(HardwareMap hardwareMap, WebcamName webcamDevice) {
		this.hardwareMap = hardwareMap;
		this.webcamDevice = webcamDevice;
	}

	/**
	 * Initializes a {@link Webcam}.
	 *
	 * @param hardwareMap The {@link HardwareMap} that has the webcam in it.
	 * @param name        The name of the webcam in the {@link HardwareMap}.
	 */
	public Webcam(HardwareMap hardwareMap, String name) {
		this(hardwareMap, hardwareMap.get(WebcamName.class, name));
	}

	/**
	 * Starts recording on the webcam and processing with processors.
	 *
	 * @param processors The processors that receive this webcam's video.
	 */
	public void start(VisionProcessor... processors) {
		int cameraMonitorView = hardwareMap.appContext.getResources()
				.getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

		this.visionPortal = new VisionPortal.Builder()
				.setCameraResolution(new Size(Constants.Webcam.CAMERA_RES_WIDTH, Constants.Webcam.CAMERA_RES_HEIGHT))
				.setCamera(webcamDevice)
				.setAutoStopLiveView(true)
				.setLiveViewContainerId(cameraMonitorView)
				.enableLiveView(true)
				.addProcessors(processors)
				.setShowStatsOverlay(true)
				.build();
	}

	public void stop() {
		this.visionPortal.close();
	}
}
