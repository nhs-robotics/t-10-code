package t10.novel.odometry;

/**
 * A coefficient set used for odometry.
 */
public class OdometryCoefficientSet {
    /**
     * The default coefficient set.
     */
    public static final OdometryCoefficientSet DEFAULT = new OdometryCoefficientSet(1, 1, 1);

    /**
     * The coefficient on the right odometer.
     */
    public final double rightCoefficient;

    /**
     * The coefficient on the left odometer.
     */
    public final double leftCoefficient;

    /**
     * The coefficient on the perpendicular odometer.
     */
    public final double perpendicularCoefficient;

    /**
     * Creates a new set of odometry coefficients.
     *
     * @param rightCoefficient Coefficient for the right odometer.
     * @param leftCoefficient Coefficient for the left odometer.
     * @param perpendicularCoefficient Coefficient for the perpendicular odometer.
     */
    public OdometryCoefficientSet(double rightCoefficient, double leftCoefficient, double perpendicularCoefficient) {
        this.rightCoefficient = rightCoefficient;
        this.leftCoefficient = leftCoefficient;
        this.perpendicularCoefficient = perpendicularCoefficient;
    }
}
