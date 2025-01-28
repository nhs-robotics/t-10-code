package t10.vision;

import java.util.List;

import ai.onnxruntime.OrtException;
import android.graphics.Canvas;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import t10.ai.DepthEstimator;
import t10.geometry.Point3;
import t10.geometry.Pose;
import t10.localizer.DepthPointCloudTransformer;
import t10.localizer.Localizer;

/**
 * Unstable. Untested API.
 */
public class DepthPointCloudProcessor implements VisionProcessor, AutoCloseable {
	private final Localizer localizer;
	private final DistanceSensor distanceSensor;
	private final int pixelX;
	private final int pixelY;
	private DepthEstimator depthEstimator;
	private DepthPointCloudTransformer pointCloudTransformer;
	private Mat resized;
	private List<Point3> pointCloud;

	public DepthPointCloudProcessor(Localizer localizer, DistanceSensor distanceSensor, int pixelX, int pixelY) {
		this.localizer = localizer;
		this.distanceSensor = distanceSensor;
		this.pixelX = pixelX;
		this.pixelY = pixelY;
	}

	public List<Point3> getPointCloud() {
		return pointCloud;
	}

	@Override
	public void close() throws Exception {
		this.depthEstimator.close();
	}

	@Override
	public void init(int width, int height, CameraCalibration cameraCalibration) {
		try {
			this.resized = new Mat();
			this.pointCloudTransformer = new DepthPointCloudTransformer(
					cameraCalibration.focalLengthX,
					cameraCalibration.focalLengthY,
					cameraCalibration.principalPointX,
					cameraCalibration.principalPointY
			);
			this.depthEstimator = new DepthEstimator();
			this.depthEstimator.createOrtSession();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object processFrame(Mat mat, long ms) {
		Imgproc.resize(mat, this.resized, new Size(mat.width() / 4d, mat.height() / 4d));

		try {
			Pose absolutePose = this.localizer.getFieldCentricPose();
			float[][] depthMatrix = this.depthEstimator.run(this.resized);

			this.pointCloudTransformer.transformToAbsoluteDepth(
					depthMatrix,
					this.distanceSensor.getDistance(DistanceUnit.INCH),
					this.pixelX,
					this.pixelY
			);
			this.pointCloud = this.pointCloudTransformer.depthToPointCloud(depthMatrix);
			this.pointCloud = this.pointCloudTransformer.mapToFieldCoordinates(pointCloud, absolutePose);
		} catch (OrtException ignored) {
			// Nothing to do.
		}

		return null;
	}

	@Override
	public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {
	}
}
