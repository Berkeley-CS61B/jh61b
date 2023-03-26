package examples.junit5;

import jh61b.grader.AutograderRunner;
import jh61b.grader.GradedTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class TeardownFailureTest {
    @AfterAll
    public static void brokenTeardown() {
        throw new RuntimeException("teardown error in AfterAll");
    }

    public static void main(String[] args) {
        AutograderRunner.run(new String[]{});
    }

    @Test
    @GradedTest(number = "1")
    public void emptyTest() {

    }

    @Test
    @GradedTest(number = "2")
    public void brokenTest() {
        fail("This is a broken test.");
    }
}
