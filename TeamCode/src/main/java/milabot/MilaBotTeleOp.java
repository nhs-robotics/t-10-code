package milabot;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import intothedeep.Constants;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import t10.bootstrap.AbstractRobotConfiguration;
import t10.bootstrap.Hardware;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.localizer.odometry.OdometryLocalizer;
import t10.motion.hardware.Motor;
import t10.motion.mecanum.MecanumCoefficientMatrix;
import t10.motion.mecanum.MecanumDriver;
import t10.utils.MathUtils;

@TeleOp
public class MilaBotTeleOp extends TeleOpOpMode {
    public static final int POSITION_BIG_ARM_MAX = 1970;
    public static final int POSITION_SMALL_ARM_MAX = 210;
    public static final int POSITION_SMALL_ARM_TOP_MAX = 310;
    private Config config;
    private MecanumDriver driver;
    private GController gamepad;
    private Telemetry.Item smallArm, bigArm;
    private boolean isBigArmExtended;
    private boolean isSmallArmExtended;

    @Override
    public void initialize() {
        this.config = new Config(this.hardwareMap);
        this.driver = this.config.createMecanumDriver();
        this.gamepad = new GController(this.gamepad1)
                .leftTrigger.whileDown(proportion -> this.config.spintake.setPower(1)).onRelease(() -> this.config.spintake.setPower(0)).ok()
                .rightTrigger.whileDown(proportion -> this.config.spintake.setPower(-1)).onRelease(() -> this.config.spintake.setPower(0)).ok()
                .rightBumper.initialToggleState(false).ok()
                .a.onPress(() -> {
                    this.isSmallArmExtended = true;
                }).ok()
                .y.onPress(() -> {
                    this.isSmallArmExtended = false;
                }).ok()
                .x.onPress(() -> {
                    this.isSmallArmExtended = false;
                    this.isBigArmExtended = false;
                }).ok()
                .b.onPress(() -> {
                    this.isSmallArmExtended = true;
                    this.isBigArmExtended = true;
                }).ok();
        this.smallArm = this.telemetry.addData("smallArm ", 0);
        this.bigArm = this.telemetry.addData("bigArm ", 0);
        this.isBigArmExtended = false;
        this.isSmallArmExtended = false;
    }

    @Override
    public void loop() {
        this.driver.useGamepad(this.gamepad1, this.gamepad.rightBumper.isToggled() ? 0.5 : 1);
        this.smallArm.setValue(this.config.smallArm.motor.getCurrentPosition());
        this.bigArm.setValue(this.config.bigArm.motor.getCurrentPosition());
        this.gamepad.update();
        this.telemetry.update();

        if (this.isBigArmExtended) {
            setBigArmPowerExtension();
            setSmallArmPowerFullExtension();
        } else {
            setBigArmPowerRetraction();

            if (this.isSmallArmExtended) {
                setSmallArmPowerExtension();
            } else {
                setSmallArmPowerRetraction();
            }
        }
    }

    private void setBigArmPowerExtension() {
        double percentExtended = this.config.bigArm.motor.getCurrentPosition() / (double) POSITION_BIG_ARM_MAX;
        double power;

        if (percentExtended > 0.99) {
            power = 0;
        } else if (percentExtended > 0.9) {
            power = 0.1;
        } else if (percentExtended > 0.5) {
            power = 0.4;
        } else {
            power = 1;
        }

        if (!MathUtils.epsilonEquals(this.config.bigArm.motor.getPower(), power)) {
            this.config.bigArm.motor.setPower(power);
        }
    }

    private void setBigArmPowerRetraction() {
        double percentExtended = this.config.bigArm.motor.getCurrentPosition() / (double) POSITION_BIG_ARM_MAX;
        double power;

        if (percentExtended > 0.9) {
            power = -1;
        } else if (percentExtended > 0.25) {
            power = -0.35;
        } else {
            power = 0;
        }
        
        if (!MathUtils.epsilonEquals(this.config.bigArm.motor.getPower(), power)) {
            this.config.bigArm.motor.setPower(power);
        }
    }

    private void setSmallArmPowerExtension() {
        double percentExtended = this.config.smallArm.motor.getCurrentPosition() / (double) POSITION_SMALL_ARM_MAX;
        double power;

        if (percentExtended > 0.9) {
            power = 0;
        } else {
            power = 1;
        }

        if (!MathUtils.epsilonEquals(this.config.smallArm.motor.getPower(), power)) {
            this.config.smallArm.motor.setPower(power);
        }
    }

    private void setSmallArmPowerFullExtension() {
        double percentExtended = this.config.smallArm.motor.getCurrentPosition() / (double) POSITION_SMALL_ARM_TOP_MAX;
        double power;

        if (percentExtended > 0.9) {
            power = 0;
        } else {
            power = 1;
        }

        if (!MathUtils.epsilonEquals(this.config.smallArm.motor.getPower(), power)) {
            this.config.smallArm.motor.setPower(power);
        }
    }

    private void setSmallArmPowerRetraction() {
        double percentExtended = this.config.smallArm.motor.getCurrentPosition() / (double) POSITION_SMALL_ARM_MAX;
        double power;

        if (percentExtended > 0.97) {
            power = -1;
        } else if (percentExtended > 0.1) {
            power = -0.7;
        } else {
            power = 0;
        }

        if (!MathUtils.epsilonEquals(this.config.smallArm.motor.getPower(), power)) {
            this.config.smallArm.motor.setPower(power);
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
                name = "Bigarm",
                zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        )
        public Motor bigArm;

        @Hardware(
                name = "Smallarm",
                zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        )
        public Motor smallArm;

        @Hardware(
                name = "Spintake"
        )
        public CRServo spintake;

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
                    new MecanumCoefficientMatrix(new double[]{1, 1, 1, 1})
            );
        }

        @Override
        public OdometryLocalizer createOdometry() {
            return null;
        }
    }
}
