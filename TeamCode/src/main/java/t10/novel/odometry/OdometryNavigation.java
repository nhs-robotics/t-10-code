package t10.novel.odometry;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.novel.mecanum.MecanumDriver;
import t10.utils.MovementVector;

public class OdometryNavigation {
    private NovelOdometry odometry;
    private MecanumDriver driver;
    public final double minError;
    public final double minAngleError;
    public final double maxLatVelocity;
    public final double maxAngVelocity;

    public OdometryNavigation(NovelOdometry odometry, MecanumDriver driver) {
        this.odometry = odometry;
        this.driver = driver;
        this.minError = 0.5;
        this.minAngleError = 5; //in degrees here
        maxLatVelocity = 10;
        maxAngVelocity = 15;
    }




    public void driveLateral(double distance)
    {
        double initialX = odometry.getRelativePose().getX();
        double finalY = odometry.getRelativePose().getY() + distance;
        while(Math.abs(finalY - odometry.getRelativePose().getY()) > minError) {
            driver.setVelocity(odometry.getRelativeVelocity(new MovementVector(-10 * Math.signum(distance), 0,0)));
            this.odometry.update();
            this.telemetryUpdate();
        }
        driver.setVelocity(new MovementVector(0,0,0));
    }

    public void driveHorizontal(double distance)
    {
        double initialY = odometry.getRelativePose().getY();
        double initialX = odometry.getRelativePose().getX();
        double finalX = initialX + distance;
        while(Math.abs(finalX - odometry.getRelativePose().getX()) > minError) {
            driver.setVelocity(odometry.getRelativeVelocity(new MovementVector(0, 10 * Math.signum(distance),0)));
            this.odometry.update();
            this.telemetryUpdate();
        }
        driver.setVelocity(new MovementVector(0,0,0));
    }

    public void driveDiagonal(double distanceX, double distanceY)
    {
        double initialY = odometry.getRelativePose().getY();
        double initialX = odometry.getRelativePose().getX();
        double finalX = initialX + distanceX;
        double finalY = initialY + distanceY;
        while((Math.abs(finalX - odometry.getRelativePose().getX()) > minError) || (Math.abs(finalY - odometry.getRelativePose().getY()) > minError)) {
            double speedX, speedY;
            /*if((Math.abs(finalX - odometry.getRelativePose().getX()) > minError))
            {
                speedX = 10 * Math.signum(distanceX);
            }
            else {
                speedX = finalX - odometry.getRelativePose().getX();
            }
            if((Math.abs(finalY - odometry.getRelativePose().getY()) > minError))
            {
                speedY = -10 * Math.signum(distanceY);
            }
            else {
                speedY = -(finalY - odometry.getRelativePose().getY());
            }*/
            speedX = 10 * Math.signum(distanceX);
            speedY = -10 * Math.signum(distanceY);
            driver.setVelocity(odometry.getRelativeVelocity(new MovementVector(speedY, speedX,0)));
            this.odometry.update();
            this.telemetryUpdate();
        }
        driver.setVelocity(new MovementVector(0,0,0));
    }

    public void turnAbsolute(double angle)
    {
        while(needAngleCorrectionDegrees(odometry.getRelativePose().getHeading(AngleUnit.DEGREES), angle))
        {
            driver.setVelocity(new MovementVector(0,0,findTurnSpeed(odometry.getRelativePose().getHeading(AngleUnit.DEGREES), angle)));
            this.odometry.update();
            this.telemetryUpdate();
        }
        driver.setVelocity(new MovementVector(0,0,0));
    }

    public void turnRelative(double angle)
    {
        double targetAngle = angle + odometry.getRelativePose().getHeading(AngleUnit.DEGREES);
        if(targetAngle > 180) {targetAngle -= 180;}
        if(targetAngle < -180) {targetAngle += 180;}
        turnAbsolute(targetAngle);
    }

    public void driveAbsolute(double targetX, double targetY)
    {
        double distanceX = targetX - odometry.getRelativePose().getX();
        double distanceY = targetY - odometry.getRelativePose().getY();
        while((Math.abs(targetX - odometry.getRelativePose().getX()) > minError) || (Math.abs(targetY - odometry.getRelativePose().getY()) > minError)) {
            double speedX, speedY;
            if((Math.abs(targetX - odometry.getRelativePose().getX()) > minError))
            {
                speedX = 10 * Math.signum(distanceX);
            }
            else {
                speedX = targetX - odometry.getRelativePose().getX();
            }
            if((Math.abs(targetY - odometry.getRelativePose().getY()) > minError))
            {
                speedY = 10 * Math.signum(distanceY);
            }
            else {
                speedY = targetY - odometry.getRelativePose().getY();
            }
            driver.setVelocity(odometry.getRelativeVelocity(new MovementVector(speedY, speedX,0)));
            this.odometry.update();
            this.telemetryUpdate();
        }
        driver.setVelocity(new MovementVector(0,0,0));
    }
    /** attempted perfect arbitrary to-point driving
     public void driveSmart(Pose targetPose)
     {
     MovementVector vector = calcTrigVelocity(targetPose,odometry.getRelativePose());
     vector = new MovementVector(-vector.getVertical(), vector.getHorizontal(), vector.getRotation());
     driver.setVelocity(vector);
     }
     public MovementVector calcTrigVelocity(Pose targetPose, Pose currentPose)
     {
     double deltaY = targetPose.getX() - currentPose.getX();
     double deltaY_abs = Math.abs(deltaY);
     double deltaX = targetPose.getY() - currentPose.getY();
     double deltaX_abs = Math.abs(deltaX);
     double currentAngle = currentPose.getHeading(AngleUnit.RADIANS);
     double targetAngle = targetPose.getHeading(AngleUnit.RADIANS);
     if (deltaY_abs < minError && deltaX_abs < minError)
     {
     return new MovementVector(0,0, findTurnSpeed(currentAngle,targetAngle));
     }
     else if (deltaY_abs < 5*minError && deltaX_abs > 5*minError)
     {
     return odometry.getRelativeVelocity(10*Math.signum(deltaY),deltaX);

     }
     else if (deltaY_abs > 5*minError && deltaX_abs < 5*minError)
     {
     return odometry.getRelativeVelocity(deltaY,10 * Math.signum(deltaX));
     }
     else if (deltaX_abs > 5*minError && deltaY_abs > 5*minError)
     {
     return odometry.getRelativeVelocity(10 * Math.signum(deltaY),10 * Math.signum(deltaX));
     }
     else {
     return new MovementVector(deltaY,deltaX,0);
     }
     }
     */

    public boolean needAngleCorrectionDegrees(double currentAngle, double targetAngle)
    {
        double startAngle = currentAngle + 180;
        double endAngle = targetAngle + 180;
        if( (startAngle < minAngleError && endAngle > 360 - startAngle) || (endAngle < minAngleError && startAngle > 360 - endAngle))
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


    public double findTurnSpeed(double currentAngle, double targetAngle)
    {
        double direction = 0;
        if(needAngleCorrectionDegrees(currentAngle, targetAngle)) {
            if (Math.abs(targetAngle)==180)
            {
                targetAngle = 180 * Math.signum(currentAngle);
            }
            if (targetAngle < currentAngle - Math.PI) {
                direction = -1;
                return maxAngVelocity * direction;
            } else if (targetAngle > currentAngle + Math.PI) {
                direction = 1;
                return maxAngVelocity * direction;
            }

            else if (targetAngle < currentAngle) {
                direction = 1;
            } else if (targetAngle > currentAngle) {
                direction = -1;
            }
        }
        if(Math.abs(targetAngle - currentAngle) < 5*minAngleError)
        {
            return direction * Math.abs(targetAngle - currentAngle);
        }
        else if ((targetAngle > 180-5*minAngleError || targetAngle < -180+5*minAngleError) && Math.abs(currentAngle) > 160)
        {
            return direction * (180 - Math.abs(currentAngle));
        }
        else {
            return maxAngVelocity * direction;
        }
    }

    private void telemetryUpdate()
    {
    }

}