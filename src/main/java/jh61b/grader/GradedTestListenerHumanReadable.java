// adapted from http://memorynotfound.com/add-junit-listener-example/
// Highly redundant with GradedTestListenerJSON. Maybe refactor later.
// Also, should output go to StdErr? That's what Paul did.
// TODO: Make stack traces less onerous. See textui.java for ideas of how we might do this.
package jh61b.grader;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;


public class GradedTestListenerHumanReadable implements TestExecutionListener {

    /* Current test result. Created at the beginning of every test, completed at the
       end of every test. */
    private static TestResult currentTestResult;
    private int testsRun;
    private int testsFailed;
    private double totalScore = 0.0;
    private double maxScore = 0.0;

    /**
     * Returns the name of a test as stored in an annotation.
     */
    private static String getAnnotationString(Annotation x, String annotationStringName) throws
            IllegalAccessException, InvocationTargetException {
        Method[] methods = x.getClass().getDeclaredMethods();

        // If the annotation has a method name() that returns a String, invoke that method and return the result
        for (Method m : methods) {
            if (m.getName().equals(annotationStringName) &&
                    m.getReturnType().getCanonicalName().equals("java.lang.String")) {
                return (String) m.invoke(x);
            }
        }
        return "Uh-oh, getAnnotationString failed to get test String. This should never happen!";
    }

    /**
     * Returns the name of a test as stored in an annotation.
     */
    private static double getAnnotationDouble(Annotation x, String annotationDoubleName) throws
            IllegalAccessException, InvocationTargetException {
        Method[] methods = x.getClass().getDeclaredMethods();

        // If the annotation has a method name() that returns a double, invoke that method and return the result
        for (Method m : methods) {
            if (m.getName().equals(annotationDoubleName) &&
                    m.getReturnType().getCanonicalName().equals("double")) {
                return (double) m.invoke(x);
            }
        }
        return -31337;
    }

    /**
     * Gets test name of the given test.
     */
    private static String getTestName(GradedTest x) throws
            IllegalAccessException, InvocationTargetException {
        return getAnnotationString(x, "name");
    }

    /**
     * Gets test number of the given test.
     */
    private static String getTestNumber(GradedTest x) throws
            IllegalAccessException, InvocationTargetException {
        return getAnnotationString(x, "number");
    }

    /**
     * Gets test weight of the given test.
     */
    private static double getTestMaxScore(GradedTest x) throws
            IllegalAccessException, InvocationTargetException {
        return getAnnotationDouble(x, "max_score");
    }


    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        testsRun = 0;
        testsFailed = 0;
    }

    /* Code to run at the end of test run. */
    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        int numPassed = testsRun - testsFailed;
        System.out.printf("Passed: %d/%d tests.%n", numPassed, testsRun);
        System.out.printf("Score: %.3f/%.3f%n", totalScore, maxScore);
    }

    private GradedTest getGradedTestMethod(TestIdentifier testIdentifier) {
        if (testIdentifier.isContainer()) return null;

        TestSource testSource = testIdentifier.getSource().orElse(null);
        if (testSource == null) {
            return null;
        } else if (!(testSource instanceof MethodSource)) {
            return null;
        }

        Method testMethodSource = ((MethodSource) testSource).getJavaMethod();
        return findAnnotation(testMethodSource, GradedTest.class).orElse(null);
    }

    public void executionStarted(TestIdentifier testIdentifier) {
        GradedTest gradedTest = getGradedTestMethod(testIdentifier);
        if (gradedTest == null) {
            return;
        }

        try {
            String testName = getTestName(gradedTest);
            String testNumber = getTestNumber(gradedTest);
            double testMaxScore = getTestMaxScore(gradedTest);

            if (testIdentifier.getUniqueIdObject().getLastSegment().getType().contains("invocation")) {
                testNumber += testIdentifier.getUniqueIdObject().getLastSegment().getValue();
            }

            /* Capture StdOut (both ours and theirs) so that we can relay it to the students. */
            currentTestResult = new TestResult(testName, testNumber, testMaxScore);

            /* By default every test passes. */
            currentTestResult.setScore(testMaxScore);

            String testSummary = String.format("test %s: %s (%s)", testNumber, testName, testIdentifier.getDisplayName());
            System.out.println("Starting " + testSummary);

            maxScore += testMaxScore;
            testsRun++;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        GradedTest gradedTest = getGradedTestMethod(testIdentifier);
        if (gradedTest == null) {
            return;
        }
        try {
            // Check if test failed
            if (testExecutionResult.getStatus() == TestExecutionResult.Status.FAILED) {
                Throwable cause = testExecutionResult.getThrowable().orElse(null);
                currentTestResult.setScore(0);
                System.out.println("Test Failed!");
                if (cause != null) {
                    cause.printStackTrace(System.out);
                }
                totalScore += getTestMaxScore(gradedTest);
            } else {
                testsFailed++;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        System.out.printf("==> Score: %.3f / %.3f%n", currentTestResult.score, currentTestResult.maxScore);
        System.out.println("-".repeat(80));
    }
}
