package t10.novel.odometry;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import t10.novel.mecanum.MecanumDriver;

public class OdometryNavigation {
    private NovelOdometry odometry;
    private MecanumDriver driver;
    private double minError;

    public OdometryNavigation(NovelOdometry odometry, MecanumDriver driver)
    {
        this.odometry = odometry;
        this.driver = driver;
        this.minError = 0.5;
    }
    public OdometryNavigation(NovelOdometry odometry, MecanumDriver driver, double minError)
    {
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
    public void driveLateral(double dist_inch)
    {
        double final_position = odometry.getRelativePose().getY() + dist_inch;
        while(Math.abs(final_position - odometry.getRelativePose().getY()) > minError)
        {
            driver.setVelocity(new Vector3D(10,0,0));
        }
    }
}
