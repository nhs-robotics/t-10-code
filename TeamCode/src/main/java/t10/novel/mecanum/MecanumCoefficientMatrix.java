package t10.novel.mecanum;

import t10.utils.MovementVector;

/**
 * Coefficient matrix for the total coefficients, vertical driving coefficients, horizontal driving coefficients, and the rotational coefficients.
 * <p>
 * Omni driving coefficients are used for corrections that are easier to make on the software side than hardware side:
 * <ul>
 *     <li>if hardware sets motors up backward</li>
 *     <li>to adjust motor power</li>
 *     <li>to correct drift and/or uneven motor power</li>
 *     <li>to allow for omni driving</li>
 * </ul>
 */
public class MecanumCoefficientMatrix {
    /**
     * The default coefficient matrix.
     */
    public static MecanumCoefficientMatrix DEFAULT = new MecanumCoefficientMatrix(new double[] {1, 1, 1, 1});

    /**
     * The total coefficient set that is primarily used to adjust coefficients.
     */
    public final MecanumCoefficientSet totals;

    /**
     * The default vertical coefficient set.
     */
    public final MecanumCoefficientSet vertical = new MecanumCoefficientSet(1, 1, 1, 1);

    /**
     * The default horizontal coefficient set.
     */
    public final MecanumCoefficientSet horizontal = new MecanumCoefficientSet(-1, 1, 1, -1);

    /**
     * The default rotational coefficient set.
     */
    public final MecanumCoefficientSet rotational = new MecanumCoefficientSet(-1, 1, -1, 1);

    /**
     * Creates a new coefficient matrix.
     *
     * @param configuredCoefficients Coefficients applied after the vertical, horizontal, or rotational coefficients are applied.
     */
    public MecanumCoefficientMatrix(double[] configuredCoefficients) {
        this.totals = new MecanumCoefficientSet(configuredCoefficients);
    }

    /**
     * Calculates the motor coefficients that are then passed to the motors.
     *
     * @param horizontalPower -1 is left full speed, 1 is right full speed.
     * @param verticalPower   -1 is backward full speed, 1 is forward full speed.
     * @param rotationalPower -1 is counterclockwise full speed, 1 is clockwise full speed.
     * @return The coefficients that have been multiplied by the motor powers.
     */
    public MecanumCoefficientSet calculateCoefficientsWithPower(double verticalPower, double horizontalPower, double rotationalPower) {
        // Notice how this is just matrix multiplication.
        return new MecanumCoefficientSet(
                totals.frontLeft * (verticalPower * vertical.frontLeft + horizontalPower * horizontal.frontLeft + rotationalPower * rotational.frontLeft),
                totals.frontRight * (verticalPower * vertical.frontRight + horizontalPower * horizontal.frontRight + rotationalPower * rotational.frontRight),
                totals.backLeft * (verticalPower * vertical.backLeft + horizontalPower * horizontal.backLeft + rotationalPower * rotational.backLeft),
                totals.backRight * (verticalPower * vertical.backRight + horizontalPower * horizontal.backRight + rotationalPower * rotational.backRight)
        );
    }

    public MovementVector calculatePowerWithCoefficients(MecanumCoefficientSet coefficientSet) {
        double horizontalPower = 0;
        double verticalPower = 0;
        double rotationalPower = 0;

        // Assuming inverse relationships for coefficient calculation:
        horizontalPower += coefficientSet.frontLeft / totals.frontLeft / horizontal.frontLeft;
        horizontalPower += coefficientSet.frontRight / totals.frontRight / horizontal.frontRight;
        horizontalPower += coefficientSet.backLeft / totals.backLeft / horizontal.backLeft;
        horizontalPower += coefficientSet.backRight / totals.backRight / horizontal.backRight;
        horizontalPower /= 4d;

        verticalPower += coefficientSet.frontLeft / totals.frontLeft / vertical.frontLeft;
        verticalPower += coefficientSet.frontRight / totals.frontRight / vertical.frontRight;
        verticalPower += coefficientSet.backLeft / totals.backLeft / vertical.backLeft;
        verticalPower += coefficientSet.backRight / totals.backRight / vertical.backRight;
        verticalPower /= 4d;

        rotationalPower += coefficientSet.frontLeft / totals.frontLeft / rotational.frontLeft;
        rotationalPower += coefficientSet.frontRight / totals.frontRight / rotational.frontRight;
        rotationalPower += coefficientSet.backLeft / totals.backLeft / rotational.backLeft;
        rotationalPower += coefficientSet.backRight / totals.backRight / rotational.backRight;
        rotationalPower /= 4d;

        return new MovementVector(horizontalPower, verticalPower, rotationalPower);
    }
}
