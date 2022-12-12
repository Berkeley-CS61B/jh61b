package examples;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import jh61b.gradescope.GradedTest;
import jh61b.gradescope.AutograderRunner;

public class SampleAGTest {
    @org.junit.Test
    @GradedTest(name = "Test", number = "1", max_score = 1)
    public void testMyTest() {
        Object x = null;
        x.toString();
        assertTrue(true);
        assertFalse(false);
//        System.out.println("hi!!!");
    }

    @org.junit.Test
    @GradedTest(name = "Test 2", number = "2", max_score = 1)
    public void testMyTest2() {
        System.out.println("hi!");
        assertFalse(true);
    }

    @org.junit.jupiter.api.Test
    @GradedTest(name = "Truth Test 5", number = "3", max_score = 1)
    public void truthTest5() {
        String string = "awesome";
        assertThat(string).startsWith("awe");
        assertWithMessage("Without me, it's just aweso")
            .that(string)
            .contains("mes");
    }

    @org.junit.Test
    @GradedTest(name = "Truth Test 4", number = "4", max_score = 1)
    public void truthTest4() {
        String string = "awesome";
        assertThat(string).startsWith("awe");
        assertWithMessage("Without me, it's just aweso")
                .that(string)
                .contains("mes");
    }

    public static void main(String[] args) {
        AutograderRunner.run(new String[]{"--json"});
//        AutograderRunner.run(new String[]{});
//        AutograderRunner.run(new String[]{"--tree", "--outfile", "out.txt"});
    }
} 