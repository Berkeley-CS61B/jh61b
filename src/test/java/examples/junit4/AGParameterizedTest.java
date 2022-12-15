package examples.junit4;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import jh61b.grader.GradedTest;
import jh61b.grader.AutograderRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@RunWith(Parameterized.class)
public class AGParameterizedTest {
    static class Fibonacci {
        public static int compute(int n) {
            int result = 0;

            if (n <= 1) {
                result = n;
            } else {
                result = compute(n - 1) + compute(n - 2);
            }

            return result;
        }
    }

    @Parameters(name = "Fibonacci {index}: fib({0})={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { 0, 0 }, { 1, 1 }, { 2, 1 }, { 3, 2 }, { 4, 3 }, { 5, 5 }, { 6, 8 }
        });
    }

    private int fInput;

    private int fExpected;

    public AGParameterizedTest(int input, int expected) {
        this.fInput = input;
        this.fExpected = expected;
    }

    @Test
    @GradedTest(number = "1")
    public void test() {
        assertEquals(fExpected, Fibonacci.compute(fInput));
    }

    public static void main(String[] args) {
        AutograderRunner.run(new String[]{"--json"});
    }
}
