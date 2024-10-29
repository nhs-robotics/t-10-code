package t10.utils;

import androidx.annotation.NonNull;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.io.Serializable;
import java.text.NumberFormat;

public class MovementVector implements Serializable {

    private final Vector3D vector;

    public MovementVector(double vertical, double horizontal, double rotation) {
        this.vector = new Vector3D(vertical, horizontal, rotation);
    }

    public MovementVector(double[] v) {
        this.vector = new Vector3D(v);
    }

    private MovementVector(Vector3D v) {
        this.vector = v;
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

    @NonNull
    @Override
    public String toString() {
        return vector.toString();
    }

    // Helper method to access the internal Vector3D instance
    private Vector3D getVector() {
        return this.vector;
    }
}
