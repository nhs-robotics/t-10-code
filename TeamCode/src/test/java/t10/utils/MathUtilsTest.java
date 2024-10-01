package t10.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class MathUtilsTest {
    @Test
    @DisplayName("sum(List<Double>)")
    void sum() {
        List<Double> nums = Arrays.asList(new Double[] {18.4, -2.5, -391.7, 0.0, -31.9});
        assertEquals(-407.7, MathUtils.sum(nums));
        assertArrayEquals(nums.toArray(), new Double[] {18.4, -2.5, -391.7, 0.0, -31.9});
    }

    @Test
    @DisplayName("weightedAverage(List<Double>, List<Double>)")
    void weightedAverage() {
        List<Double> nums = Arrays.asList(new Double[] {18.4, -2.5, -391.7, 0.0, -31.9});
        List<Double> weights = Arrays.asList(new Double[] {0.2, 0.4, 0.1, 0.1, 0.2});

        assertEquals(-42.87, MathUtils.weightedAverage(nums, weights), 0.00001);
        assertArrayEquals(nums.toArray(), new Double[] {18.4, -2.5, -391.7, 0.0, -31.9});
        assertArrayEquals(weights.toArray(), new Double[] {0.2, 0.4, 0.1, 0.1, 0.2});
    }

    @Test
    @DisplayName("normalize List<Double> weights")
    void normalize() {
        List<Double> weights = Arrays.asList(new Double[] {0.7, 0.4, 0.4, 0.1, 0.2});

        double[] normalizedWeights = MathUtils.normalize(weights).stream().mapToDouble(d -> d).toArray();

        assertArrayEquals(new double[] {0.38888888888, 0.22222222222, 0.22222222222, 0.05555555555, 0.11111111111}, normalizedWeights, 0.00001);
    }
}
