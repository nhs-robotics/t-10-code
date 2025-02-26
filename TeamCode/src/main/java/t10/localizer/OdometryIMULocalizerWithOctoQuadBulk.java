package t10.localizer;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.digitalchickenlabs.OctoQuad;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import t10.geometry.Pose;
import t10.motion.hardware.DummyEncoder;
import t10.motion.hardware.Encoder;
import t10.utils.MathUtils;

public class OdometryIMULocalizerWithOctoQuadBulk extends OdometryIMULocalizer {
	private final OctoQuad theOctoQuad;
	private final int rightChannel;
	private final int leftChannel;
	private final int perpendicularChannel;

	/**
	 * @param coefficients             The coefficients to use for the odometers. Chances are this is {@link OdometryCoefficientSet#DEFAULT}.
	 * @param octoQuad                 The OctoQuad which contains all the encoders.
	 * @param lateralWheelDistance     The distance between the lateral wheels.
	 * @param perpendicularWheelOffset The offset of the perpendicular wheel from the center of the robot chassis.
	 * @see <a href="http://web.archive.org/web/20230529000105if_/https://gm0.org/en/latest/_images/offsets-and-trackwidth.png">Diagram (archived)</a> or <a href="https://gm0.org/en/latest/_images/offsets-and-trackwidth.png">diagram</a>.
	 */
	public OdometryIMULocalizerWithOctoQuadBulk(OdometryCoefficientSet coefficients, OctoQuad octoQuad, int rightChannel, int leftChannel, int perpendicularChannel, double lateralWheelDistance, double perpendicularWheelOffset, double ticksPerRevolution, double encoderDiameterIn, BNO055IMU imu) {
		super(
				coefficients,
				new DummyEncoder(ticksPerRevolution, encoderDiameterIn),
				new DummyEncoder(ticksPerRevolution, encoderDiameterIn),
				new DummyEncoder(ticksPerRevolution, encoderDiameterIn),
				lateralWheelDistance,
				perpendicularWheelOffset,
				imu
		);
		this.theOctoQuad = octoQuad;
		this.rightChannel = rightChannel;
		this.leftChannel = leftChannel;
		this.perpendicularChannel = perpendicularChannel;
		this.theOctoQuad.resetAllPositions();
	}

	private void updatePositions() {
		int[] positions = {0,0,0,0,0,0,0,0};
		try {
			positions = this.theOctoQuad.readAllPositions();
		} catch (Exception ignored) {
			return;
		}

		((DummyEncoder) this.rightEncoder).ticks = positions[this.rightChannel];
		((DummyEncoder) this.leftEncoder).ticks = positions[this.leftChannel];
		((DummyEncoder) this.perpendicularEncoder).ticks = positions[this.perpendicularChannel];
	}

	@Override
	public void loop() {
		this.updatePositions();
		super.loop();
	}

	@Override
	public void setFieldCentric(Pose pose) {
		this.theOctoQuad.resetAllPositions();
		this.updatePositions();
		super.setFieldCentric(pose);
	}
}
