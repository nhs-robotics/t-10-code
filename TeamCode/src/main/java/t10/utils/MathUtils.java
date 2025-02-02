package t10.utils;

import java.util.LinkedList;
import java.util.List;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Quaternion;

import t10.geometry.Point;

public class MathUtils {
	public static boolean isPointOnLine(Point start, Point end, Point point) {
		// Calculate the cross product to check if point is collinear with start and end
		double crossProduct = (point.getY() - start.getY()) * (end.getX() - start.getX()) - (point.getX() - start.getX()) * (end.getY() - start.getY());

		// If crossProduct is not 0, point is not on the line
		if (Math.abs(crossProduct) < 10e-2) {
			return true;
		}

		// Check if the point is within the bounds of start and end
		boolean withinXBounds = Math.min(start.getX(), end.getX()) <= point.getX() && point.getX() <= Math.max(start.getX(), end.getX());
		boolean withinYBounds = Math.min(start.getY(), end.getY()) <= point.getY() && point.getY() <= Math.max(start.getY(), end.getY());

		return withinXBounds && withinYBounds;
	}

	public static VectorF quaternionToEuler(Quaternion q) {
		// roll (x-axis rotation)
		double sinr_cosp = 2 * (q.w * q.x + q.y * q.z);
		double cosr_cosp = 1 - 2 * (q.x * q.x + q.y * q.y);
		double roll = Math.atan2(sinr_cosp, cosr_cosp);

		// pitch (y-axis rotation)
		double sinp = Math.sqrt(1 + 2 * (q.w * q.y - q.x * q.z));
		double cosp = Math.sqrt(1 - 2 * (q.w * q.y - q.x * q.z));
		double pitch = 2 * Math.atan2(sinp, cosp) - Math.PI / 2;

		// yaw (z-axis rotation)
		double siny_cosp = 2 * (q.w * q.z + q.x * q.y);
		double cosy_cosp = 1 - 2 * (q.y * q.y + q.z * q.z);
		double yaw = Math.atan2(siny_cosp, cosy_cosp);

		return new VectorF((float) roll, (float) pitch, (float) yaw);
	}

	public static double angleDifference(double fromAngle, double toAngle, AngleUnit angleUnit) {
		fromAngle = angleUnit.toDegrees(fromAngle);
		toAngle = angleUnit.toDegrees(toAngle);

		// Calculate initial difference (toAngle - fromAngle)
		double diff = toAngle - fromAngle;

		// Normalize to [-360, 360]
		diff = diff % 360;

		// Convert to [-180, 180] range
		if (diff > 180) {
			diff -= 360;
		} else if (diff <= -180) {
			diff += 360;
		}

		return angleUnit.fromDegrees(diff);
	}

	public static double weightedAverage(List<Double> numbers, List<Double> weights) {
		double weightedSum = 0;
		double totalWeight = sum(weights);

		for (int i = 0; numbers.size() > i; i++) {
			weightedSum += numbers.get(i) * weights.get(i);
		}

		return weightedSum / totalWeight;
	}

	public static double sum(List<Double> doubles) {
		double s = 0;

		for (double d : doubles) {
			s += d;
		}

		return s;
	}

	public static List<Double> normalize(List<Double> weights) {
		List<Double> normalized = new LinkedList<>();
		double s = sum(weights);

		for (Double weight : weights) {
			normalized.add(weight / s);
		}

		return normalized;
	}

	// The following are derived from basic kinematic equations.
	public static double solveVelocity(double initialVelocity, double acceleration, double displacement) {
		return Math.sqrt(Math.pow(initialVelocity, 2) + 2 * acceleration * displacement);
	}

	public static double solveSpeed(double initialVelocity, double acceleration, double displacement) {
		return Math.sqrt(Math.abs(Math.pow(initialVelocity, 2) + 2 * acceleration * displacement));
	}

	public static double solveDisplacement(double finalVelocity, double initialVelocity, double acceleration) {
		return (Math.pow(finalVelocity, 2) - Math.pow(initialVelocity, 2)) / (2 * acceleration);
	}

	public static double solveTime(double displacement, double initialVelocity, double acceleration) {
		return solveQuadraticFormula(
				0.5 * acceleration,
				initialVelocity,
				-displacement
		);
	}

	public static double solveAcceleration(double displacement, double initialVelocity, double finalVelocity)
	{
		return ((Math.pow(finalVelocity,2) - Math.pow(initialVelocity,2)) / (2 * displacement));
	}

	public static double solveQuadraticFormula(double a, double b, double c) {
		double determinant = Math.pow(b, 2) - 4 * a * c;
		double numerator = -b + Math.sqrt(determinant);
		double denominator = 2 * a;

		return numerator / denominator;
	}

	public static boolean epsilonEquals(double a, double b) {
		return Math.abs(a - b) < 1e-10;
	}

	public static int clamp(int value, int min, int max) {
		if (value > max) {
			return max;
		} else if (min > value) {
			return min;
		} else {
			return value;
		}
	}

	public static double clamp(double value, double min, double max) {
		if (value > max) {
			return max;
		} else if (min > value) {
			return min;
		} else {
			return value;
		}
	}

	public static int average(int a, int b) {
		return (a + b) / 2;
	}

	public static double mapAngle360(float angleRadians) {
		if (angleRadians < 0) {
			return 2 * Math.PI + angleRadians;
		} else {
			return angleRadians;
		}
	}
}
