package t10.novel.mecanum;

/**
 * Coefficient set for omni driving. Includes each mecanum wheel.
 */
public class MecanumCoefficientSet {
    /**
     * Coefficient for the front left motor.
     */
    public final double frontLeft;

    /**
     * Coefficient for the front right motor.
     */
    public final double frontRight;

    /**
     * Coefficient for the back left motor.
     */
    public final double backLeft;

    /**
     * Coefficient for the back right motor.
     */
    public final double backRight;

    /**
     * Coefficients for each respective motor.
     * Allowed value domain: [-1, 1]
     *
     * @param frontLeft  Coefficient for front left motor.
     * @param frontRight Coefficient for front right motor.
     * @param backLeft   Coefficient for back left motor.
     * @param backRight  Coefficient for back right  motor.
     */
    public MecanumCoefficientSet(double frontLeft, double frontRight, double backLeft, double backRight) {
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;
    }

    /**
     * Coefficients for each respective motor.
     *
     * @param coefficients Coefficients in order of motors: front left, front right, back left, and back right.
     */
    public MecanumCoefficientSet(double[] coefficients) {
        this(coefficients[0], coefficients[1], coefficients[2], coefficients[3]);
    }
}