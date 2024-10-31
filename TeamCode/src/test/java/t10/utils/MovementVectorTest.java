package t10.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MovementVectorTest {

    private MovementVector vector = new MovementVector(3, -5, 93);

    @BeforeEach
    void resetVector() {
        vector = new MovementVector(3, -5, 93);
    }

    @Test
    @DisplayName("constructor(double, double, double)")
    void constructorAndGetters() {
        assertEquals(3, vector.getVertical());
        assertEquals(-5, vector.getHorizontal());
        assertEquals(93, vector.getRotation());
    }

    @Test
    @DisplayName("constructor(double[])")
    void constructorAndGetters2() {
        vector = new MovementVector(new double[] {3, -5, 93});
        assertEquals(3, vector.getVertical());
        assertEquals(-5, vector.getHorizontal());
        assertEquals(93, vector.getRotation());
    }

    @Test
    @DisplayName("constructor(Vector3D)")
    void constructorAndGetters3() {
        Vector3D v = new Vector3D(3, -5, 93);
        vector = new MovementVector(v);
        assertEquals(3, vector.getVertical());
        assertEquals(-5, vector.getHorizontal());
        assertEquals(93, vector.getRotation());
    }

    @Test
    @DisplayName("add(MovementVector)")
    void add() {
        MovementVector newVector = vector.add(new MovementVector(-5, 8, -3));
        assertEquals(-2, newVector.getVertical());
        assertEquals(3, newVector.getHorizontal());
        assertEquals(90, newVector.getRotation());
    }

    @Test
    @DisplayName("subtract(MovementVector)")
    void subtract() {
        MovementVector newVector = vector.subtract(new MovementVector(-5, 8, -3));
        assertEquals(8, newVector.getVertical());
        assertEquals(-13, newVector.getHorizontal());
        assertEquals(96, newVector.getRotation());
    }

    @Test
    @DisplayName("scalarMultiply(double)")
    void scalarMultiply() {
        MovementVector newVector = vector.scalarMultiply(5);
        assertEquals(15, newVector.getVertical());
        assertEquals(-25, newVector.getHorizontal());
        assertEquals(465, newVector.getRotation());
    }

    @Test
    @DisplayName("getMagnitude()")
    void getMagnitude() {
        assertEquals(Math.sqrt(8683), vector.getMagnitude());

    }

    @Test
    @DisplayName("normalize()")
    void normalize() {
        MovementVector newVector = vector.normalize();
        assertEquals(1, newVector.getMagnitude(), 0.00001);
    }

    @Test
    @DisplayName("equals(Object)")
    void equals() {
        assertFalse(vector.equals(new MovementVector(3, 5, 93)));
        assertTrue(vector.equals(new MovementVector(3, -5, 93)));
        assertTrue(vector.equals(vector));
        assertFalse(vector.equals("Not a MovementVector"));
    }

    @Test
    @DisplayName("toString()")
    void toStringTest() {
        assertEquals("{3; -5; 93}", vector.toString());
    }
}
