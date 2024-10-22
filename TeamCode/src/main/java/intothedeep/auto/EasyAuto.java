package intothedeep.auto;

import t10.bootstrap.AutonomousOpMode;
import t10.novel.mecanum.MecanumDriver;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import intothedeep.Constants;
import intothedeep.IntoTheDeepRobotConfiguration;

public abstract class EasyAuto extends AutonomousOpMode {
    private IntoTheDeepRobotConfiguration config;
    private MecanumDriver driver;


    public EasyAuto() {}

    @Override
    public void initialize() {
        this.config = new IntoTheDeepRobotConfiguration(this.hardwareMap);
        this.driver = new MecanumDriver(
                this.config.fl,
                this.config.fr,
                this.config.bl,
                this.config.br,
                this.config.imu,
                Constants.Coefficients.PRODUCTION_COEFFICIENTS
        );
    }

    public void horizontalMovement(double distX, double time) {
        diagonalMovement(distX, 0, time);
    }

    public void verticalMovement(double distY, double time) {
        diagonalMovement(0, distY, time);
    }

    public void diagonalMovement(double distX, double distY, double time) {
        this.driver.setVelocity(new Vector3D(-distY / time, distX / time, 0));

        sleep(time);

        this.driver.halt();
    }

    public void rotationalMovement(double degrees, double time) {
        //Convert degrees to inches along circumference.
        //TODO: Figure out why this works. It should be off by a factor of 2.
        double headingInches = (degrees / 360L) * (2 * Math.PI * Constants.Robot.ROBOT_DIAMETER_IN);

        this.driver.setVelocity(new Vector3D(0, 0, headingInches/time));

        sleep(time);

        this.driver.halt();
    }

    //TODO: Should this be one method controlling different manipulators
    // or multiple methods for each different manipulator?
    public void manipulatorMovement(String manipulatorName, String action) {
    }
}
