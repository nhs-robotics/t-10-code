package t10.novel.odometry;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.novel.mecanum.MecanumDriver;
import t10.reconstructor.Pose;

public class OdometryNavigation {
    private NovelOdometry odometry;
    private MecanumDriver driver;
    private double minError;
    private double minAngleError;
    private double maxLatVelocity;
    private double maxAngVelocity;

    public OdometryNavigation(NovelOdometry odometry, MecanumDriver driver) {
        this.odometry = odometry;
        this.driver = driver;
        this.minError = 0.5;
        this.minAngleError = Math.PI / 60; //in radians here
        maxLatVelocity = 10;
        maxAngVelocity = 5;

    }

    public OdometryNavigation(NovelOdometry odometry, MecanumDriver driver, double minError) {
        this.odometry = odometry;
        this.driver = driver;
        this.minError = minError;
    }

   /* public double dist_to_position()
    {
        return Math.sqrt((odometry.getRelativePose().getX())^2)

    }

    public void driveDeadWheel(Vector3D displacement)
    {
        while(dist_to_position < minError)
    }
*/

    public Vector3D calcAdjust(Pose targetPose, Pose currentPose)
    {
        double scaleFactor;
        double deltaX = targetPose.getX() - currentPose.getX();
        double deltaY = targetPose.getY() - currentPose.getY();
        double currentAngle = currentPose.getHeading(AngleUnit.RADIANS);
        double targetAngle = targetPose.getHeading(AngleUnit.RADIANS);
        double velocityVertical = 1;
        double velocityHorizontal;
        if(Math.sqrt(deltaX * deltaX + deltaY * deltaY) < minError)
        {
            return new Vector3D(0,0, findTurnSpeed(currentAngle,targetAngle) * maxAngVelocity);
        }
        else if (deltaX != 0) {
            velocityHorizontal = ((deltaY / deltaX) * Math.sin(currentAngle) - Math.cos(currentAngle)) / (Math.sin(currentAngle) - (deltaY / deltaX) * Math.cos(currentAngle));
        }
        else {
            velocityHorizontal = (((deltaX / deltaY) * Math.cos(currentAngle) - Math.sin(currentAngle)) / (Math.cos(currentAngle) - (deltaX / deltaY) * Math.sin(currentAngle)));
        }

        if(velocityHorizontal > velocityVertical) {
            scaleFactor = 10 / velocityHorizontal;
        }
        else { scaleFactor = 10;}

        return new Vector3D(scaleFactor * velocityVertical,scaleFactor * velocityHorizontal, findTurnSpeed(currentAngle,targetAngle) * maxAngVelocity);
    }
    private boolean needAngleCorrection(double currentAngle, double targetAngle)
    {
        double startAngle = currentAngle + Math.PI;
        double endAngle = targetAngle + Math.PI;
        if( (startAngle < minAngleError && endAngle > 2*Math.PI - startAngle) || (endAngle < minAngleError && startAngle > 2*Math.PI - endAngle))
        {
            return false;
        }
        else if (Math.abs(endAngle - startAngle) < minAngleError)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    private double findTurnSpeed(double currentAngle,double targetAngle)
    {
        double turnPower;
        if( needAngleCorrection(currentAngle, targetAngle))
        {
            if (currentAngle > 0) {
                if ((targetAngle > currentAngle) || (targetAngle < currentAngle - Math.PI)) {
                    turnPower = 1;
                } else {
                    turnPower = -1;
                }
            }
            else {
                if ((targetAngle < currentAngle) || (targetAngle > currentAngle + Math.PI)) {
                    turnPower = -1;
                } else {
                    turnPower = 1;
                }
            }
        }
        else {turnPower = 0;}
        return turnPower;
    }


}