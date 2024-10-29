package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import intothedeep.KevinRobotConfiguration;
import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.novel.mecanum.MecanumDriver;
import t10.novel.odometry.NovelOdometry;
import t10.novel.odometry.OdometryNavigation;
import t10.reconstructor.Pose;
import t10.utils.MovementVector;

@TeleOp
public class OdometryTestingTeleOp extends TeleOpOpMode {
    private MecanumDriver driver;
    private GController gamepadController;
    private KevinRobotConfiguration c;
    private NovelOdometry odometry;
    private OdometryNavigation navigator;
    private Telemetry.Item x;
    private Telemetry.Item y;
    private Telemetry.Item r;
    private Telemetry.Item direct90, direct180, direct270, direct0, direct45, direct225;
    private Telemetry.Item angle;
    private double distance;
    private Pose init_pose;
    MovementVector vector;

    @Override
    public void initialize() {
        this.c = new KevinRobotConfiguration(this.hardwareMap);
        this.driver = this.c.createMecanumDriver();
        this.gamepadController = new GController(this.gamepad1)
                .x.initialToggleState(false).ok();  // micro-movement
        this.gamepadController = new GController(this.gamepad1)
                .a.initialToggleState(false).ok();
        this.gamepadController = new GController(this.gamepad1)
                .b.initialToggleState(false).ok();

        this.odometry = c.createOdometry();
        this.navigator = new OdometryNavigation(odometry, driver);

        //this.telemetry.setNumDecimalPlaces(0, 4);
        this.x = this.telemetry.addData("x_novel: ", "0");
        this.y = this.telemetry.addData("y_novel: ", "0");
        this.r = this.telemetry.addData("r_novel: ", "0");
        this.direct0 = this.telemetry.addData("direction_0: ",0);
        this.direct90 = this.telemetry.addData("direction_90: ",0);
        this.direct180 = this.telemetry.addData("direction_180: ",0);
        this.direct270 = this.telemetry.addData("direction_-90: ",0);
        this.direct45 = this.telemetry.addData("direction_45: ",0);
        this.direct225 = this.telemetry.addData("direction_-45: ",0);

        distance = 20;
        init_pose = odometry.getRelativePose();
/*
        this.leftWheel = this.telemetry.addData("Left Wheel: ", "0");
        this.rightWheel = this.telemetry.addData("Right Wheel: ", "0");
        this.perpWheel = this.telemetry.addData("Perp Wheel: ", "0");

 */
    }

    @Override
    public void loop() {
        this.gamepadController.update();
        //vector = navigator.calcTrigVelocity(init_pose,odometry.getRelativePose());
        //this.v_2.setValue(vector);
        //vector = new MovementVector(-vector.getVertical(), vector.getHorizontal(), vector.getRotation());
        //this.driver.useGamepad(this.gamepad1, this.gamepadController.x.isToggled() ? 4 : 1);
        this.x.setValue(this.odometry.getRelativePose().getX());
        this.y.setValue(this.odometry.getRelativePose().getY());
        this.r.setValue(this.odometry.getRelativePose().getHeading(AngleUnit.DEGREES));
        this.direct0.setValue(navigator.findTurnSpeed(odometry.getRelativePose().getHeading(AngleUnit.DEGREES),0));
        this.direct45.setValue(navigator.findTurnSpeed(odometry.getRelativePose().getHeading(AngleUnit.DEGREES),45));
        this.direct90.setValue(navigator.findTurnSpeed(odometry.getRelativePose().getHeading(AngleUnit.DEGREES),90));
        this.direct180.setValue(navigator.findTurnSpeed(odometry.getRelativePose().getHeading(AngleUnit.DEGREES),180));
        this.direct225.setValue(navigator.findTurnSpeed(odometry.getRelativePose().getHeading(AngleUnit.DEGREES),-45));
        this.direct270.setValue(navigator.findTurnSpeed(odometry.getRelativePose().getHeading(AngleUnit.DEGREES),-90));

        if (gamepadController.x.isToggled()) {
            driveHorizontal(-20);
        } else if (gamepadController.a.isToggled()) {
            driveLateral(-20);
        } else if (gamepadController.y.isToggled()) {
            driveLateral(20);
        } else if (gamepadController.b.isToggled()) {
            driveHorizontal(20);
        }
        if (gamepadController.dpadUp.isToggled()) {
            turnAbsolute(0);
        } else if (gamepadController.dpadRight.isToggled()) {
            turnAbsolute(90);
        } else if (gamepadController.dpadDown.isToggled()) {
            turnAbsolute(180);
        } else if (gamepadController.dpadLeft.isToggled()) {
            turnAbsolute(-90);
        } else if (gamepadController.rightBumper.isToggled()) {
            turnAbsolute(45);
        } else if (gamepadController.leftBumper.isToggled()) {
            turnAbsolute(-45);
        } else {
            driver.setVelocity(new MovementVector(0,0,0));
        }
        gamepadController.rightTrigger.whileDown((amplitude) -> driver.setVelocity(new MovementVector(0, 0, 10*amplitude)));
        gamepadController.leftTrigger.whileDown((amplitude) -> driver.setVelocity(new MovementVector(0, 0, -10*amplitude)));
        this.telemetry.update();
        this.odometry.update();
    }



    public void driveLateral(double distance)
    {
        double initialX = odometry.getRelativePose().getX();
        double finalY = odometry.getRelativePose().getY();
        while(Math.abs(finalY - odometry.getRelativePose().getY()) > navigator.minError) {
            driver.setVelocity(odometry.getRelativeVelocity(new MovementVector(-10 * Math.signum(distance), initialX - odometry.getRelativePose().getX(),0)));
            this.x.setValue(this.odometry.getRelativePose().getX());
            this.y.setValue(this.odometry.getRelativePose().getY());
            this.r.setValue(this.odometry.getRelativePose().getHeading(AngleUnit.DEGREES));
            this.telemetry.update();
            this.odometry.update();
        }
        driver.setVelocity(new MovementVector(0,0,0));
    }

    public void driveHorizontal(double distance)
    {
        double initialY = odometry.getRelativePose().getY();
        double initialX = odometry.getRelativePose().getX();
        double finalX = initialX + distance;
        while(Math.abs(finalX - odometry.getRelativePose().getX()) > navigator.minError) {
            driver.setVelocity(odometry.getRelativeVelocity(new MovementVector(initialY - odometry.getRelativePose().getY(), 10 * Math.signum(distance),0)));
            this.x.setValue(this.odometry.getRelativePose().getX());
            this.y.setValue(this.odometry.getRelativePose().getY());
            this.r.setValue(this.odometry.getRelativePose().getHeading(AngleUnit.DEGREES));
            this.telemetry.update();
            this.odometry.update();
        }
        driver.setVelocity(new MovementVector(0,0,0));
    }

    public void turnAbsolute(double angle)
    {
        while(navigator.needAngleCorrectionDegrees(odometry.getRelativePose().getHeading(AngleUnit.DEGREES), angle))
        {
            driver.setVelocity(new MovementVector(0,0,navigator.findTurnSpeed(odometry.getRelativePose().getHeading(AngleUnit.DEGREES), angle)));
            this.x.setValue(this.odometry.getRelativePose().getX());
            this.y.setValue(this.odometry.getRelativePose().getY());
            this.r.setValue(this.odometry.getRelativePose().getHeading(AngleUnit.DEGREES));
            this.telemetry.update();
            this.odometry.update();
        }
        driver.setVelocity(new MovementVector(0,0,0));
    }

    public void turnRelative(double angle)
    {
        if(Math.abs(angle) > 180) {}
        else {
            double initialAngle = odometry.getRelativePose().getHeading(AngleUnit.DEGREES);
            double targetAngle = initialAngle + angle;
            if(targetAngle > 180) {
                targetAngle -= 360;
                while(odometry.getRelativePose().getHeading(AngleUnit.DEGREES) >= initialAngle - navigator.minAngleError || odometry.getRelativePose().getHeading(AngleUnit.DEGREES) < targetAngle)
                {
                    driver.setVelocity(new MovementVector(0,0,navigator.maxAngVelocity * Math.signum(angle)));
                    this.x.setValue(this.odometry.getRelativePose().getX());
                    this.y.setValue(this.odometry.getRelativePose().getY());
                    this.r.setValue(this.odometry.getRelativePose().getHeading(AngleUnit.DEGREES));
                    this.telemetry.update();
                    this.odometry.update();
                }
            }
            else if (targetAngle < 180) {
                targetAngle += 360;
                while(odometry.getRelativePose().getHeading(AngleUnit.DEGREES) <= initialAngle + navigator.minAngleError || odometry.getRelativePose().getHeading(AngleUnit.DEGREES) > targetAngle)
                {
                    driver.setVelocity(new MovementVector(0,0,navigator.maxAngVelocity * Math.signum(angle)));
                    this.x.setValue(this.odometry.getRelativePose().getX());
                    this.y.setValue(this.odometry.getRelativePose().getY());
                    this.r.setValue(this.odometry.getRelativePose().getHeading(AngleUnit.DEGREES));
                    this.telemetry.update();
                    this.odometry.update();
                }
            }
            else {
                while(Math.abs(odometry.getRelativePose().getHeading(AngleUnit.DEGREES) - targetAngle) > navigator.minAngleError)
                {
                    driver.setVelocity(new MovementVector(0,0,navigator.maxAngVelocity * Math.signum(angle)));
                    this.x.setValue(this.odometry.getRelativePose().getX());
                    this.y.setValue(this.odometry.getRelativePose().getY());
                    this.r.setValue(this.odometry.getRelativePose().getHeading(AngleUnit.DEGREES));
                    this.telemetry.update();
                    this.odometry.update();
                }
            }
        }
    }
    /*
    public void turn(double angle)
    {
        double target = odometry.getRelativePose().getHeading(AngleUnit.DEGREES) + angle;
        if(target > 180) { target -= 360;}
        else if (target < -180) {target += 360;}
        double direction = Math.signum(angle);
        if(direction == -1)
        {
            while(angle > )
        }
        driver.setVelocity(new MovementVector(0,0,direction * 5));
    }


    private void driveRight() {
        if (Math.abs(distance - odometry.getRelativePose().getX()) > 2) {
            driver.setVelocity(new MovementVector(0, 10, 0));
        } else {
            driver.setVelocity(new MovementVector(0, 0, 0));
        }
    }

    private void driveForward() {
        if (Math.abs(distance - odometry.getRelativePose().getY()) > 2) {
            driver.setVelocity(new MovementVector(-10, 0, 0));
        } else {
            driver.setVelocity(new MovementVector(0, 0, 0));
        }
    }

    private void turnRight(double angle) {
        if (Math.abs(angle - odometry.getRelativePose().getNegativeHeading(AngleUnit.DEGREES)) > 5) {
            driver.setVelocity(new MovementVector(0, 0, 5));
        } else {
            driver.setVelocity(new MovementVector(0, 0, 0));
        }
    }
    private void turnLeft(double angle) {
        if (Math.abs(angle - odometry.getRelativePose().getNegativeHeading(AngleUnit.DEGREES)) > 5) {
            driver.setVelocity(new MovementVector(0, 0, -5));
        } else {
            driver.setVelocity(new MovementVector(0, 0, 0));
        }
    }

    private void driveForwardAbsolute(double deltaY) {
        if (Math.abs(deltaY - odometry.getRelativePose().getY()) > 2) {
            driver.setVelocity(odometry.getRelativeVelocity(new MovementVector(-10, 0, 0)));
        } else {
            driver.setVelocity(new MovementVector(0, 0, 0));
        }
    }
    private void driveRightAbsolute(double deltaX) {
        if (Math.abs(deltaX - odometry.getRelativePose().getX()) > 2) {
            driver.setVelocity(odometry.getRelativeVelocity(new MovementVector(0, 10, 0)));
        } else {
            driver.setVelocity(new MovementVector(0, 0, 0));
        }
    }

    */
}