package t10.localizer.odometry;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import t10.motion.mecanum.MecanumDriver;
import t10.geometry.MovementVector;

public class OdometryNavigation {
    private OdometryLocalizer odometry;
    private MecanumDriver driver;
    public final double MIN_ERROR = 0.5;
    public final double MIN_ANGLE_ERROR = 2;
    public final double MAX_LATERAL_VELOCITY = 10;
    public final double MAX_ANG_VELOCITY = 15;

    public OdometryNavigation(OdometryLocalizer odometry, MecanumDriver driver) {
        this.odometry = odometry;
        this.driver = driver;
    }

    /**
     * @param distanceY The distance to travel in the field-relative Y direction (the 0 direction, in odometry)
     * @param distanceX The distance to travel in the field-relative X direction (the 90 direction, in odometry)
     */
    public void odometryDrive(double distanceY, double distanceX) {
        double finalX = odometry.getFieldCentricPose().getX() + distanceX;
        double finalY = odometry.getFieldCentricPose().getY() + distanceY;
        double distX, distY;
        do {
            // Find the remaining displacement
            distX = finalX - odometry.getFieldCentricPose().getX();
            distY = finalY - odometry.getFieldCentricPose().getY();

            // Preserving their relative sizes, scale them so one direction is at the maximum velocity
            double scaleFactor = MAX_LATERAL_VELOCITY / Math.max(Math.abs(distX),Math.abs(distY));
            driver.setVelocity(odometry.changeToRobotCenteredVelocity(distY * scaleFactor, distX * scaleFactor));
            this.odometry.update();
        } while (Math.abs(distX) > MIN_ERROR && Math.abs(distY) > MIN_ERROR);
    }

    public void turnAbsolute(double angle) {
        while (needAngleCorrectionDegrees(odometry.getFieldCentricPose().getHeading(AngleUnit.DEGREES), angle)) {
            driver.setVelocity(new MovementVector(0, 0, findTurnSpeed(odometry.getFieldCentricPose().getHeading(AngleUnit.DEGREES), angle)));
            this.odometry.update();
        }
        driver.setVelocity(new MovementVector(0, 0, 0));
    }

    public void turnRelative(double angle) {
        double targetAngle = angle + odometry.getFieldCentricPose().getHeading(AngleUnit.DEGREES);
        if (targetAngle > 180) {
            targetAngle -= 360;
        }
        if (targetAngle < -180) {
            targetAngle += 360;
        }
        turnAbsolute(targetAngle);
    }

    public void driveAbsolute(double targetX, double targetY) {
        double distanceX = targetX - odometry.getFieldCentricPose().getX();
        double distanceY = targetY - odometry.getFieldCentricPose().getY();
        while((Math.abs(targetX - odometry.getFieldCentricPose().getX()) > MIN_ERROR) || (Math.abs(targetY - odometry.getFieldCentricPose().getY()) > MIN_ERROR)) {
            double speedX, speedY;
            if((Math.abs(targetX - odometry.getFieldCentricPose().getX()) > MIN_ERROR))
            {
                speedX = 10 * Math.signum(distanceX);
            }
            else {
                speedX = targetX - odometry.getFieldCentricPose().getX();
            }
            if((Math.abs(targetY - odometry.getFieldCentricPose().getY()) > MIN_ERROR))
            {
                speedY = 10 * Math.signum(distanceY);
            }
            else {
                speedY = targetY - odometry.getFieldCentricPose().getY();
            }
            driver.setVelocity(odometry.changeToRobotCenteredVelocity(new MovementVector(speedY, speedX,0)));
            this.odometry.update();
        }
        driver.setVelocity(new MovementVector(0,0,0));
    }

    /**
     * attempted perfect arbitrary to-point driving
     * public void driveSmart(Pose targetPose)
     * {
     * MovementVector vector = calcTrigVelocity(targetPose,odometry.getFieldCentricPose());
     * vector = new MovementVector(-vector.getVertical(), vector.getHorizontal(), vector.getRotation());
     * driver.setVelocity(vector);
     * }
     * public MovementVector calcTrigVelocity(Pose targetPose, Pose currentPose)
     * {
     * double deltaY = targetPose.getX() - currentPose.getX();
     * double deltaY_abs = Math.abs(deltaY);
     * double deltaX = targetPose.getY() - currentPose.getY();
     * double deltaX_abs = Math.abs(deltaX);
     * double currentAngle = currentPose.getHeading(AngleUnit.RADIANS);
     * double targetAngle = targetPose.getHeading(AngleUnit.RADIANS);
     * if (deltaY_abs < minError && deltaX_abs < minError)
     * {
     * return new MovementVector(0,0, findTurnSpeed(currentAngle,targetAngle));
     * }
     * else if (deltaY_abs < 5*minError && deltaX_abs > 5*minError)
     * {
     * return odometry.getRobotCentricVelocity(10*Math.signum(deltaY),deltaX);
     * <p>
     * }
     * else if (deltaY_abs > 5*minError && deltaX_abs < 5*minError)
     * {
     * return odometry.getRobotCentricVelocity(deltaY,10 * Math.signum(deltaX));
     * }
     * else if (deltaX_abs > 5*minError && deltaY_abs > 5*minError)
     * {
     * return odometry.getRobotCentricVelocity(10 * Math.signum(deltaY),10 * Math.signum(deltaX));
     * }
     * else {
     * return new MovementVector(deltaY,deltaX,0);
     * }
     * }
     */

    public boolean needAngleCorrectionDegrees(double currentAngle, double targetAngle) {
        double startAngle = currentAngle + 180;
        double endAngle = targetAngle + 180;
        if ((startAngle < MIN_ANGLE_ERROR && endAngle > 360 - startAngle) || (endAngle < MIN_ANGLE_ERROR && startAngle > 360 - endAngle)) {
            return false;
        } else if (Math.abs(endAngle - startAngle) < MIN_ANGLE_ERROR) {
            return false;
        } else {
            return true;
        }
    }


    public double findTurnSpeed(double currentAngle, double targetAngle) {
        double direction = 0;
            if (Math.abs(targetAngle) == 180) {
                targetAngle = 180 * Math.signum(currentAngle);
            }
            if (targetAngle < currentAngle - 180) {
                    direction = 1;
                } else if (targetAngle > currentAngle + 180) {
                    direction = -1;
                } else if (targetAngle < currentAngle) {
                    direction = -1;
                } else if (targetAngle > currentAngle) {
                    direction = 1;
                }

            if (Math.abs(targetAngle - currentAngle) < 5 * MIN_ANGLE_ERROR) {
                return direction * Math.abs(targetAngle - currentAngle);
            }
            else if(Math.abs(targetAngle - currentAngle) > 360 - 5* MIN_ANGLE_ERROR) {
                return direction * Math.abs(360 - (Math.abs(targetAngle - currentAngle)));
            }
            else {
                return MAX_ANG_VELOCITY * direction;
            }
        }
    }