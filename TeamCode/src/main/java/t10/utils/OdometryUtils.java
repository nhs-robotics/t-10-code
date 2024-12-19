package t10.utils;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.geometry.MovementVector;
import t10.geometry.Pose;

public class OdometryUtils {

    public static MovementVector getRobotCentricVelocity(MovementVector absoluteVelocity, Pose pose)
    {
        double theta = pose.getHeading(AngleUnit.RADIANS);
        double forwardRelative = (absoluteVelocity.getVertical() * Math.cos(theta) + absoluteVelocity.getHorizontal() * Math.sin(theta));
        double rightwardRelative = -absoluteVelocity.getVertical() * Math.sin(theta) + absoluteVelocity.getHorizontal() * Math.cos(theta);
        return new MovementVector(forwardRelative, rightwardRelative, 0);
    }

    //IMPORTANT - this MUST be iterated, otherwise it'll keep going in the earlier direction while rotating - and not work
    public static MovementVector getRobotCentricVelocity(double lateral, double horizontal, Pose pose)
    {
        return getRobotCentricVelocity(new MovementVector(lateral, horizontal, 0), pose);
    }
}
