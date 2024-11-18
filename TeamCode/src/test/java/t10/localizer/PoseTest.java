package t10.localizer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import t10.geometry.Pose;
import t10.geometry.MovementVector;

public class PoseTest {

    private Pose pose = new Pose(3, -5, 90, AngleUnit.DEGREES);

    @BeforeEach
    void resetVectorPose() {
        pose = new Pose(3, -5, 90, AngleUnit.DEGREES);
    }

    @Test
    @DisplayName("constructor(double, double, double, AngleUnit:DEGREES)")
    void constructorAndGetters() {
        assertEquals(3, pose.getY());
        assertEquals(-5, pose.getX());
        assertEquals(90, pose.getHeading(AngleUnit.DEGREES));
        assertEquals(Math.PI / 2, pose.getHeading(AngleUnit.RADIANS));
    }

    @Test
    @DisplayName("constructor(double, double, double, AngleUnit:RADIANS)")
    void constructorAndGetters2() {
        pose = new Pose(3, -5, Math.PI / 2, AngleUnit.RADIANS);
        assertEquals(3, pose.getY());
        assertEquals(-5, pose.getX());
        assertEquals(90, pose.getHeading(AngleUnit.DEGREES));
        assertEquals(Math.PI / 2, pose.getHeading(AngleUnit.RADIANS));
    }

    @Test
    @DisplayName("add(MovementVector)")
    void add() {
        Pose newPose = pose.add(new Pose(-5, 8, -89, AngleUnit.DEGREES));
        assertEquals(-2, newPose.getY());
        assertEquals(3, newPose.getX());
        assertEquals(1, newPose.getHeading(AngleUnit.DEGREES), 0.00001);
    }

    @Test
    @DisplayName("subtract(MovementVector)")
    void subtract() {
        Pose newPose = pose.subtract(new Pose(-5, 8, -89, AngleUnit.DEGREES));
        assertEquals(8, newPose.getY());
        assertEquals(-13, newPose.getX());
        assertEquals(179, newPose.getHeading(AngleUnit.DEGREES));
    }

    @Test
    @DisplayName("toMovementVector()")
    void toMovementVector() {
        MovementVector vector = pose.toMovementVector();
        assertEquals(3, vector.getVertical());
        assertEquals(-5, vector.getHorizontal());
        assertEquals(90, vector.getRotation());
    }

    @Test
    @DisplayName("fromMovementVector(Pose)")
    void fromMovementVector() {
        Pose newPose = Pose.fromMovementVector(new MovementVector(3, -5, 90));
        assertEquals(3, newPose.getY());
        assertEquals(-5, newPose.getX());
        assertEquals(90, newPose.getHeading(AngleUnit.DEGREES));
    }

    @Test
    @DisplayName("setHeading(double, AngleUnit)")
    void setHeading() {
        pose.setHeading(135, AngleUnit.DEGREES);
        assertEquals(135, pose.getHeading(AngleUnit.DEGREES));
        assertEquals(3 * Math.PI / 4, pose.getHeading(AngleUnit.RADIANS));
    }
}
