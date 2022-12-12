package jh61b.utils;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class RuntimeInstrumentation {
    private static final int SKIP = 8;
    private static final int ITERATIONS = 100;

    public enum ComplexityType {
        CONSTANT(0, "constant"),
        LOGARITHMIC(1, "logarithmic"),
        LINEAR(2, "linear"),
        QUADRATIC(3, "quadratic"),
        WORSE(4, "worse than quadratic");

        private final String name;
        private int size;

        ComplexityType(int size, String name) {
            this.size = size;
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public boolean isSlowerThan(ComplexityType other) {
            return this.size > other.size;
        }
    }

    public static <DS> long timeFunction(DS ds, Consumer<DS> function) {
        long startTime = System.nanoTime();
        function.accept(ds);
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    public static <DS> ComplexityType getEmpiricalComplexity(Function<Integer, DS> provideDSOfSize, Consumer<DS> functionToTest, int numberOfDoubles) {
        List<Long> times = new ArrayList<>();
        int maxSize = (1 << (numberOfDoubles + SKIP));
        for (int currentSize = 1; currentSize < maxSize; currentSize *= 2) {
            long totalTime = 0;
            for (int i = 0; i < ITERATIONS; i++) {
                DS ds = provideDSOfSize.apply(currentSize);
                // C a c h e
                functionToTest.accept(ds);
                totalTime += timeFunction(ds, functionToTest);
            }
            times.add(Math.round((double)totalTime / ITERATIONS));
        }

        for (int i = 0; i < SKIP; i++) {
            times.remove(0);
        }

        if (isApproximately(ComplexityType.CONSTANT, times)) {
            return ComplexityType.CONSTANT;
        }
        else if (isApproximately(ComplexityType.LOGARITHMIC, times)) {
            return ComplexityType.LOGARITHMIC;
        }
        else if (isApproximately(ComplexityType.LINEAR, times)) {
            return ComplexityType.LINEAR;
        }
        else if (isApproximately(ComplexityType.QUADRATIC, times)) {
            return ComplexityType.QUADRATIC;
        }
        else {
            return ComplexityType.WORSE;
        }
    }

    private static Set<Integer> findMaxes(List<Long> times) {
        int maxIdx = 0;
        int sndIdx = -1;
        for (int i = 0; i < times.size(); i++) {
            if (times.get(i) > times.get(maxIdx)) {
                sndIdx = maxIdx;
                maxIdx = i;
            }
        }
        return Set.of(maxIdx, sndIdx);
    }

    private static boolean isApproximately(ComplexityType type, List<Long> times) {
        List<Double> y = new ArrayList<>();
        List<Double> x = new ArrayList<>();
        Set<Integer> skips = findMaxes(times);

        for (int i = 0; i < times.size(); i++) {
            if (skips.contains(i)) {
                continue;
            }

            int numElements = (1 << (i + SKIP));
            x.add((double) numElements);
            double d = 0.0;
            switch (type) {
                case CONSTANT:
                    d = times.get(i);
                    break;
                case LOGARITHMIC:
                    d = times.get(i) / (Math.log10(numElements) / Math.log10(2));
                    break;
                case LINEAR:
                    d = ((double)times.get(i)) / numElements;
                    break;
                case QUADRATIC:
                    d = ((double)times.get(i)) / (numElements * numElements);
                    break;
                default:
                    throw new RuntimeException("unimplemented isApproximately for " + type);
            }
            y.add(d);
        }

        // Store sums
        double sumX = 0;
        double sumY = 0;
        for (int i = 0; i < x.size(); i ++) {
            sumX += x.get(i);
            sumY += y.get(i);
        }

        // Calc standard deviation of numElements
        double std = 0;
        for (int i = 0; i < x.size(); i ++) {
            std += (Math.pow(x.get(i) - sumX / x.size(), 2));
        }
        std /= times.size();
        double slope;
        double inter;

        // If constant, no slope - best fit is mean of times
        if (type == ComplexityType.CONSTANT) {
            slope = 0;
            inter = sumY / y.size();
        }
        // Otherwise, do least squares regression to find the best
        // linear fit for the transformed times
        else {
            double cov = 0;
            for (int i = 0; i < y.size(); i ++) {
                cov += (x.get(i) - sumX / y.size()) * (y.get(i) - sumY / y.size());

            }
            cov /= y.size() - 1;
            slope = cov / std;
            inter = sumY / y.size() - slope * sumX / y.size();
        }

        // Calculate mean squared error
        double mse = 0;
        for (int i = 0; i < y.size(); i ++) {
            mse += Math.pow(y.get(i) - inter + slope * x.get(i), 2);
        }

        // Use R^2 measure to check fit
        double rSq = 1 - mse / std;

        // Tune depending on strictness - 0.95 accounts for variations
        // *Should* actually be like 0.99, but sometimes weird heap operations
        // happen and make certain runs take longer
        // System.out.println(type + ", " + y + ", MSE: " + mse + ", R^2: " + rSq);
        return rSq >= 0.92;
    }

    public static <DS> void assertAtMost(String whatIsBeingTested, ComplexityType expected, Function<Integer, DS> provideDSOfSize, Consumer<DS> functionToTest, int numberOfDoubles) {
        ComplexityType calculated = getEmpiricalComplexity(provideDSOfSize, functionToTest, numberOfDoubles);
        if (calculated.isSlowerThan(expected)) {
            fail(whatIsBeingTested + " is expected to be " + expected + " time or better. The actual calculated time is " + calculated + ".\nThis test is non-deterministic which means it might not always be correct.  If you run it multiple times and it usually passes, that's probably fine.");
        }
    }
}

