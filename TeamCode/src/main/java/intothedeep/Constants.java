package intothedeep;

import t10.motion.mecanum.MecanumCoefficientMatrix;

public class Constants {
	/**
	 * Constants for the webcam.
	 * Camera: C270
	 * Units: Pixels
	 * <a href="https://horus.readthedocs.io/en/release-0.2/source/scanner-components/camera.html">Source</a>
	 */
	public static class Webcam {
		public static final double C270_FOCAL_LENGTH_X = 649.5832;
		public static final double C270_FOCAL_LENGTH_Y = 649.5805;
		public static final double C270_PRINCIPAL_POINT_X = 330.0829;
		public static final double C270_PRINCIPAL_POINT_Y = 231.2183;
		public static final int CAMERA_RES_WIDTH = 640;
		public static final int CAMERA_RES_HEIGHT = 480;
	}

	public static class Robot {
		public static final double ROBOT_DIAMETER_IN = 17.341;
		public static final double WHEEL_DIAMETER_IN = 11.8737360135;
		public static final double ACTUAL_DIAMETER_IN = 3.7795275590551185;
		public static final double ROBOT_WIDTH_IN = 16.9291;
	}

    public static class TickCounts {
        public static final double LIFT_MOTOR_TICK_COUNT = ((((((1+(46d/17d))) * (1+(46d/17d))) * (1+(46d/17d))) * (1+(46d/17d))) * 28);
        public static final double MOVEMENT_MOTOR_TICK_COUNT = ((((1 + (46d / 17d))) * (1 + (46d / 11d))) * 28);  // This equation is pulled straight from https://www.gobilda.com/5203-series-yellow-jacket-planetary-gear-motor-19-2-1-ratio-24mm-length-8mm-rex-shaft-312-rpm-3-3-5v-encoder/
    }

	public static class Odometry {
		public static final double ODOMETRY_WHEEL_DIAMETER_IN = 2.0;
		public static final double TICKS_PER_ODOMETRY_REVOLUTION = 8192;
	}

	public static class Coefficients {
		public static final MecanumCoefficientMatrix SNOWBALL_COEFFICIENTS = new MecanumCoefficientMatrix(new double[]{
				-1, 1, 1, -1
		});

		public static final MecanumCoefficientMatrix KEVIN_COEFFICIENTS = new MecanumCoefficientMatrix(new double[]{
				-1, 1, -1, 1
		});
	}

	public static final double APRIL_TAG_SIZE_METERS = 0.0508;
	public static final int GAMEPAD_JOYSTICK_Y_COEFFICIENT = -1;
}
