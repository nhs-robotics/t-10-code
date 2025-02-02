package t10.metrics.packet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class MetricsPacket {
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

	@SerializedName("_packetType")
	public String packetType;

	public MetricsPacket() {
		this.packetType = this.getClass().getSimpleName();
	}

	public String toString() {
		return GSON.toJson(this);
	}
}
