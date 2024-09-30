package t10.reconstructor;

/**
 * Represents a point (x, y)
 */
public class Point {
    protected double x;
    protected double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    /**
     * @param point The other point to find the distance between
     * @return The Euclidean distance between this point and {@code point}.
     */
    public double distanceTo(Point point) {
        double dx = point.x - this.x;
        double dy = point.y - this.y;

        return Math.sqrt(dx * dx + dy * dy);
    }
}
