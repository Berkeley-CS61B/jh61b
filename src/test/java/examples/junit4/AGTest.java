package examples.junit4;

import jh61b.grader.GradedTest;
import jh61b.grader.AutograderRunner;

import org.junit.Test;

import static org.junit.Assert.*;


public class AGTest {

    @Test
    @GradedTest(name = "Passing JUnit 4 test with no output", number = "1")
    public void passingTest4() {
    }

    @Test
    @GradedTest(name = "Passing JUnit 4 test with output", number = "2")
    public void passingTest4Output() {
        System.out.println("This is some output.");
        System.out.println("Here's a second line of output.");
    }

    @Test
    @GradedTest(name = "Failing JUnit 4 test", number = "3")
    public void failingTest4() {
        fail();
    }

    @Test
    @GradedTest(name = "Failing JUnit 4 test with message", number = "4")
    public void failingTest4WithMessage() {
        fail("Failure Message");
    }

    @Test
    @GradedTest(name = "Failing JUnit 4 test with output and message", number = "5")
    public void failingTest4WithOutputAndMessage() {
        System.out.println("This is some output.");
        System.out.println("Here's a second line of output.");
        fail("Failure Message");
    }

    @Test
    @GradedTest(name = "Failing JUnit 4 test with output and assertion", number = "6")
    public void failingTest4WithAssertion() {
        System.out.println("This is some output.");
        System.out.println("Here's a second line of output.");
        assertEquals("Wrong string", "A", "B");
    }


    public static void main(String[] args) {
        AutograderRunner.run(new String[]{"--json"});
    }
}
