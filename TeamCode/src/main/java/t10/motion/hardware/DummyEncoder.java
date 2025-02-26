package t10.motion.hardware;

public class DummyEncoder implements Encoder {
	private final double ticksPerRevolution;
	private final double encoderDiameterIn;
	public int ticks;

	public DummyEncoder(double ticksPerRevolution, double encoderDiameterIn) {
		this.ticksPerRevolution = ticksPerRevolution;
		this.encoderDiameterIn = encoderDiameterIn;
	}

	@Override
	public int getCurrentTicks() {
		return this.ticks;
	}

	@Override
	public double getCurrentInches() {
		return this.ticks / this.ticksPerRevolution * this.encoderDiameterIn * Math.PI;
	}

	@Override
	public void reset() {
	}
}
