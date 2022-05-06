package de.derredstoner.anticheat.util;

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

    public static boolean isAlmostEqual(float num1, float num2) {
        return Math.abs(num1 - num2) < 0.001;
    }

}
