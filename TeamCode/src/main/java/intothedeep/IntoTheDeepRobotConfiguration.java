package intothedeep;

import com.qualcomm.hardware.digitalchickenlabs.OctoQuad;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.bootstrap.AbstractRobotConfiguration;
import t10.bootstrap.Hardware;
import t10.novel.NovelEncoder;
import t10.novel.NovelMotor;
import t10.novel.OdometryEncoder;
import t10.novel.mecanum.MecanumDriver;
import t10.novel.odometry.NovelOdometry;
import t10.novel.odometry.OdometryCoefficientSet;
import t10.localizer.odometry.OdometryLocalizer;
import t10.motion.NovelEncoder;
import t10.motion.NovelMotor;
import t10.motion.mecanum.MecanumDriver;
import t10.localizer.odometry.OdometryCoefficientSet;

public class IntoTheDeepRobotConfiguration extends AbstractRobotConfiguration {

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

    public IntoTheDeepRobotConfiguration(HardwareMap hardwareMap) {
        super(hardwareMap);

        OctoQuad octoQuad = hardwareMap.get(OctoQuad.class,"OctoQuad");

        this.imu.initialize(
                new IMU.Parameters(new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                ))
        );
    }

    @Hardware(name = "OL")
    public NovelMotor odometryLeft;

    @Hardware(name = "OR")
    public NovelMotor odometryRight;

    @Hardware(name = "OP")
    public NovelMotor odometryPerpendicular;

    @Hardware(name = "OctoQuad")
    public OctoQuad octoQuad;


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

    public NovelOdometry createOdometry() {
        return new NovelOdometry(
                new OdometryCoefficientSet(1, 1, 1),
                // 4-5-6 is right-left-perpendicular
                new OdometryEncoder(octoQuad,4, Constants.Odometry.ODOMETRY_WHEEL_DIAMETER_IN, Constants.Odometry.TICKS_PER_ODOMETRY_REVOLUTION),
                new OdometryEncoder(octoQuad,5, Constants.Odometry.ODOMETRY_WHEEL_DIAMETER_IN, Constants.Odometry.TICKS_PER_ODOMETRY_REVOLUTION),
                new OdometryEncoder(octoQuad,6, Constants.Odometry.ODOMETRY_WHEEL_DIAMETER_IN, Constants.Odometry.TICKS_PER_ODOMETRY_REVOLUTION),
                11.5,
                -6.5
        );
    }
}

class OctoSwerveModule {

    public  double driveCounts;
    public  double driveCountsPerSec;
    public  double steerDegrees;
    public  double steerDegreesPerSec;

    private final String   name;
    private final int      channel;
    private final double   angleOffset;
    private final double   steerDirMult;

    private static final int    VELOCITY_SAMPLE_INTERVAL_MS = 25;   // To provide 40 updates per second.
    private static final double DEGREES_PER_US = (360.0 / 1024.0);  // based on REV Through Bore Encoder
    private static final double VELOCITY_SAMPLES_PER_S = (1000.0 / VELOCITY_SAMPLE_INTERVAL_MS);

    // The correct drive and turn directions must be set for the Swerve Module based on the specific hardware geometry.
    // Forward motion must generate an increasing drive count.
    // Counter Clockwise steer rotation must generate an increasing Steer Angle (degrees)
    private static final boolean INVERT_DRIVE_ENCODER = false; // Set true if forward motion decreases drive "Count"
    private static final boolean INVERT_STEER_ENCODER = false; // Set true if counter clockwise steer action decreases steer "Degree"

    /***
     * @param octoquad provide access to configure OctoQuad
     * @param name name used for telemetry display
     * @param quadChannel Quadrature encoder channel.  Pulse Width channel is this + 4
     * @param angleOffset Angle to subtract from absolute encoder to calibrate zero position. (see comments above)
     */
    public OctoSwerveModule (OctoQuad octoquad, String name, int quadChannel, double angleOffset) {
        this.name = name;
        this.channel = quadChannel;
        this.angleOffset = angleOffset;
        this.steerDirMult = INVERT_STEER_ENCODER  ? -1 : 1 ;  // create a multiplier to flip the steer angle.

        // Set the drive encoder direction.  Note the absolute encoder does not have built-in direction inversion.
        octoquad.setSingleEncoderDirection(channel, INVERT_DRIVE_ENCODER ? OctoQuad.EncoderDirection.REVERSE : OctoQuad.EncoderDirection.FORWARD);

        // Set the velocity sample interval on both encoders
        octoquad.setSingleVelocitySampleInterval(channel, VELOCITY_SAMPLE_INTERVAL_MS);
        octoquad.setSingleVelocitySampleInterval(channel + 4, VELOCITY_SAMPLE_INTERVAL_MS);

        // Setup Absolute encoder pulse range to match REV Through Bore encoder.
        octoquad.setSingleChannelPulseWidthParams (channel + 4, new OctoQuad.ChannelPulseWidthParams(1,1024));
    }

    /***
     * Calculate the Swerve module's position and velocity values
     * @param encoderDataBlock  most recent full data block read from OctoQuad.
     */
    public void updateModule(OctoQuad.EncoderDataBlock encoderDataBlock) {
        driveCounts = encoderDataBlock.positions[channel];  // get Counts.
        driveCountsPerSec = encoderDataBlock.velocities[channel] * VELOCITY_SAMPLES_PER_S; // convert counts/interval to counts/sec

        // convert uS to degrees.  Add in any possible direction flip.
        steerDegrees = AngleUnit.normalizeDegrees((encoderDataBlock.positions[channel+ 4] * DEGREES_PER_US * steerDirMult) - angleOffset);
        // convert uS/interval to deg per sec.  Add in any possible direction flip.
        steerDegreesPerSec = encoderDataBlock.velocities[channel + 4] * DEGREES_PER_US * steerDirMult * VELOCITY_SAMPLES_PER_S;
    }

    /**
     * Display the Swerve module's state as telemetry
     * @param telemetry OpMode Telemetry object
     */
    public void show(Telemetry telemetry) {
        telemetry.addData(name, "%8.0f %7.0f %7.0f %6.0f", driveCounts, driveCountsPerSec, steerDegrees, steerDegreesPerSec);
    }
}
