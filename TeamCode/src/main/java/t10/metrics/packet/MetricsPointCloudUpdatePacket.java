package t10.metrics.packet;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import t10.geometry.Point3;

public class MetricsPointCloudUpdatePacket extends MetricsPacket {
	@SerializedName("points")
	public List<Point3> points;

	public MetricsPointCloudUpdatePacket() {
	}

	public MetricsPointCloudUpdatePacket(List<Point3> points) {
		this.points = points;
	}
}
