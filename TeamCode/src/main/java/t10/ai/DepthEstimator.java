package t10.ai;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OnnxTensorLike;
import ai.onnxruntime.OnnxValue;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.opencv.core.Mat;

import t10.geometry.Point3;
import t10.geometry.Pose;
import t10.utils.IOUtils;

public class DepthEstimator implements AutoCloseable {
	private OrtEnvironment environment;
	private OrtSession session;

	public void createOrtSession() throws OrtException, IOException {
		this.environment = OrtEnvironment.getEnvironment();
		InputStream resourceAsStream = DepthEstimator.class.getResourceAsStream("/depth_anything_v2_small_q4.onnx");
		ByteBuffer byteBuffer = IOUtils.readInputStream(resourceAsStream, 50000000);
		this.session = this.environment.createSession(byteBuffer);
		resourceAsStream.close();
	}

	private OnnxTensor convertImageToTensor(Mat image) throws OrtException {
		// Convert to float buffer
		int width = image.width();
		int height = image.height();
		FloatBuffer buffer = FloatBuffer.allocate(width * height * 3);

		for (int x = 0; width > x; x++) {
			for (int y = 0; height > y; y++) {
				double[] bgr = image.get(y, x);

				buffer.put(x + y * width + 0 * (width * height), (float) bgr[2]);
				buffer.put(x + y * width + 1 * (width * height), (float) bgr[1]);
				buffer.put(x + y * width + 2 * (width * height), (float) bgr[0]);
			}
		}

		// Create the tensor
		return OnnxTensor.createTensor(
				this.environment,
				buffer,
				new long[]{
						1,
						3,
						height,
						width,
				}
		);
	}

	private float[][] estimateDepth(OnnxTensorLike tensor) throws OrtException {
		// Prepare inputs
		Map<String, OnnxTensorLike> inputs = new HashMap<>();
		inputs.put("pixel_values", tensor);

		// Run
		OrtSession.Result result = session.run(inputs);

		// Return depth map output
		Optional<OnnxValue> predictedDepth = result.get("predicted_depth");
		float[][][] value = (float[][][]) predictedDepth.get().getValue();
		return value[0];
	}

	public float[][] run(Mat image) throws OrtException {
		OnnxTensor onnxTensor = this.convertImageToTensor(image);
		return this.estimateDepth(onnxTensor);
	}

	@Override
	public void close() throws Exception {
		this.session.close();
		this.environment.close();
	}

	/**
	 * Unstable API. Untested.
	 */
	public static class DepthPointCloudTransformer {
		// Camera intrinsics
		private final double focalLengthX;
		private final double focalLengthY;
		private final double principalPointX;
		private final double principalPointY;

		// Field constants
		private static final double FIELD_SIZE = 12.0; // feet
		private static final double METERS_TO_FEET = 3.28084;

		/**
		 * Creates a new DepthPointCloudTransformer with the given camera parameters.
		 *
		 * @param focalLengthX    Focal length x.
		 * @param focalLengthY    Focal length y.
		 * @param principalPointX Principal point x.
		 * @param principalPointY Principal point y.
		 */
		public DepthPointCloudTransformer(double focalLengthX, double focalLengthY, double principalPointX, double principalPointY) {
			this.focalLengthX = focalLengthX;
			this.focalLengthY = focalLengthY;
			this.principalPointX = principalPointX;
			this.principalPointY = principalPointY;
		}

		/**
		 * Transform relative depth matrix to absolute depths.
		 */
		public void transformToAbsoluteDepth(float[][] depthMatrix, double absoluteDepth, int pixelX, int pixelY) {
			// Calculate and apply scaling factor
			double scalingFactor = absoluteDepth / depthMatrix[pixelY][pixelX];

			for (int y = 0; depthMatrix.length > y; y++) {
				for (int x = 0; depthMatrix[0].length > x; x++) {
					depthMatrix[y][x] *= (float) scalingFactor;
				}
			}
		}

		/**
		 * Convert depth matrix to point cloud in camera coordinates.
		 */
		public List<Point3> depthToPointCloud(float[][] absoluteDepthMatrix) {
			List<Point3> points = new ArrayList<>();

			for (int row = 0; row < absoluteDepthMatrix.length; row++) {
				for (int col = 0; col < absoluteDepthMatrix[0].length; col++) {
					float depth = absoluteDepthMatrix[row][col];

					if (depth <= 0 || Double.isNaN(depth)) {
						continue;
					}

					// Project to 3D using pinhole camera model
					double x = (col - principalPointX) * depth / focalLengthX;
					double y = (row - principalPointY) * depth / focalLengthY;
					double z = depth;

					points.add(new Point3(x, y, z));
				}
			}

			return points;
		}

		/**
		 * Transform points from camera coordinates to field coordinates.
		 */
		public List<Point3> mapToFieldCoordinates(List<Point3> depthPoints, Pose robotPose) {
			List<Point3> fieldPoints = new ArrayList<>();

			double headingRadians = robotPose.getHeading(AngleUnit.RADIANS);
			double cos = Math.cos(headingRadians);
			double sin = Math.sin(headingRadians);
			double robotX = robotPose.getX();
			double robotY = robotPose.getY();

			for (Point3 point : depthPoints) {
				// Convert camera frame to robot frame
				double robotFrameX = point.getZ();   // camera Z -> robot X (forward)
				double robotFrameY = -point.getX();  // camera X -> robot Y (left)
				double robotFrameZ = -point.getY();  // camera Y -> robot Z (up)

				// Rotate by robot heading
				double fieldX = robotFrameX * cos - robotFrameY * sin;
				double fieldY = robotFrameX * sin + robotFrameY * cos;

				// Translate by robot position and convert to feet
				fieldX = robotX + fieldX * METERS_TO_FEET;
				fieldY = robotY + fieldY * METERS_TO_FEET;
				double fieldZ = robotFrameZ * METERS_TO_FEET;

				// Add point if it's within field bounds
				if (isInField(fieldX, fieldY)) {
					fieldPoints.add(new Point3(fieldX, fieldY, fieldZ));
				}
			}

			return fieldPoints;
		}

		/**
		 * Check if point is within field bounds.
		 */
		private boolean isInField(double x, double y) {
			return x >= 0 && x <= FIELD_SIZE && y >= 0 && y <= FIELD_SIZE;
		}
	}
}
