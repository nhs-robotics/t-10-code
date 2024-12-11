package t10.localizer;

import org.firstinspires.ftc.robotcore.external.navigation.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t10.geometry.Point;
import t10.geometry.Pose;
import t10.localizer.apriltag.AprilTagLocalizer;
import t10.localizer.odometry.OdometryLocalizer;

public class Localizer {
    private final @NotNull OdometryLocalizer odometerLocalizer;
    private final @Nullable AprilTagLocalizer aprilTagLocalizer;

    public Localizer(
            @Nullable AprilTagLocalizer aprilTagLocalizer,
            @NotNull OdometryLocalizer odometerLocalizer,
            @NotNull Pose initialPose
    ) {
        this.aprilTagLocalizer = aprilTagLocalizer;
        this.odometerLocalizer = odometerLocalizer;
        this.odometerLocalizer.setFieldCentricPose(initialPose);
    }

    public Pose getFieldCentricPose() {
        this.odometerLocalizer.update();
        Pose odometryPose = this.odometerLocalizer.getFieldCentricPose();

        if (this.aprilTagLocalizer != null) {
            Point fieldLocation = this.aprilTagLocalizer.getFieldLocation();

            if (fieldLocation != null) {
                Pose newPose = new Pose(fieldLocation.getY(), fieldLocation.getX(), odometryPose.getHeading(AngleUnit.RADIANS), AngleUnit.RADIANS);
                this.odometerLocalizer.setFieldCentricPose(newPose);
                return newPose;
            }
        }

        return odometryPose;
    }
}
