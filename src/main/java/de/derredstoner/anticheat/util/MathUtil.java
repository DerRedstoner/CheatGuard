package de.derredstoner.anticheat.util;

import com.comphenix.protocol.wrappers.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MathUtil {

    public static final double EXPANDER = Math.pow(2, 24);

    public static boolean isOpposite(double num1, double num2) {
        return (num1 > 0 && num2 < 0) || (num1 < 0 && num2 > 0);
    }

    public static float getCorrectYawDelta(float delta) {
        float distance = Math.abs(delta) % 360.0F;
        if (distance > 180.0F) {
            distance = 360.0F - distance;
        }
        return distance;
    }

    public static boolean isMathGround(final double num) {
        return num % (1/64D) < 1E-4;
    }

    public static float gcdRational(List<Float> numbers) {
        if(numbers == null) return 0;

        try {
            float result = numbers.get(0);
            for(int i = 1; i < numbers.size(); i++) {
                result = gcdRational(numbers.get(i), result, 0);
            }
            return result;
        } catch (StackOverflowError e) {
            return 0;
        }
    }

    public static float gcdRational(float a, float b, int counter) {
        try {
            if(a == 0 || counter > 50) {
                return b;
            }
            int quotient = getIntQuotient(b, a);
            float remainder = ((b / a) - quotient) * a;
            if(Math.abs(remainder) < Math.max(a, b) * 1E-3F)
                remainder = 0;
            counter++;
            return gcdRational(remainder, a, counter);
        } catch (StackOverflowError e) {
            return 0;
        }
    }

    public static int getIntQuotient(float dividend, float divisor) {
        float ans = dividend / divisor;
        float error = Math.max(dividend, divisor) * 1E-3F;
        return (int) (ans + error);
    }

    public static long gcd(final long current, final long previous) {
        return (previous <= 16384L) ? current : gcd(previous, current % previous);
    }

    public static double gcd(final double a, final double b) {
        try {
            if (a < b) {
                return gcd(b, a);
            }

            if (Math.abs(b) < 0.001) {
                return a;
            } else {
                return gcd(b, a - Math.floor(a / b) * b);
            }
        } catch (StackOverflowError e) {
            return 0;
        }
    }

    public static double getStandardDeviation(final Collection<? extends Number> data) {
        final double variance = getVariance(data);
        return Math.sqrt(variance);
    }

    public static double getVariance(final Collection<? extends Number> data) {
        int count = 0;

        double sum = 0.0;
        double variance = 0.0;

        double average;

        for (final Number number : data) {
            sum += number.doubleValue();
            ++count;
        }

        average = sum / count;

        for (final Number number : data) {
            variance += Math.pow(number.doubleValue() - average, 2.0);
        }

        return variance;
    }

    public static Pair<List<Double>, List<Double>> getOutliers(final Collection<? extends Number> collection) {
        final List<Double> values = new ArrayList<>();

        for (final Number number : collection) {
            values.add(number.doubleValue());
        }

        final double q1 = getMedian(values.subList(0, values.size() / 2));
        final double q3 = getMedian(values.subList(values.size() / 2, values.size()));

        final double iqr = Math.abs(q1 - q3);
        final double lowThreshold = q1 - 1.5 * iqr, highThreshold = q3 + 1.5 * iqr;

        final Pair<List<Double>, List<Double>> tuple = new Pair<>(new ArrayList<>(), new ArrayList<>());

        for (final Double value : values) {
            if (value < lowThreshold) {
                tuple.getFirst().add(value);
            }
            else if (value > highThreshold) {
                tuple.getSecond().add(value);
            }
        }

        return tuple;
    }

    private static double getMedian(final List<Double> data) {
        if (data.size() % 2 == 0) {
            return (data.get(data.size() / 2) + data.get(data.size() / 2 - 1)) / 2;
        } else {
            return data.get(data.size() / 2);
        }
    }

    public static int floor(double num) {
        int var = (int) num;
        return num < (double) var ? var - 1 : var;
    }

    public static boolean isAlmostEqual(float num1, float num2) {
        return Math.abs(num1 - num2) < 0.001;
    }

}
