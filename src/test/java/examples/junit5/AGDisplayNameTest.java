package examples.junit5;

import jh61b.grader.GradedTest;
import jh61b.grader.AutograderRunner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AGDisplayNameTest {

    @Test
    @DisplayName("This is a display name")
    @GradedTest(number = "1")
    public void displayNameTest() {}

    @Test
    @DisplayName("This display name will be ignored")
    @GradedTest(name = "The GradedTest annotation has a name attribute", number = "2")
    public void noDisplayNameTest() {}

    public static void main(String[] args) {
        AutograderRunner.run(new String[]{"--json"});
    }
}
