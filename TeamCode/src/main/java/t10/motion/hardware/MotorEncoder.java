package t10.motion.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * A wrapper for an encoder, typically an odometry wheel.
 */
public class MotorEncoder implements Encoder {
	private final DcMotor encoder;
	private final double ticksPerRevolution;
	private final double encoderDiameterIn;

	/**
	 * Creates a NovelEncoder.
	 *
	 * @param encoder            The instance of the encoder from the hardware map. Yes, this is supposed to be a {@link DcMotor}.
	 * @param encoderDiameterIn  The diameter of the encoder wheel in inches.
	 * @param ticksPerRevolution The number of ticks the encoder has per revolution of the wheel.
	 */
	public MotorEncoder(DcMotor encoder, double encoderDiameterIn, double ticksPerRevolution) {
		this.encoder = encoder;
		this.encoder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		this.ticksPerRevolution = ticksPerRevolution;
		this.encoderDiameterIn = encoderDiameterIn;
	}

	/**
	 * @return The current position in ticks of the encoder.
	 */
	@Override
	public int getCurrentTicks() {
		return this.encoder.getCurrentPosition();
	}

	/**
	 * @return The current position in inches of the encoder.
	 */
	@Override
	public double getCurrentInches() {
		return this.getCurrentTicks() / this.ticksPerRevolution * this.encoderDiameterIn * Math.PI;
	}

	@Override
	public void reset() {
		this.encoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		this.encoder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
	}
}
