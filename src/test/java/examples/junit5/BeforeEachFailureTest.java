package examples.junit5;

import jh61b.grader.AutograderRunner;
import jh61b.grader.GradedTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class BeforeEachFailureTest {
    public static void main(String[] args) {
        AutograderRunner.run(new String[]{"--json"});
    }

    @BeforeEach
    public void brokenSetup() {
        throw new RuntimeException("setup error in BeforeEach!");
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
