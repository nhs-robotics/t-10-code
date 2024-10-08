package intothedeep.teleop;

import t10.bootstrap.AbstractRobotConfiguration;
import t10.bootstrap.Hardware;
import t10.novel.NovelMotor;
import t10.novel.mecanum.MecanumDriver;
import t10.novel.odometry.NovelOdometry;
import t10.novel.odometry.OdometryCoefficientSet;
import t10.vision.Webcam;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.*;
import intothedeep.Constants;
import t10.novel.odometry.NovelOdometry;

public class AzazelRobotConfiguration extends AbstractRobotConfiguration {
    @Hardware(name = "Webcam")
    public Webcam webcam;

    /***************\
     |* CONTROL HUB *|
     \***************/

    // Wheels
    // NOTE: LinearSlideLeft has the perpendicular  odometer encoder
    @Hardware(name = "LinearSlideLeft")
    public NovelMotor linearSlideLeft;

    @Hardware(name = "LinearSlideRight")
    public NovelMotor linearSlideRight;

    // NOTE: LinearSlideLeft has the right odometer encoder
    @Hardware(name = "SpinningIntake")
    public NovelMotor spinningIntake;

    // NOTE: LinearSlideLeft has the left odometer encoder
    @Hardware(name = "Roller")
    public NovelMotor roller;

    // Servos
    @Hardware(name = "AirplaneLauncher")
    public Servo airplaneLauncher;

    /*****************\
     |* EXPANSION HUB *|
     \*****************/

    // Wheels
    @Hardware(name = "FL", diameterIn = 3.7795275590551185, ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT, zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE)
    public NovelMotor fl;

    @Hardware(name = "FR", diameterIn = 3.7795275590551185, ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT, zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE)
    public NovelMotor fr;

    @Hardware(name = "BL", diameterIn = 3.7795275590551185, ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT, zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE)
    public NovelMotor bl;

    @Hardware(name = "BR", diameterIn = 3.7795275590551185, ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT, zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE)
    public NovelMotor br;

    // Servos
    @Hardware(name = "ContainerPixelHolder")
    public Servo containerPixelHolder;

    @Hardware(name = "ContainerRotationLeft")
    // NOTE: The left servo is able to rotate the container downwards from halfway, but not upwards from halfway.
    public Servo containerRotationLeft;

    @Hardware(name = "ContainerRotationRight")
    // NOTE: The right servo is able to rotate the container upwards from halfway, but not downwards from halfway.
    public Servo containerRotationRight;

    @Hardware(name = "imu")
    public IMU imu;

    public AzazelRobotConfiguration(HardwareMap hardwareMap) {
        super(hardwareMap);
        imu.initialize(
                new IMU.Parameters(new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                ))
        );
    }

    @Override
    public MecanumDriver createMecanumDriver() {
        return new MecanumDriver(fl, fr, bl, br, imu, Constants.Coefficients.PRODUCTION_COEFFICIENTS);
    }

    @Override
    public NovelOdometry createOdometry() {
        return new NovelOdometry(OdometryCoefficientSet.DEFAULT, spinningIntake.encoder, roller.encoder, linearSlideLeft.encoder, Constants.Odometry.ODOMETRY_LATERAL_WHEEL_DISTANCE, Constants.Odometry.ODOMETRY_PERPENDICULAR_WHEEL_OFFSET);
    }
}
