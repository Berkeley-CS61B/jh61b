package examples.junit5;

import jh61b.grader.GradedTest;
import jh61b.grader.AutograderRunner;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AGTest {

    @Test
    @GradedTest(name = "Passing JUnit 5 test with no output", number = "1", max_score = 1)
    public void passingTest5() {
    }

    @Test
    @GradedTest(name = "Passing JUnit 5 test with output", number = "2", max_score = 1)
    public void passingTest5Output() {
        System.out.println("This is some output.");
        System.out.println("Here's a second line of output.");
    }

    @Test
    @GradedTest(name = "Failing JUnit 5 test", number = "3", max_score = 1)
    public void failingTest5() {
        fail();
    }

    @Test
    @GradedTest(name = "Failing JUnit 5 test with message", number = "4", max_score = 1)
    public void failingTest5WithMessage() {
        fail("Failure Message");
    }

    @Test
    @GradedTest(name = "Failing JUnit 5 test with output and message", number = "5", max_score = 1)
    public void failingTest5WithOutputAndMessage() {
        System.out.println("This is some output.");
        System.out.println("Here's a second line of output.");
        fail("Failure Message");
    }

    @Test
    @GradedTest(name = "Failing JUnit 5 test with output and assertion", number = "6", max_score = 1)
    public void failingTest5WithAssertion() {
        System.out.println("This is some output.");
        System.out.println("Here's a second line of output.");
        assertEquals("Wrong string", "A", "B");
    }

    public static void main(String[] args) {
        AutograderRunner.run(new String[]{"--json"});
    }
}
