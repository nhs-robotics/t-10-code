package t10.metrics.packet;

import com.google.gson.annotations.SerializedName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import t10.geometry.Point;
import t10.geometry.Pose;

public class MetricsUpdatePacket extends MetricsPacket {
    @SerializedName("metricName")
    public String metricName;

    @SerializedName("metricType")
    public MetricsType metricType;

    @SerializedName("metricValue")
    public Object metricValue;

    public enum MetricsType {
        @SerializedName("integer")
        INTEGER(Integer.class),

        @SerializedName("double")
        DOUBLE(Double.class),

        @SerializedName("string")
        STRING(String.class),

        @SerializedName("point")
        POINT(MetricTypePoint.class),

        @SerializedName("pose")
        POSE(MetricTypePose.class);

        public final Class<?> metricTypeClass;

        MetricsType(Class<?> metricTypeClass) {
            this.metricTypeClass = metricTypeClass;
        }
    }

    public static class MetricTypePoint {
        @SerializedName("x")
        public double x;

        @SerializedName("y")
        public double y;

        public MetricTypePoint(Point point) {
            this.x = point.getX();
            this.y = point.getY();
        }
    }

    public static class MetricTypePose extends MetricTypePoint {
        @SerializedName("heading")
        public double heading;

        public MetricTypePose(Pose pose) {
            super(pose);
            this.heading = pose.getHeading(AngleUnit.RADIANS);
        }
    }
}
