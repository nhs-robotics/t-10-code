package t10.geometry;

public class Point3 extends Point {
    protected double z;

    public Point3(double x, double y, double z) {
        super(x, y);
        this.z = z;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double distanceTo(Point3 point3) {
        double dx = point3.x - this.x;
        double dy = point3.y - this.y;
        double dz = point3.z - this.z;

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
