package intothedeep;

import com.qualcomm.hardware.digitalchickenlabs.OctoQuad;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import t10.bootstrap.AbstractRobotConfiguration;
import t10.bootstrap.Hardware;
import t10.motion.hardware.Motor;
import t10.motion.hardware.OctoQuadEncoder;
import t10.localizer.odometry.OdometryLocalizer;
import t10.motion.mecanum.MecanumDriver;
import t10.localizer.odometry.OdometryCoefficientSet;

public class SnowballConfig extends AbstractRobotConfiguration {
    // Wheels
    @Hardware(
            name = "FL",
            diameterIn = Constants.Robot.ACTUAL_DIAMETER_IN,
            ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT,
            zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    )
    public Motor fl;

    @Hardware(
            name = "FR",
            diameterIn = Constants.Robot.ACTUAL_DIAMETER_IN,
            ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT,
            zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    )
    public Motor fr;

    @Hardware(
            name = "BL",
            diameterIn = Constants.Robot.ACTUAL_DIAMETER_IN,
            ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT,
            zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    )
    public Motor bl;

    @Hardware(
            name = "BR",
            diameterIn = Constants.Robot.ACTUAL_DIAMETER_IN,
            ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT,
            zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    )
    public Motor br;

    @Hardware(name = "imu")
    public IMU imu;

    @Hardware(name = "LiftRight",
            ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT)
    public Motor liftRight;

    @Hardware(name = "LiftLeft", 
            ticksPerRevolution = Constants.TickCounts.MOVEMENT_MOTOR_TICK_COUNT)
    public Motor liftLeft;

    @Hardware(name = "ArmExtension")
    public Motor armExtension;

    @Hardware(name = "ArmRotation")
    public Motor armRotation;

    @Hardware(name = "OctoQuad")
    public OctoQuad octoQuad;
  
    public SnowballConfig(HardwareMap hardwareMap) {
        super(hardwareMap);

        this.imu.initialize(
                new IMU.Parameters(new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                ))
        );
    }

    @Override
    public MecanumDriver createMecanumDriver() {
        return new MecanumDriver(
                this.fl,
                this.fr,
                this.bl,
                this.br,
                Constants.Coefficients.SNOWBALL_COEFFICIENTS
        );
    }

    public OdometryLocalizer createOdometry() {
        return new OdometryLocalizer(
                new OdometryCoefficientSet(1, 1, -1),
                // 4-6-5 is right-left-perpendicular
                new OctoQuadEncoder(octoQuad, 4, Constants.Odometry.ODOMETRY_WHEEL_DIAMETER_IN, Constants.Odometry.TICKS_PER_ODOMETRY_REVOLUTION),
                new OctoQuadEncoder(octoQuad, 6, Constants.Odometry.ODOMETRY_WHEEL_DIAMETER_IN, Constants.Odometry.TICKS_PER_ODOMETRY_REVOLUTION),
                new OctoQuadEncoder(octoQuad, 5, Constants.Odometry.ODOMETRY_WHEEL_DIAMETER_IN, Constants.Odometry.TICKS_PER_ODOMETRY_REVOLUTION),
                11.5,
                -6.5
        );
    }
}
