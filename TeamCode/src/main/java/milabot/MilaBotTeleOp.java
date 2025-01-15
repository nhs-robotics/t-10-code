package milabot;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import intothedeep.Constants;
import t10.bootstrap.AbstractRobotConfiguration;
import t10.bootstrap.Hardware;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.localizer.odometry.OdometryLocalizer;
import t10.motion.hardware.Motor;
import t10.motion.mecanum.MecanumCoefficientMatrix;
import t10.motion.mecanum.MecanumDriver;

@TeleOp
public class MilaBotTeleOp extends TeleOpOpMode {
    private Config config;
    private MecanumDriver driver;
    private GController gamepad;

    @Override
    public void initialize() {
        this.config = new Config(this.hardwareMap);
        this.driver = this.config.createMecanumDriver();
        this.gamepad = new GController(this.gamepad1)
                .x.initialToggleState(true).ok();
    }

    @Override
    public void loop() {
        this.driver.useGamepad(this.gamepad1, this.gamepad.x.isToggled() ? 0.5 : 1);

        if (this.gamepad1.dpad_up) {
            this.config.arm1.setPower(1);
        } else if (this.gamepad1.dpad_down) {
            this.config.arm1.setPower(-1);
        } else {
            this.config.arm1.setPower(0);
        }
    }

    static class Config extends AbstractRobotConfiguration {
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

        @Hardware(
                name = "Arm1"
        )
        public Motor arm1;

        public Config(HardwareMap hardwareMap) {
            super(hardwareMap);
        }

        @Override
        public MecanumDriver createMecanumDriver() {
            return new MecanumDriver(
                    this.fl,
                    this.fr,
                    this.bl,
                    this.br,
                    new MecanumCoefficientMatrix(new double[] { -1, -1, -1, -1 })
            );
        }

        @Override
        public OdometryLocalizer createOdometry() {
            return null;
        }
    }
}
