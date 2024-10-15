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
    public void lateralMovement(double distX, double distY, double time) {
        this.driver.setVelocity(new Vector3D(-distY / time, distX / time, 0));

        sleep(time);

        this.driver.halt();
    }

    public void rotationalMovement(double degrees, double time) {
        this.driver.setVelocity(new Vector3D(0, 0, degrees/time));

        sleep(time);

        this.driver.halt();
    }

    public void manipulatorMovement(String type) {
    }
}
