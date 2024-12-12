package intothedeep.teleop;

import com.qualcomm.hardware.digitalchickenlabs.OctoQuad;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import intothedeep.Constants;
import t10.bootstrap.AbstractRobotConfiguration;
import t10.bootstrap.Hardware;
import t10.bootstrap.TeleOpOpMode;
import t10.localizer.odometry.OdometryLocalizer;
import t10.motion.NovelMotor;
import t10.motion.OdometryEncoder;
import t10.motion.mecanum.MecanumDriver;

@TeleOp(name = "OctoQuadTestTeleop")
public class OctoQuadTestTeleop extends TeleOpOpMode {
    OctoQuadTestConfiguration c;
    Telemetry.Item r;

    @Override
    public void initialize() {
        this.c = new OctoQuadTestConfiguration(this.hardwareMap);
        this.r = this.telemetry.addData("Rotation: ", "0");
    }

    @Override
    public void loop() {
        if (this.gamepad1.a) {
            this.c.novelMotor.setPower(0.2);
        } else if (this.gamepad1.b) {
            this.c.novelMotor.setPower(-0.2);
        } else {
            this.c.novelMotor.setPower(0);
        }
        this.r.setValue(this.c.odometryEncoder.getCurrentInches());
    }
}

class OctoQuadTestConfiguration extends AbstractRobotConfiguration {
    public OctoQuadTestConfiguration(HardwareMap hardwareMap) {
        super(hardwareMap);
    }

    @Hardware(name = "OctoQuad")
    public OctoQuad octoQuad;

    public OdometryEncoder odometryEncoder = new OdometryEncoder(octoQuad, 0,
            Constants.Odometry.ODOMETRY_WHEEL_DIAMETER_IN,
            Constants.Odometry.TICKS_PER_ODOMETRY_REVOLUTION);

    @Hardware(name = "Motor",
    zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
    )
  
    public NovelMotor novelMotor;

    @Override
    public MecanumDriver createMecanumDriver() {
        return null;
    }

    @Override
    public OdometryLocalizer createOdometry() {
        return null;
    }
}
