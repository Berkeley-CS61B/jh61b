package examples.junit5;

import jh61b.grader.AutograderRunner;
import jh61b.grader.GradedTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AGParameterizedTest {

    public static boolean isPalindrome(String candidate) {
        int length = candidate.length();
        for (int i = 0; i < length / 2; i++) {
            if (candidate.charAt(i) != candidate.charAt(length - (i + 1))) {
                return false;
            }
        }
        return true;
    }

    @ParameterizedTest
    @ValueSource(strings = {"racecar", "definitely not a palindrome"})
    @GradedTest(number = "1")
    void palindromes(String candidate) {
        System.out.println("It's very hard to get the parent context, so we don'.");
        assertTrue(isPalindrome(candidate));
    }

    @ParameterizedTest(name = "{index}: testing ''{0}'' palindromicity")
    @ValueSource(strings = {"racecar", "definitely not a palindrome"})
    @GradedTest(number = "2")
    void palindromes2(String candidate) {
        System.out.println("Use ParameterizedTest.name to give your tests meaningful descriptions!");
        assertTrue(isPalindrome(candidate));
    }

    public static void main(String[] args) {
        AutograderRunner.run(new String[]{"--json"});
    }
}
