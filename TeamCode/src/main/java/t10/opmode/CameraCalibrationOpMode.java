package t10.opmode;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

import t10.bootstrap.BootstrappedOpMode;
import t10.gamepad.GController;
import t10.vision.Webcam;

@Autonomous(name = "Camera Calibrator")
public class CameraCalibrationOpMode extends BootstrappedOpMode {
	private static final int CHECKERBOARD_WIDTH = 9;
	private static final int CHECKERBOARD_HEIGHT = 6;
	private static final Size CHECKERBOARD_PATTERN_SIZE = new Size(CHECKERBOARD_WIDTH, CHECKERBOARD_HEIGHT);
	private static final double SQUARE_SIZE_METERS = 0.02467;
	private static final Paint CORNER_POINT_PAINT = new Paint();
	private List<WebcamName> webcams;
	private int cameraIndex = 0;
	private GController gamepadController;
	private Webcam webcam;
	private Telemetry.Item resolution, rms;
	private Telemetry.Item fx, fy, px, py;   // Focal length and principal point
	private Telemetry.Item k1, k2, p1, p2, k3;   // Distortion coefficients
	private List<Mat> objectPoints;  // 3D points in real world spacex`
	private List<Mat> imagePoints;   // 2D points in image plane
	private Size imageSize;

	static {
		CORNER_POINT_PAINT.setColor(Color.GREEN);
		CORNER_POINT_PAINT.setTextSize(12f);
	}

	@Override
	public void init() {
		super.init();

		this.webcams = this.hardwareMap.getAll(WebcamName.class);
		this.gamepadController = new GController(this.gamepad1)
				.rightBumper.onPress(() -> {
					this.cameraIndex = (this.cameraIndex + 1) % this.webcams.size();
					this.updateCamera();
				}).ok()
				.leftBumper.onPress(() -> {
					this.cameraIndex--;

					if (this.cameraIndex < 0) {
						this.cameraIndex = this.webcams.size() - 1;
					}

					this.updateCamera();
				}).ok();
		this.fx = this.telemetry.addData("fx: ", "(no data)");
		this.fy = this.telemetry.addData("fy: ", "(no data)");
		this.px = this.telemetry.addData("px: ", "(no data)");
		this.py = this.telemetry.addData("py: ", "(no data)");
		this.resolution = this.telemetry.addData("res: ", "(no data)");
		this.rms = this.telemetry.addData("rmsError: ", "(no data)");
		this.k1 = this.telemetry.addData("k1: ", "(no data)");
		this.k2 = this.telemetry.addData("k2: ", "(no data)");
		this.k3 = this.telemetry.addData("k3: ", "(no data)");
		this.p1 = this.telemetry.addData("start: ", "(no data)");
		this.p2 = this.telemetry.addData("p2: ", "(no data)");
		this.imagePoints = new ArrayList<>();
		this.objectPoints = new ArrayList<>();
		this.updateCamera();
	}

	@Override
	public void loop() {
	}

	// Create the 3D points for the checkerboard pattern
	// This represents where each corner is in 3D space
	// We assume the checkerboard is lying flat on XY plane, so Z=0
	//
	// Consider a 3x3 checkerboard with 25mm squares:
	// P1 --- P2 --- P3
	// |      |      |
	// P4 --- P5 --- P6
	// |      |      |
	// P7 --- P8 --- P9
	//
	// `objp` would be:
	// P1: (0.000, 0.000, 0)    P2: (0.025, 0.000, 0)    P3: (0.050, 0.000, 0)
	// P4: (0.000, 0.025, 0)    P5: (0.025, 0.025, 0)    P6: (0.050, 0.025, 0)
	// P7: (0.000, 0.050, 0)    P8: (0.025, 0.050, 0)    P9: (0.050, 0.050, 0)
	private Mat createCheckerboardPoints() {
		// Create matrix to store 3D points
		Mat objp = new Mat(CHECKERBOARD_HEIGHT * CHECKERBOARD_WIDTH, 3, CvType.CV_32F);

		// Fill the matrix with points
		// For example, if SQUARE_SIZE = 0.025 (25mm):
		// Point 0: (0, 0, 0)         - Top-left corner
		// Point 1: (0.025, 0, 0)     - One square right
		// Point 2: (0.050, 0, 0)     - Two squares right
		// ...and so on
		for (int row = 0; row < CHECKERBOARD_HEIGHT; row++) {
			for (int col = 0; col < CHECKERBOARD_WIDTH; col++) {
				objp.put(
						row * CHECKERBOARD_WIDTH + col,
						0,

						// data (x, y, z)
						col * SQUARE_SIZE_METERS,  // X coordinate
						row * SQUARE_SIZE_METERS,        // Y coordinate
						0                                // Z coordinate (flat board)
				);
			}
		}

		return objp;
	}

	private void calibrateCamera() {
		if (imagePoints.isEmpty()) {
			// No points found
			return;
		}

		// Calibrate camera
		Mat cameraMatrix = new Mat();
		Mat distCoeffs = new Mat();
		List<Mat> rvecs = new ArrayList<>();
		List<Mat> tvecs = new ArrayList<>();

		double rmsError = Calib3d.calibrateCamera(
				objectPoints,         // 3D points
				imagePoints,          // 2D points
				imageSize,            // Image size
				cameraMatrix,         // Output camera matrix
				distCoeffs,           // Output distortion coefficients
				rvecs,                // Output rotation vectors
				tvecs                 // Output translation vectors
		);

		// Extract intrinsic parameters
		fx.setValue(cameraMatrix.get(0, 0)[0]);  // Focal length in x
		fy.setValue(cameraMatrix.get(1, 1)[0]);  // Focal length in y
		px.setValue(cameraMatrix.get(0, 2)[0]);  // Principal point x
		py.setValue(cameraMatrix.get(1, 2)[0]);  // Principal point y
		rms.setValue(rmsError);
	}

	private void updateCamera() {
		if (this.webcam != null) {
			try {
				this.webcam.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.fx.setValue("(no data)");
		this.fy.setValue("(no data)");
		this.px.setValue("(no data)");
		this.py.setValue("(no data)");
		this.resolution.setValue("(no data)");
		this.rms.setValue("(no data)");
		this.k1.setValue("(no data)");
		this.k2.setValue("(no data)");
		this.k3.setValue("(no data)");
		this.p1.setValue("(no data)");
		this.p2.setValue("(no data)");
		this.imagePoints.clear();
		this.objectPoints.clear();
		this.imageSize = null;

		this.webcam = new Webcam(this.hardwareMap, this.webcams.get(this.cameraIndex));
		this.webcam.start(new VisionProcessor() {
			private Mat gray;

			@Override
			public void init(int width, int height, CameraCalibration cameraCalibration) {
				imageSize = new Size(width, height);
				this.gray = new Mat();
				resolution.setValue(width + "x" + height);
			}

			@Override
			public Object processFrame(Mat image, long ms) {
				// Convert to grayscale
				Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

				// Find checkerboard corners
				MatOfPoint2f corners = new MatOfPoint2f();

				boolean containsCheckerboard = Calib3d.findChessboardCorners(
						gray,
						CHECKERBOARD_PATTERN_SIZE,
						corners,
						Calib3d.CALIB_CB_ADAPTIVE_THRESH
								+ Calib3d.CALIB_CB_NORMALIZE_IMAGE
								+ Calib3d.CALIB_CB_FAST_CHECK
				);

				if (!containsCheckerboard) {
					return null;
				}

				// Refine corner locations
				TermCriteria criteria = new TermCriteria(
						TermCriteria.EPS + TermCriteria.COUNT, 30, 0.001
				);

				Imgproc.cornerSubPix(
						gray,
						corners,
						new Size(11, 11),
						new Size(-1, -1),
						criteria
				);

				// Add the points from this image
				objectPoints.add(createCheckerboardPoints());  // 3D points are same for each image
				imagePoints.add(corners);                      // 2D points are different for each image

				calibrateCamera();
				telemetry.update();

				return corners;
			}

			@SuppressLint("DefaultLocale")
			@Override
			public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {
				if (userContext == null) {
					return;
				}

				Point[] cornerPoints = ((MatOfPoint2f) userContext).toArray();

				for (int i = 0; i < cornerPoints.length; i++) {
					Point corner = cornerPoints[i];

					// Draw the point and its index on the visualization image
					canvas.drawCircle(
							(float) corner.x,
							(float) corner.y,
							2.5f,
							CORNER_POINT_PAINT
					);

					canvas.drawText(
							String.valueOf(i),
							(float) corner.x,
							(float) corner.y,
							CORNER_POINT_PAINT
					);
				}
			}
		});
	}
}
