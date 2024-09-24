package intothedeep;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import t10.bootstrap.Hardware;
import t10.novel.OdometryCoefficientSet;
import t10.novel.OmniDriveCoefficients;
import t10.novel.hardware.NovelEncoder;
import t10.novel.hardware.NovelMotor;
import t10.novel.hardware.NovelOdometry;
import t10.novel.motion.NovelMecanumDriver;
import t10.utils.RobotConfiguration;
import t10.vision.Webcam;

public class SamuelRobotConfiguration extends RobotConfiguration {
    @Hardware(name = "Webcam")
    public Webcam webcam;

    // Wheels
    @Hardware(
            name = "FL",
            diameterIn = Constants.Robot.ACTUAL_DIAMETER_IN,
            ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT,
            zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    )
    public NovelMotor fl;

    @Hardware(
            name = "FR",
            diameterIn = Constants.Robot.ACTUAL_DIAMETER_IN,
            ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT,
            zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    )
    public NovelMotor fr;

    @Hardware(
            name = "BL",
            diameterIn = Constants.Robot.ACTUAL_DIAMETER_IN,
            ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT,
            zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    )
    public NovelMotor bl;

    @Hardware(
            name = "BR",
            diameterIn = Constants.Robot.ACTUAL_DIAMETER_IN,
            ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT,
            zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    )
    public NovelMotor br;

    @Hardware(name = "imu")
    public IMU imu;

    // NOTE: LinearSlideLeft has the perpendicular  odometer encoder
    @Hardware(name = "LinearSlideLeft")
    public NovelMotor odometryPerpendicular;

    // NOTE: SpinningIntake has the right odometer encoder
    @Hardware(name = "SpinningIntake")
    public NovelMotor odometryRight;

    // NOTE: Roller has the left odometer encoder
    @Hardware(name = "Roller")
    public NovelMotor odometryLeft;

    public SamuelRobotConfiguration(HardwareMap hardwareMap) {
        super(hardwareMap);

        this.imu.initialize(
                new IMU.Parameters(new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                ))
        );
    }

    public NovelMecanumDriver createDriver(OmniDriveCoefficients coefficients) {
        return new NovelMecanumDriver(
                this.fl,
                this.fr,
                this.bl,
                this.br,
                this.imu,
                coefficients
        );
    }

    public NovelOdometry createOdometry() {
        return new NovelOdometry(
                new OdometryCoefficientSet(1, 1, 1),
                new NovelEncoder(this.odometryRight.motor, Constants.Odometry.ODOMETRY_WHEEL_DIAMETER_IN, Constants.Odometry.TICKS_PER_ODOMETRY_REVOLUTION),
                new NovelEncoder(this.odometryLeft.motor, Constants.Odometry.ODOMETRY_WHEEL_DIAMETER_IN, Constants.Odometry.TICKS_PER_ODOMETRY_REVOLUTION),
                new NovelEncoder(this.odometryPerpendicular.motor, Constants.Odometry.ODOMETRY_WHEEL_DIAMETER_IN, Constants.Odometry.TICKS_PER_ODOMETRY_REVOLUTION)
        );
    }
}
