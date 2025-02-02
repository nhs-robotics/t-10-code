package t10.metrics.packet;

import com.google.gson.annotations.SerializedName;

public class MetricsCameraFramePacket extends MetricsPacket {
	@SerializedName("jpegBase64")
	public String jpegBase64;
}
