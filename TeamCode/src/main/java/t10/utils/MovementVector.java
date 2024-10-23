package t10.utils;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.io.Serializable;
import java.text.NumberFormat;

public class MovementVector implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Vector3D vector;

    public MovementVector(double vertical, double horizontal, double rotation) {
        this.vector = new Vector3D(vertical, horizontal, rotation);
    }

    public MovementVector(double[] v) {
        this.vector = new Vector3D(v);
    }

    public MovementVector(Vector3D v) {
        this.vector = v;
    }

    public MovementVector(double alpha, double delta) {
        this.vector = new Vector3D(alpha, delta);
    }

    public MovementVector(double a, MovementVector u) {
        this.vector = new Vector3D(a, u.getVector());
    }

    public MovementVector(double a1, MovementVector u1, double a2, MovementVector u2) {
        this.vector = new Vector3D(a1, u1.getVector(), a2, u2.getVector());
    }

    public MovementVector(double a1, MovementVector u1, double a2, MovementVector u2,
                          double a3, MovementVector u3) {
        this.vector = new Vector3D(a1, u1.getVector(), a2, u2.getVector(), a3, u3.getVector());
    }

    public MovementVector(double a1, MovementVector u1, double a2, MovementVector u2,
                          double a3, MovementVector u3, double a4, MovementVector u4) {
        this.vector = new Vector3D(a1, u1.getVector(), a2, u2.getVector(), a3, u3.getVector(), a4, u4.getVector());
    }

    // Getters for the renamed components
    public double getVertical() {
        return vector.getX();
    }

    public double getHorizontal() {
        return vector.getY();
    }

    public double getRotation() {
        return vector.getZ();
    }

    // Wrapper for the methods of Vector3D
    public MovementVector add(final MovementVector mv) {
        return new MovementVector(vector.add(mv.getVector()));
    }

    public MovementVector subtract(final MovementVector mv) {
        return new MovementVector(vector.subtract(mv.getVector()));
    }

    public MovementVector scalarMultiply(double a) {
        return new MovementVector(vector.scalarMultiply(a));
    }

    public MovementVector normalize() {
        return new MovementVector(vector.normalize());
    }

    public double getNorm() {
        return vector.getNorm();
    }

    public double dotProduct(final MovementVector mv) {
        return vector.dotProduct(mv.getVector());
    }

    public MovementVector crossProduct(final MovementVector mv) {
        return new MovementVector(vector.crossProduct(mv.getVector()));
    }

    public double distance(final MovementVector mv) {
        return vector.distance(mv.getVector());
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof MovementVector) {
            MovementVector mv = (MovementVector) other;
            return vector.equals(mv.getVector());
        }
        return false;
    }

    public int hashCode() {
        return vector.hashCode();
    }

    @Override
    public String toString() {
        return vector.toString();
    }

    public String toString(final NumberFormat format) {
        return vector.toString(format);
    }

    // Helper method to access the internal Vector3D instance
    private Vector3D getVector() {
        return this.vector;
    }
}