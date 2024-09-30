package t10.vision;

import android.util.Size;
import intothedeep.Constants;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.*;

import java.util.List;

/**
 * Wrapper for the FTC April Tag library to get what you really want: April Tag detection/position/pose information.
 */
public class AprilTagRetriever {
    private final VisionPortal visionPortal;
    private final AprilTagProcessor aprilTagProcessor;
    public final AprilTagLibrary library;

    /**
     * Creates an April Tag Retriever with the current April Tags for the current season's game.
     * @param webcam The webcam to use for vision.
     */
    public AprilTagRetriever(Webcam webcam) {
        this(webcam, AprilTagGameDatabase.getCurrentGameTagLibrary());
    }

    /**
     * Creates an April Tag Retriever with a webcam for a specific season's game.
     * @see AprilTagGameDatabase
     * @param webcam The webcam to use for vision.
     * @param library The April Tag Library to use/April Tag data, found in {@link AprilTagGameDatabase}
     */
    public AprilTagRetriever(Webcam webcam, AprilTagLibrary library) {
        int cameraMonitorView = webcam.hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", webcam.hardwareMap.appContext.getPackageName());
        this.library = library;
        this.aprilTagProcessor = new AprilTagProcessor.Builder()
                .setDrawAxes(true)
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                .setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)  // inches and degrees match the april tag library metadata
                .setTagLibrary(this.library)
                .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                .build();
        this.visionPortal = new VisionPortal.Builder()
                .setCameraResolution(new Size(Constants.Webcam.CAMERA_RES_WIDTH, Constants.Webcam.CAMERA_RES_HEIGHT))
                .setCamera(webcam.webcamDevice)
                .setAutoStopLiveView(true)
                .setLiveViewContainerId(cameraMonitorView)
                .enableLiveView(true)
                .addProcessor(this.aprilTagProcessor)
                .build();
    }

    /**
     * Returns the currently identified April Tags from the webcam.
     * @return The list of currently visible April Tags.
     */
    public List<AprilTagDetection> getDetections() {
        return this.aprilTagProcessor.getDetections();
    }
}
