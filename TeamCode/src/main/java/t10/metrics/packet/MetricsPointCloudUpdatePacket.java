package t10.metrics.packet;

import com.google.gson.annotations.SerializedName;
import t10.geometry.Point3;

import java.util.List;

public class MetricsPointCloudUpdatePacket extends MetricsPacket {
    @SerializedName("points")
    public List<Point3> points;

    public MetricsPointCloudUpdatePacket() {

    }

    public MetricsPointCloudUpdatePacket(List<Point3> points) {
        this.points = points;
    }
}
