package t10.novel.odometry;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import t10.novel.NovelEncoder;
import t10.reconstructor.Pose;

/**
 * Odometry localization interface.
 */

/**
 * NOTE: right is positive x, forward is positive y, clockwise rotation is positive phi
 * "(0,0,0)" is at the starting position, with the starting heading
 * */
public class NovelOdometry {
    private final OdometryCoefficientSet coefficients;
    private final NovelEncoder rightEncoder;
    private final NovelEncoder leftEncoder;
    private final NovelEncoder perpendicularEncoder;
    private final double lateralWheelDistance;
    private final double perpendicularWheelOffset;
    private double leftWheelPos;
    private double rightWheelPos;
    private double perpendicularWheelPos;
    private Pose relativePose;

    /**
     * Creates a NovelOdometry instance.
     * See diagram: <a href="http://web.archive.org/web/20230529000105if_/https://gm0.org/en/latest/_images/offsets-and-trackwidth.png">here (archived)</a> or <a href="https://gm0.org/en/latest/_images/offsets-and-trackwidth.png">here</a>.
     *
     * @param coefficients The coefficients to use for the odometers. Chances are this is {@link OdometryCoefficientSet#DEFAULT}.
     * @param rightEncoder The right side encoder.
     * @param leftEncoder The left side encoder.
     * @param perpendicularEncoder The perpendicular encoder.
     * @param lateralWheelDistance The distance between the lateral wheels.
     * @param perpendicularWheelOffset The offset of the perpendicular wheel from the center of the robot chassis.
     */
    public NovelOdometry(
            OdometryCoefficientSet coefficients,
            NovelEncoder rightEncoder,
            NovelEncoder leftEncoder,
            NovelEncoder perpendicularEncoder,
            double lateralWheelDistance,
            double perpendicularWheelOffset
    ) {
        this.coefficients = coefficients;
        this.rightEncoder = rightEncoder;
        this.leftEncoder = leftEncoder;
        this.perpendicularEncoder = perpendicularEncoder;
        this.lateralWheelDistance = lateralWheelDistance;
        this.perpendicularWheelOffset = perpendicularWheelOffset;

        this.setRelativePose(new Pose(0, 0, 0, AngleUnit.RADIANS));
    }

    /**
     * Updates the underlying state of this odometry localizer.
     * Calling this method more regularly increases accuracy of the calculations, but obviously it is impossible to call this method infinitely fast, so calculations will contain some error.
     * <p>
     * Adapted from <a href="https://gm0.org/en/latest/docs/software/concepts/odometry.html">gm0</a>.
     */
    public void update() {
        // Get new wheel positions
        double newLeftWheelPos = this.leftEncoder.getCurrentInches();
        double newRightWheelPos = this.rightEncoder.getCurrentInches();
        double newPerpendicularWheelPos = this.perpendicularEncoder.getCurrentInches();

        // Get changes in odometer wheel positions since last update
        double deltaLeftWheelPos = this.coefficients.leftCoefficient * (newLeftWheelPos - this.leftWheelPos);
        double deltaRightWheelPos = this.coefficients.rightCoefficient * (newRightWheelPos - this.rightWheelPos); // Manual adjustment for inverted odometry wheel
        double deltaPerpendicularWheelPos = this.coefficients.perpendicularCoefficient * (newPerpendicularWheelPos - this.perpendicularWheelPos);

        double phi = (deltaLeftWheelPos - deltaRightWheelPos) / this.lateralWheelDistance;
        double deltaX_relative = (deltaLeftWheelPos + deltaRightWheelPos) / 2d;
        double deltaY_relative= deltaPerpendicularWheelPos - this.perpendicularWheelOffset * phi;

        // Heading of movement is assumed average between last known and current rotation
        //                    CURRENT ROTATION                                             LAST SAVED ROTATION       
        // double currentRotation = phi + this.relativePose.getHeading(AngleUnit.RADIANS);
        // double lastRotation = this.relativePose.getHeading(AngleUnit.RADIANS);
        // double averageRotationOverObservationPeriod = (currentRotation + lastRotation) / 2;
        double heading = phi + this.relativePose.getHeading(AngleUnit.RADIANS);
        double deltaX = deltaX_relative * Math.sin(-heading) + deltaY_relative * Math.cos(-heading);
        double deltaY = deltaX_relative * Math.cos(-heading) - deltaY_relative * Math.sin(-heading);

        this.relativePose = this.relativePose.add(new Pose(deltaX, deltaY, phi, AngleUnit.RADIANS));

        // Update encoder wheel position
        this.leftWheelPos = newLeftWheelPos;
        this.rightWheelPos = newRightWheelPos;
        this.perpendicularWheelPos = newPerpendicularWheelPos;
    }

    /**
     * @return The current relative pose of the robot based on the odometers.
     */
    public Pose getRelativePose() {
        return this.relativePose;
    }

    /**
     * Sets the relative pose to a new pose. Helps to combat error as a result of {@link NovelOdometry#update()} not being called infinitely fast.
     *
     * @param pose New relative pose to base odometry calculations off of.
     */
    public void setRelativePose(Pose pose) {
        this.relativePose = pose;
        this.leftWheelPos = this.leftEncoder.getCurrentInches();
        this.rightWheelPos = this.rightEncoder.getCurrentInches();
        this.perpendicularWheelPos = this.perpendicularEncoder.getCurrentInches();
    }
}
