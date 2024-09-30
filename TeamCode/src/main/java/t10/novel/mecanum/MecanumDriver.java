package t10.novel.mecanum;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import t10.novel.NovelMotor;

/**
 * Wrapper class for driving a mecanum robot.
 */
public class MecanumDriver {
    private final IMU imu;
    private final NovelMotor frontLeft;
    private final NovelMotor frontRight;
    private final NovelMotor backLeft;
    private final NovelMotor backRight;
    private final MecanumCoefficientMatrix omniDriveCoefficients;

    public MecanumDriver(
            NovelMotor frontLeft,
            NovelMotor frontRight,
            NovelMotor backLeft,
            NovelMotor backRight,
            IMU imu,
            MecanumCoefficientMatrix omniDriveCoefficients
    ) {
        this.imu = imu;
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;
        this.omniDriveCoefficients = omniDriveCoefficients;
    }

    public void setVelocity(Vector3D velocity) {
        MecanumCoefficientSet coefficientSet = this.omniDriveCoefficients.calculateCoefficientsWithPower(
                velocity.getX(),
                velocity.getY(),
                velocity.getZ()
        );

        this.setVelocity(
                coefficientSet.frontLeft,
                coefficientSet.frontRight,
                coefficientSet.backLeft,
                coefficientSet.backRight
        );
    }

    public void setVelocity(double frontLeft, double frontRight, double backLeft, double backRight) {
        this.frontLeft.setVelocity(frontLeft);
        this.frontRight.setVelocity(frontRight);
        this.backLeft.setVelocity(backLeft);
        this.backRight.setVelocity(backRight);
    }

    public void halt() {
        this.setVelocity(0, 0, 0, 0);
    }

    /**
     * Call this method in loop in tele-op to allow the gamepad to be used for driving.
     *
     * @param gamepad The gamepad that controls the robot's movement.
     * @param powerMultipler Used for micro-movement, set to 0.5 for micro-movement.
     */
    public void useGamepad(Gamepad gamepad, double powerMultipler) {
        MecanumCoefficientSet coefficientSet = this.omniDriveCoefficients.calculateCoefficientsWithPower(
                gamepad.left_stick_y,
                gamepad.left_stick_x,
                gamepad.right_stick_x
        );

        this.frontLeft.motor.setPower(coefficientSet.frontLeft * powerMultipler);
        this.frontRight.motor.setPower(coefficientSet.frontRight * powerMultipler);
        this.backLeft.motor.setPower(coefficientSet.backLeft * powerMultipler);
        this.backRight.motor.setPower(coefficientSet.backRight * powerMultipler);
    }
}
