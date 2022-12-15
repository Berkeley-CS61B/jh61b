package examples;

import jh61b.grader.GradedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.google.common.truth.Truth.assertThat;

public interface SampleInterface {
    @Test
    @GradedTest(name = "Interface test", number = "i001", max_score = 1)
    default void interfaceTest() {
        assertThat(true).isTrue();
    }
    @ParameterizedTest
    @CsvSource({
            "apple,         1",
            "banana,        2",
            "'lemon, lime', 0xF1",
            "strawberry,    700_000"
    })
    @GradedTest(name = "Parameterized interface Test", number = "pi001", max_score = 1)
    default void interfaceTest(String fruit, int rank) {
        assertThat(fruit).isNotNull();
        assertThat(rank).isNotEqualTo(0);
    }
}
