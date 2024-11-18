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
    private Pose lastFieldCentricPose;

    public Localizer(
            @Nullable AprilTagLocalizer aprilTagLocalizer,
            @NotNull OdometryLocalizer odometerLocalizer,
            @NotNull Pose initialPose
    ) {
        this.aprilTagLocalizer = aprilTagLocalizer;
        this.odometerLocalizer = odometerLocalizer;
        this.setFieldCentricPose(initialPose);
    }

    public void setFieldCentricPose(Pose pose) {
        this.odometerLocalizer.setFieldCentricPose(new Pose(0, 0, 0, AngleUnit.RADIANS));
        this.lastFieldCentricPose = pose;
    }

    public Pose getFieldCentricPose() {
        this.odometerLocalizer.update();
        Pose odometryPose = this.odometerLocalizer.getFieldCentricPose();

        if (this.aprilTagLocalizer != null) {
            Point fieldLocation = this.aprilTagLocalizer.getFieldLocation();

            if (fieldLocation != null) {
                this.setFieldCentricPose(
                        new Pose(
                                fieldLocation.getY(),
                                fieldLocation.getX(),
                                this.lastFieldCentricPose.getHeading(AngleUnit.RADIANS) + odometryPose.getHeading(AngleUnit.RADIANS),
                                AngleUnit.RADIANS
                        )
                );

                return this.lastFieldCentricPose;
            }
        }

        return this.lastFieldCentricPose.add(odometryPose);
    }
}
