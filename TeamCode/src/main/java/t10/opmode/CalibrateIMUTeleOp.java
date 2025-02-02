package t10.opmode;

import java.io.File;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ReadWriteFile;
import intothedeep.SnowballConfig;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import t10.bootstrap.BootstrappedOpMode;

@TeleOp
public class CalibrateIMUTeleOp extends BootstrappedOpMode {
	private SnowballConfig config;
	private File file;
	private Telemetry.Item accl;
	private Telemetry.Item gyro;
	private Telemetry.Item acclxyz;
	private Telemetry.Item gyroxyz;

	@Override
	public void init() {
		super.init();
		this.config = new SnowballConfig(hardwareMap);
		this.accl = this.telemetry.addData("accl ", "");
		this.gyro = this.telemetry.addData("gyro ", "");
		this.acclxyz = this.telemetry.addData("accl x/y/z ", "");
		this.gyroxyz = this.telemetry.addData("gyro x/y/z ", "");

		this.file = AppUtil.getInstance().getSettingsFile("BNO055IMUCalibration.json");
	}

	@Override
	public void loop() {
		accl.setValue(this.config.imu.isAccelerometerCalibrated());
		gyro.setValue(this.config.imu.isGyroCalibrated());
		Acceleration linearAcceleration = this.config.imu.getLinearAcceleration();
		acclxyz.setValue(String.format("%.4f / %.4f / %.4f", linearAcceleration.xAccel, linearAcceleration.yAccel, linearAcceleration.zAccel));

		Orientation angularOrientation = this.config.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
		gyroxyz.setValue(String.format("%.4f / %.4f / %.4f", angularOrientation.firstAngle, angularOrientation.secondAngle, angularOrientation.thirdAngle));

		if (this.gamepad1.a) {
			BNO055IMU.CalibrationData calibrationData = this.config.imu.readCalibrationData();
			ReadWriteFile.writeFile(file, calibrationData.serialize());
		}

		this.telemetry.update();
	}
}
