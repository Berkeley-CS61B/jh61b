package examples.junit5;

import jh61b.grader.AutograderRunner;
import jh61b.grader.GradedTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class SetupFailureTest {
    @BeforeAll
    public static void brokenSetup() {
        throw new RuntimeException("setup error in BeforeAll!");
    }

    public static void main(String[] args) {
        AutograderRunner.run(new String[]{"--json"});
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
