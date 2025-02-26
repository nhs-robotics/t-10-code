package t10.localizer;

import com.qualcomm.hardware.bosch.BNO055IMU;
import intothedeep.Constants;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import t10.geometry.Pose;
import t10.motion.hardware.Encoder;
import t10.utils.MathUtils;

public class OdometryIMULocalizer extends OdometryLocalizer {
	private final BNO055IMU imu;
	private float lastHeading;

	/**
	 * @param coefficients             The coefficients to use for the odometers. Chances are this is {@link OdometryCoefficientSet#DEFAULT}.
	 * @param rightEncoder             The right side encoder.
	 * @param leftEncoder              The left side encoder.
	 * @param perpendicularEncoder     The perpendicular encoder.
	 * @param lateralWheelDistance     The distance between the lateral wheels.
	 * @param perpendicularWheelOffset The offset of the perpendicular wheel from the center of the robot chassis.
	 * @see <a href="http://web.archive.org/web/20230529000105if_/https://gm0.org/en/latest/_images/offsets-and-trackwidth.png">Diagram (archived)</a> or <a href="https://gm0.org/en/latest/_images/offsets-and-trackwidth.png">diagram</a>.
	 */
	public OdometryIMULocalizer(OdometryCoefficientSet coefficients, Encoder rightEncoder, Encoder leftEncoder, Encoder perpendicularEncoder, double lateralWheelDistance, double perpendicularWheelOffset, BNO055IMU imu) {
		super(coefficients, rightEncoder, leftEncoder, perpendicularEncoder, lateralWheelDistance, perpendicularWheelOffset);
		this.imu = imu;

		// If we don't compute it initially, we're going to get a really large delta when we're actually localizing. This would throw us off a lot.
		this.computeDeltaHeading();
	}

	@Override
	protected double computeDeltaHeading() {
		Orientation angularOrientation = this.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS);
		final float dao = (float) (2 * Math.PI);
		float currentHeading = MathUtils.clamp(angularOrientation.firstAngle * -1, -dao, dao);
		double deltaHeading = MathUtils.angleDifference(this.lastHeading, currentHeading, AngleUnit.RADIANS);
		this.lastHeading = currentHeading;

		return deltaHeading;
	}

	@Override
	public void setFieldCentric(Pose pose) {
		super.setFieldCentric(pose);
		this.imu.initialize(Constants.IMU_PARAMETERS);
	}
}
