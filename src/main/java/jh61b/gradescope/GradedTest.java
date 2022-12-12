package jh61b.gradescope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>GradedTest</code> annotation allows you to specify optional parameters:
 * <li><code>name</code>: String that specifies the name of the test.</li>
 * <li><code>number</code>: String that specifies the number of the test.</li>
 * <li><code>max_score</code>: Double that specifies the number of points that the test is worth.</li>
 * <li><code>max_output_length</code>: bytes of output to display before it's truncated</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface GradedTest {
    String name() default "Unnamed test";

    String number() default "Unnumbered test";

    double max_score() default 1.0;

    int max_output_length() default 2000;

    boolean suppress_output() default false;
}
