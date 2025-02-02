package t10.metrics.packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.SerializedName;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;

import t10.bootstrap.BootstrappedOpMode;

public class MetricsNewConnectionPacket extends MetricsPacket {
	public MetricsNewConnectionPacket() {
	}

	public MetricsNewConnectionPacket(BootstrappedOpMode bootstrappedOpMode) {
		super();

		// Op mode metadata
		Autonomous autonomousAnnotation = bootstrappedOpMode.getClass().getAnnotation(Autonomous.class);
		TeleOp teleOpAnnotation = bootstrappedOpMode.getClass().getAnnotation(TeleOp.class);

		if (autonomousAnnotation != null) {
			this.opModeName = autonomousAnnotation.name();
			this.opModeType = MetricsOpModeType.AUTONOMOUS;
		} else if (teleOpAnnotation != null) {
			this.opModeName = teleOpAnnotation.name();
			this.opModeType = MetricsOpModeType.TELE_OP;
		} else {
			throw new IllegalStateException("not running a valid op mode: neither Auto nor Tele-Op");
		}

		if (this.opModeName.isEmpty()) {
			this.opModeName = bootstrappedOpMode.getClass().getSimpleName();
		}

		// Hardware map
		this.hardware = new ArrayList<>();

		for (HardwareMap.DeviceMapping<? extends HardwareDevice> hardwareDevices : bootstrappedOpMode.hardwareMap.allDeviceMappings) {
			for (HardwareDevice hardwareDevice : hardwareDevices) {
				MetricsHardwareDevice metricsHardwareDevice = new MetricsHardwareDevice();
				Set<String> names = bootstrappedOpMode.hardwareMap.getNamesOf(hardwareDevice);
				String name;

				if (names.isEmpty()) {
					name = "(unknown)";
				} else {
					name = String.join("/", names);
				}

				metricsHardwareDevice.name = name;
				metricsHardwareDevice.type = hardwareDevice.getClass().getSimpleName() + " (" + hardwareDevice.getDeviceName() + ")";
				metricsHardwareDevice.connectionDetails = hardwareDevice.getConnectionInfo();
				metricsHardwareDevice.version = hardwareDevice.getVersion();

				this.hardware.add(metricsHardwareDevice);
			}
		}
	}

	@SerializedName("opModeName")
	public String opModeName;

	@SerializedName("opModeType")
	public MetricsOpModeType opModeType;

	@SerializedName("hardware")
	public List<MetricsHardwareDevice> hardware;

	public enum MetricsOpModeType {
		@SerializedName("autonomous")
		AUTONOMOUS,

		@SerializedName("teleOp")
		TELE_OP;
	}

	public static class MetricsHardwareDevice {
		@SerializedName("name")
		public String name;

		@SerializedName("type")
		public String type;

		@SerializedName("connectionDetails")
		public String connectionDetails;

		@SerializedName("version")
		public int version;
	}
}
