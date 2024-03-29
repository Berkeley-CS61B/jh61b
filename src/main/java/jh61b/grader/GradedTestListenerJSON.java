package jh61b.grader;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GradedTestListenerJSON implements TestExecutionListener {
    // Storage of print output that has been intercepted.
    private CappedByteArrayOutputStream capturedData;

    // Tracks original stdout
    private final PrintStream STDOUT = System.out;
    private final PrintStream STDERR = System.err;

    // Current test result. Created at the beginning of every test, completed at the end of every test.
    private TestResult currentTestResult;

    // All test results.
    private List<TestResult> allTestResults;

    // File of all test results.
    private Path resultPath;

    // Test run start time.
    private long startTime;

    public void setResultPath(Path path) {
        this.resultPath = path;
    }

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        allTestResults = new ArrayList<>();

        // http://stackoverflow.com/questions/5936562/disable-system-err
//        System.setErr(new PrintStream(new OutputStream() {
//            public void write(int b) {
//            }
//        }));

        try {
            if (resultPath != null) {
                // Clear file
                Files.write(resultPath, new byte[0]);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        startTime = System.currentTimeMillis();
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        long elapsed = System.currentTimeMillis() - startTime;

        JsonObject json = new JsonObject();
        json.addProperty("execution_time", elapsed);

        JsonArray tests = new JsonArray();
        for (TestResult tr : allTestResults) {
            tests.add(tr.toJSON());
        }
        json.add("tests", tests);
        System.setOut(STDOUT);

        String pretty = (new GsonBuilder().setPrettyPrinting().create()).toJson(json);
        if (resultPath == null) {
            STDOUT.println(pretty);
        } else {
            try {
                Files.write(resultPath, pretty.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void executionStarted(TestIdentifier testIdentifier) {
        if (testIdentifier.isContainer()) {
            currentTestResult = null;
            return;
        }

        TestSource testSource = testIdentifier.getSource().orElse(null);
        if (testSource == null) {
            STDERR.println("Warning: " + testIdentifier.getDisplayName() + " has no test source!");
            currentTestResult = null;
            return;
        } else if (!(testSource instanceof MethodSource)) {
            STDERR.println("Warning: " + testIdentifier.getDisplayName() + " is not a method source!");
            currentTestResult = null;
            return;
        }

        Method testMethodSource = ((MethodSource) testSource).getJavaMethod();
        GradedTest gradedTest = findAnnotation(testMethodSource, GradedTest.class).orElse(null);
        if (gradedTest == null) {
            STDERR.println("Warning: " + testIdentifier.getDisplayName() + " is missing @GradedTest annotation!");
            currentTestResult = null;
            return;
        }

        String name = gradedTest.name();
        if (name.equals(GradedTest.DEFAULT_NAME)) {
            name = testIdentifier.getDisplayName();
        }

        String number = gradedTest.number();
        if (testIdentifier.getUniqueIdObject().getLastSegment().getType().contains("invocation")) {
            // Ugly hack to get around Gradescope's string ordering
            String invocation = testIdentifier.getUniqueIdObject().getLastSegment().getValue();
            if (invocation.startsWith("#") && invocation.length() == 2) {
                invocation = "#0" + invocation.substring(1);
            }
            number += invocation;
        }

        currentTestResult = new TestResult(name, number, gradedTest.max_score(), gradedTest.suppress_output());

        // Full score unless there's an explicit failure
        currentTestResult.setScore(gradedTest.max_score());

        // Capture stdout so that we can relay it to the students.
        capturedData = new CappedByteArrayOutputStream(gradedTest.max_output_length());
        System.setOut(new PrintStream(capturedData));
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if (testIdentifier.isContainer()) {
            if (testExecutionResult.getThrowable().isEmpty()) {
                return;
            }
            currentTestResult = new TestResult("Test Container", "-", 0, false);
        }

        if (currentTestResult == null) return;

        boolean failed = false;
        // Check if test failed
        if (testExecutionResult.getStatus() == TestExecutionResult.Status.FAILED) {
            Throwable cause = testExecutionResult.getThrowable().orElse(null);
            currentTestResult.setScore(0);
            currentTestResult.addOutput("Test Failed!" + System.lineSeparator());
            currentTestResult.addOutput("-".repeat(12) + System.lineSeparator());
            currentTestResult.addOutput(JUnitUtilities.throwableToString(cause));
            failed = true;
        }

        if (capturedData != null && capturedData.written()) {
            if (failed) {
                currentTestResult.addOutput(System.lineSeparator());
            }
            currentTestResult.addOutput("Output:\n");
            currentTestResult.addOutput(capturedData.toString());
            if (capturedData.truncated() && capturedData.getMaxSize() > 0) {
                currentTestResult.addOutput("\n... truncated due to excessive output!");
            }
        }
        allTestResults.add(currentTestResult);
    }
}