package t10.motion;

import com.qualcomm.hardware.digitalchickenlabs.OctoQuad;

public class OctoQuadEncoder extends AbstractEncoder {
    private final OctoQuad octoQuad;
    private final int channel;
    private final double ticksPerRevolution;
    private final double encoderDiameterIn;

    /**
     * Creates an OdometryEncoder.
     *
     * @param octoQuad The octoQuad that the odometry wire is connected to.
     * @param channel The index of the channel that the encoder is plugged into
     * @param encoderDiameterIn The diameter of the encoder wheel in inches.
     * @param ticksPerRevolution The number of ticks the encoder has per revolution of the wheel.
     */
    public OctoQuadEncoder(OctoQuad octoQuad, int channel, double encoderDiameterIn, double ticksPerRevolution) {
        this.octoQuad = octoQuad;
        this.channel = channel;
        this.ticksPerRevolution = ticksPerRevolution;
        this.encoderDiameterIn = encoderDiameterIn;
    }

    /**
     * @return The current position in ticks of the encoder.
     */
    public int getCurrentTicks() {
        return this.octoQuad.readSinglePosition(channel);
    }

    /**
     * @return The current position in inches of the encoder.
     */
    public double getCurrentInches() {
        return this.getCurrentTicks() / this.ticksPerRevolution * this.encoderDiameterIn * Math.PI;
    }
}


