package t10.vision;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import intothedeep.Constants;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class SampleAlignmentProcessor implements VisionProcessor {
	private final SampleColor color;
	private Mat hsvMat;
	private Mat filteredMaskMat1;  // Used only for red (because red has an upper and lower range on HSV spectrum)
	private Mat filteredMaskMat2;  // Used only for red (because red has an upper and lower range on HSV spectrum)
	private Mat maskMat;
	public static final double ADJUSTMENT_ANGLE = 4.9;
	public static final double SAMPLE_WIDTH = 1.5;  // samples are 1.5in wide
	public static final double SAMPLE_HEIGHT = 3.5;  // samples are 3.5in tall
	public static final int CENTER_X_POSITION = 223;

	// Color ranges in HSV
	private static final Scalar YELLOW_LOWER = new Scalar(20, 100, 70);
	private static final Scalar YELLOW_UPPER = new Scalar(32, 255, 255);
	private static final Scalar BLUE_LOWER = new Scalar(90, 160, 30);
	private static final Scalar BLUE_UPPER = new Scalar(130, 255, 255);
	private static final Scalar RED_LOWER_1 = new Scalar(0, 150, 100);
	private static final Scalar RED_UPPER_1 = new Scalar(10, 255, 255);
	private static final Scalar RED_LOWER_2 = new Scalar(170, 150, 100);
	private static final Scalar RED_UPPER_2 = new Scalar(180, 255, 255);
	private Mat adjustmentRotationMatrix;
	private List<MatOfPoint> contours;
	private Mat contourHierarchy;
	private Mat coloredMaskedMat;
	public Rect detectedSpecimen;
	private Mat morphologyKernel;

	public SampleAlignmentProcessor(SampleColor color) {
		this.color = color;
	}

	@Override
	public void init(int width, int height, CameraCalibration cameraCalibration) {
		this.hsvMat = new Mat();
		this.filteredMaskMat1 = new Mat();
		this.filteredMaskMat2 = new Mat();
		this.maskMat = new Mat();
		this.coloredMaskedMat = new Mat();
		this.contours = new ArrayList<>();
		this.contourHierarchy = new Mat();
		this.adjustmentRotationMatrix = Imgproc.getRotationMatrix2D(new Point(width / 2d, height / 2d), ADJUSTMENT_ANGLE, 1);
		this.morphologyKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
	}

	@Override
	public Object processFrame(Mat mat, long ms) {
		Imgproc.warpAffine(mat, mat, this.adjustmentRotationMatrix, mat.size(), Imgproc.INTER_LINEAR);
//		mat = mat.submat(0, mat.height(), 0, mat.width() - 150); // crop the visible claw
		Imgproc.cvtColor(mat, this.hsvMat, Imgproc.COLOR_RGB2HSV);

		if (Objects.requireNonNull(this.color) == SampleColor.BLUE) {
			Core.inRange(hsvMat, BLUE_LOWER, BLUE_UPPER, maskMat);
		} else if (this.color == SampleColor.RED) {
			Core.inRange(hsvMat, RED_LOWER_1, RED_UPPER_1, filteredMaskMat1);
			Core.inRange(hsvMat, RED_LOWER_2, RED_UPPER_2, filteredMaskMat2);
			Core.bitwise_or(filteredMaskMat1, filteredMaskMat2, maskMat);
		} else if (this.color == SampleColor.YELLOW) {
			Core.inRange(hsvMat, YELLOW_LOWER, YELLOW_UPPER, maskMat);
		}

		Imgproc.morphologyEx(this.maskMat, this.maskMat, Imgproc.MORPH_CLOSE, this.morphologyKernel);
		Core.bitwise_and(this.hsvMat, this.hsvMat, this.coloredMaskedMat, this.maskMat);
		Imgproc.findContours(this.maskMat, this.contours, this.contourHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		this.contourHierarchy.release();

		int largestArea = -1;
		Rect largestRect = null;

		for (MatOfPoint contour : this.contours) {
			Rect rect = Imgproc.boundingRect(contour);
			int area = rect.width * rect.height;

			if (largestArea < 0 || area > largestArea) {
				largestArea = area;
				largestRect = rect;
			}

			contour.release();
		}

		this.contours.clear();

		if (largestRect == null) {
			this.detectedSpecimen = null;
			return null;
		}

		this.detectedSpecimen = largestRect;
		return null;
	}

	@Override
	public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight,
							float scaleBmpPxToCanvasPx, float scaleCanvasDensity,
							Object userContext) {
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setTextSize(28);
		paint.setStrokeWidth(2 * scaleBmpPxToCanvasPx);
//
		if (this.detectedSpecimen != null) {
			canvas.drawRect(new android.graphics.Rect(
					(int) (this.detectedSpecimen.x * scaleBmpPxToCanvasPx),
					(int) (this.detectedSpecimen.y * scaleBmpPxToCanvasPx),
					(int) ((this.detectedSpecimen.x + this.detectedSpecimen.width) * scaleBmpPxToCanvasPx),
					(int) ((this.detectedSpecimen.y + this.detectedSpecimen.height) * scaleBmpPxToCanvasPx)
			), paint);
		}

//		canvas.drawText("right: " + Core.countNonZero(this.rightMat), 0, 90, paint);

		canvas.drawLine(CENTER_X_POSITION * scaleBmpPxToCanvasPx, 0, CENTER_X_POSITION * scaleBmpPxToCanvasPx, canvas.getHeight(), paint);

//		for (int x = 0; canvas.getWidth() > x; x += 2) {
//			for (int y = 0; canvas.getHeight() > y; y += 2) {
//				double[] doubles = this.maskMat.get(y, x);
//				if (doubles != null && doubles[0] > 0) {
//					canvas.drawPoint(x * scaleBmpPxToCanvasPx, y * scaleBmpPxToCanvasPx, paint);
//				}
//			}
//		}
	}

	public enum SampleColor {
		RED,
		BLUE,
		YELLOW
	}
}