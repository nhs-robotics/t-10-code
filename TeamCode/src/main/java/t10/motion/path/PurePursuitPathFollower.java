package t10.motion.path;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import t10.geometry.MovementVector;
import t10.geometry.Point;
import t10.geometry.Pose;
import t10.localizer.Localizer;
import t10.motion.mecanum.MecanumDriver;
import t10.motion.profile.TrapezoidalMotionProfile;
import t10.utils.MathUtils;

import java.util.Arrays;
import java.util.LinkedList;

import static t10.utils.MathUtils.isPointOnLine;

public class PurePursuitPathFollower {
    private static final double FOLLOWER_STOP_DISTANCE = 0.5;
    public final Point[] path;
    public final Localizer localizer;
    public final double lookaheadDistance;
    private final double speed;
    private double lastAngle;

    public PurePursuitPathFollower(Point[] path, Localizer localizer, double lookaheadDistance, double speed) {
        if (path.length < 2) {
            throw new IllegalArgumentException("path must contain at least two points (a start and an end)");
        }

        this.path = path;
        this.localizer = localizer;
        this.lookaheadDistance = lookaheadDistance;
        this.speed = speed;
    }

    /**
     * This method makes the follow the path. It must be called as frequently as possible when following the path.
     *
     * @param mecanumDriver The driver controller.
     * @return True if the path has been fully followed, false if following is still in progress.
     */
    public boolean follow(MecanumDriver mecanumDriver) throws IllegalStateException {
        Pose currentPose = this.localizer.getFieldCentricPose();
        Point lookaheadPoint = getLookaheadPoint(
                currentPose,
                this.lookaheadDistance
        );

        if (lookaheadPoint == null) throw new IllegalStateException("No Lookahead Point Found");

        if (currentPose.distanceTo(lookaheadPoint) < FOLLOWER_STOP_DISTANCE) {
            mecanumDriver.halt();
            return true;
        }

        moveTowardsPosition(
                mecanumDriver,
                currentPose,
                lookaheadPoint
        );

        return false;
    }

    private double getTraveledDistance(Point targetPoint) {
        double distanceTraveled = 0;

        for (int i = 0; this.path.length - 1 > i; i++) {
            Point p1 = this.path[i];
            Point p2 = this.path[i + 1];

            if (isPointOnLine(p1, p2, targetPoint)) {
                distanceTraveled += p1.distanceTo(this.localizer.getFieldCentricPose());
                break;
            } else {
                distanceTraveled += p1.distanceTo(p2);
            }
        }

        return distanceTraveled;
    }

    private void moveTowardsPosition(MecanumDriver mecanumDriver, Point currentPoint, Point targetPoint) {
        double dx = targetPoint.getX() - currentPoint.getX();
        double dy = targetPoint.getY() - currentPoint.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            // Get the motion profile's velocity
            double angle = Math.atan2(dy, dx);
            double angleDifference = MathUtils.angleDifference(angle, this.lastAngle, AngleUnit.RADIANS);
            double velocity = Math.max(
                    this.speed / 10,
                    this.speed * (0.75 - angleDifference) * (currentPoint.distanceTo(targetPoint) / this.lookaheadDistance)
            );

            mecanumDriver.setVelocity(
                    new MovementVector(
                            Math.sin(angle) * velocity,
                            Math.cos(angle) * velocity,
                            0
                    )
            );

            this.lastAngle = angle;
        }
    }

    private double signumWithSpecialCase(double n) {
        return (n == 0) ? 1 : Math.signum(n);
    }

    /**
     * Taken from <a href="https://github.com/xiaoxiae/PurePursuitAlgorithm/blob/master/src/main/PurePursuit.java">here</a>, and adapted to work with T-10's codebase.
     * Generates the furthest lookahead point on the path that is distance r from the point (x, y).
     *
     * @param point The point of the origin of the circle. This should be the absolute position of the robot.
     * @param r The lookahead distance.
     * @return A double[] coordinate pair if the lookahead point exists, or null.
     * @see <a href="http://mathworld.wolfram.com/Circle-LineIntersection.html">Circle-Line Intersection</a>
     */
    public Point getLookaheadPoint(Point point, double r) {
        Point lookahead = null;
        double x = point.getX();
        double y = point.getY();

        // iterate through all pairs of points
        for (int i = 0; i < path.length - 1; i++) {
            // form a segment from each two adjacent points
            Point segmentStart = path[i];
            Point segmentEnd = path[i + 1];

            // translate the segment to the origin
            double[] p1 = new double[]{segmentStart.getX() - x, segmentStart.getY() - y};
            double[] p2 = new double[]{segmentEnd.getX() - x, segmentEnd.getY() - y};

            // calculate an intersection of a segment and a circle with radius r (lookahead) and origin (0, 0)
            double dx = p2[0] - p1[0];
            double dy = p2[1] - p1[1];
            double d = Math.sqrt(dx * dx + dy * dy);
            double D = p1[0] * p2[1] - p2[0] * p1[1];

            // if the discriminant is zero or the points are equal, there is no intersection
            double discriminant = r * r * d * d - D * D;
            if (discriminant < 0 || Arrays.equals(p1, p2)) continue;

            // the x components of the intersecting points
            double x1 = (D * dy + signumWithSpecialCase(dy) * dx * Math.sqrt(discriminant)) / (d * d);
            double x2 = (D * dy - signumWithSpecialCase(dy) * dx * Math.sqrt(discriminant)) / (d * d);

            // the y components of the intersecting points
            double y1 = (-D * dx + Math.abs(dy) * Math.sqrt(discriminant)) / (d * d);
            double y2 = (-D * dx - Math.abs(dy) * Math.sqrt(discriminant)) / (d * d);

            // whether each of the intersections are within the segment (and not the entire line)
            boolean validIntersection1 = Math.min(p1[0], p2[0]) < x1 && x1 < Math.max(p1[0], p2[0])
                    || Math.min(p1[1], p2[1]) < y1 && y1 < Math.max(p1[1], p2[1]);
            boolean validIntersection2 = Math.min(p1[0], p2[0]) < x2 && x2 < Math.max(p1[0], p2[0])
                    || Math.min(p1[1], p2[1]) < y2 && y2 < Math.max(p1[1], p2[1]);

            // remove the old lookahead if either of the points will be selected as the lookahead
            if (validIntersection1 || validIntersection2) lookahead = null;

            // select the first one if it's valid
            if (validIntersection1) {
                lookahead = new Point(x1 + x, y1 + y);
            }

            // select the second one if it's valid and either lookahead is none,
            // or it's closer to the end of the segment than the first intersection
            if (validIntersection2) {
                if (lookahead == null || Math.abs(x1 - p2[0]) > Math.abs(x2 - p2[0]) || Math.abs(y1 - p2[1]) > Math.abs(y2 - p2[1])) {
                    lookahead = new Point(x2 + x, y2 + y);
                }
            }
        }

        // special case for the very last point on the path
        Point lastPoint = path[path.length - 1];

        double endX = lastPoint.getX();
        double endY = lastPoint.getY();

        // if we are closer than lookahead distance to the end, set it as the lookahead
        if (Math.sqrt((endX - x) * (endX - x) + (endY - y) * (endY - y)) <= r) {
            return new Point(endX, endY);
        }

        return lookahead;
    }

    public static class Builder {
        private final LinkedList<Point> path;
        private double lookaheadDistance;
        private Localizer localizer;
        private double speed;

        public Builder() {
            this.path = new LinkedList<>();
        }

        public Builder addPoint(Point point) {
            this.path.add(point);
            return this;
        }

        public Builder addPoint(double x, double y) {
            return this.addPoint(new Point(x, y));
        }

        public Builder removePoint(Point point) {
            this.path.remove(point);
            return this;
        }

        public Builder removePoint(int idx) {
            this.path.remove(idx);
            return this;
        }

        public Builder setLookaheadDistance(double lookaheadDistance) {
            this.lookaheadDistance = lookaheadDistance;
            return this;
        }

        public Builder setLocalizer(Localizer localizer) {
            this.localizer = localizer;
            return this;
        }

        public Builder setSpeed(double speed) {
            this.speed = speed;
            return this;
        }

        public double getPathDistance() {
            double d = 0;

            for (int i = 0; i < this.path.size() - 1; i++) {
                Point p1 = this.path.get(i);
                Point p2 = this.path.get(i + 1);

                d += p1.distanceTo(p2);
            }

            return d;
        }

        public PurePursuitPathFollower build() {
            return new PurePursuitPathFollower(
                    this.path.toArray(new Point[0]),
                    this.localizer,
                    this.lookaheadDistance,
                    this.speed
            );
        }
    }
}