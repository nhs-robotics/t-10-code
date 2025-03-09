package t10.metrics;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.util.Base64;
import org.firstinspires.ftc.robotcore.external.function.Continuation;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import t10.Loop;
import t10.bootstrap.BootstrappedOpMode;
import t10.geometry.Point;
import t10.geometry.Pose;
import t10.metrics.packet.MetricsCameraFramePacket;
import t10.metrics.packet.MetricsNewConnectionPacket;
import t10.metrics.packet.MetricsPacket;
import t10.metrics.packet.MetricsUpdatePacket;

public class MetricsServer extends WebSocketServer implements Loop {
	private final BootstrappedOpMode opMode;
	private final List<Field> metricFields;

	public MetricsServer(BootstrappedOpMode opMode) {
		super(new InetSocketAddress(51631));
		this.opMode = opMode;
		this.metricFields = new ArrayList<>();

		for (Field field : this.opMode.getClass().getFields()) {
			if (field.isAnnotationPresent(Metric.class)) {
				this.metricFields.add(field);
			}
		}

		this.setReuseAddr(true);
		this.setTcpNoDelay(true);
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		conn.send(new MetricsNewConnectionPacket(this.opMode).toString());
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
	}

	@Override
	public void onStart() {
	}

	@Override
	public void loop() {
//		if (this.webcam != null) {
//			webcam.visionPortal.getFrameBitmap(Continuation.createTrivial(bitmap -> {
//				if (MetricsServer.this.getConnections().isEmpty()) {
//					return;
//				}
//
//				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//				bitmap.compress(Bitmap.CompressFormat.JPEG, 15, outputStream);
//				String jpegBase64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
//				MetricsCameraFramePacket pkt = new MetricsCameraFramePacket();
//				pkt.jpegBase64 = jpegBase64;
//
//				MetricsServer.this.sendPacket(pkt);
//			}));
//		}
//
//		for (Field metricField : metricFields) {
//			metricField.setAccessible(true);
//
//			try {
//				Object o = metricField.get(this.opMode);
//				Class<?> metricFieldType = metricField.getType();
//				MetricsUpdatePacket metricsUpdatePacket = new MetricsUpdatePacket();
//				metricsUpdatePacket.metricName = metricField.getName();
//
//				if (metricFieldType == Double.class || metricFieldType == Float.class) {
//					metricsUpdatePacket.metricType = MetricsUpdatePacket.MetricsType.DOUBLE;
//					metricsUpdatePacket.metricValue = o;
//				} else if (metricFieldType == String.class) {
//					metricsUpdatePacket.metricType = MetricsUpdatePacket.MetricsType.STRING;
//					metricsUpdatePacket.metricValue = o;
//				} else if (metricFieldType == Point.class) {
//					metricsUpdatePacket.metricType = MetricsUpdatePacket.MetricsType.POINT;
//
//					if (o != null) {
//						metricsUpdatePacket.metricValue = new MetricsUpdatePacket.MetricTypePoint((Point) o);
//					}
//				} else if (metricFieldType == Pose.class) {
//					metricsUpdatePacket.metricType = MetricsUpdatePacket.MetricsType.POSE;
//
//					if (o != null) {
//						metricsUpdatePacket.metricValue = new MetricsUpdatePacket.MetricTypePose((Pose) o);
//					}
//				} else {
//					metricsUpdatePacket.metricType = MetricsUpdatePacket.MetricsType.STRING;
//
//					if (o != null) {
//						metricsUpdatePacket.metricValue = o.toString();
//					}
//				}
//
//				this.sendPacket(metricsUpdatePacket);
//			} catch (IllegalAccessException e) {
//				throw new RuntimeException(e);
//			}
//		}
	}

	public void sendPacket(MetricsPacket packet) {
		this.broadcast(packet.toString());
	}
}
