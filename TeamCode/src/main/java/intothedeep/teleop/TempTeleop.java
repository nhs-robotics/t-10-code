package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import intothedeep.SnowballConfig;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import t10.bootstrap.Hardware;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.localizer.odometry.OdometryLocalizer;
import t10.motion.mecanum.MecanumDriver;
import t10.localizer.Localizer;
import t10.geometry.Pose;
import t10.vision.Webcam;

@TeleOp
public class TempTeleop extends TeleOpOpMode {
    private SnowballWithWebcamConfig c;
    private MecanumDriver driver;
    private OdometryLocalizer odometry;
    private GController gamepadController;
    private AprilTagProcessor aprilTagProcessor;
    private Localizer localizer;
    private Telemetry.Item x, y, heading;

    @Override
    public void initialize() {
        this.c = new SnowballWithWebcamConfig(this.hardwareMap);
        this.driver = this.c.createMecanumDriver();
        this.odometry = this .c.createOdometry();
        this.gamepadController = new GController(this.gamepad1).x.initialToggleState(true).ok();  // micro-movement
//        this.aprilTagProcessor = Webcam.createAprilTagProcessor(AprilTagProcessor.TagFamily.TAG_36h11, true);
        this.localizer = new Localizer(
//                new AprilTagLocalizer(this.aprilTagProcessor),
                null,
                this.odometry,
                new Pose(0, 0, 0, AngleUnit.RADIANS)
        );
        this.c.webcam.start(
                this.aprilTagProcessor
        );

        this.x = this.telemetry.addData("x ", "");
        this.y = this.telemetry.addData("y ", "");
        this.heading = this.telemetry.addData("heading ", "");
    }

    @Override
    public void loop() {
        Pose fc = this.localizer.getFieldCentricPose();
        this.x.setValue(fc.getX());
        this.y.setValue(fc.getY());
        this.heading.setValue(fc.getHeading(AngleUnit.DEGREES));

        this.telemetry.update();
        this.odometry.update();
        this.driver.useGamepad(this.gamepad1, 0.5);
    }
}
class SnowballWithWebcamConfig extends SnowballConfig
{

    public SnowballWithWebcamConfig(HardwareMap hardwareMap)
    {
        super(hardwareMap);
    }

    @Hardware(name = "Webcam")
    public Webcam webcam;
}
