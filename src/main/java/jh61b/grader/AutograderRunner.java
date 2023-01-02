package jh61b.grader;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.TagFilter;
import org.junit.platform.launcher.core.LauncherConfig;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

public class AutograderRunner {

    enum OutputFormat {
        JSON, PLAINTEXT
    }

    private OutputFormat outputFormat = OutputFormat.PLAINTEXT;
    private String outfile = null;
    private List<String> tags;

    public AutograderRunner(String[] args) {
        tags = new ArrayList<String>();
        parseArgs(args);
    }

    private void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--json":
                    outputFormat = OutputFormat.JSON;
                    break;
                case "--plaintext":
                    outputFormat = OutputFormat.PLAINTEXT;
                    break;
                case "--outfile":
                    outfile = args[++i];
                    break;
                case "--tag-expr":
                    tags.add(args[++i]);
            }
        }
    }

    public void startWithLauncher(LauncherDiscoveryRequest request) {
        // JUnit engines are added and present by default
        LauncherConfig.Builder launcherConfigBuilder = LauncherConfig.builder();
        if (outputFormat == OutputFormat.JSON) {
            GradedTestListenerJSON listener = new GradedTestListenerJSON();
            if (outfile != null) {
                listener.setResultPath(Paths.get(outfile));
            }
            launcherConfigBuilder.addTestExecutionListeners(listener);
        } else {
            launcherConfigBuilder.addTestExecutionListeners(new GradedTestListenerHumanReadable());
        }
        LauncherConfig config = launcherConfigBuilder.build();
        try (LauncherSession session = LauncherFactory.openSession(config)) {
            Launcher launcher = session.getLauncher();
            launcher.execute(request);
        }
    }

    /**
     * Simple test discovery by running all tests in provided class.
     * @param testClass class containing tests to run
     */
    private void start(Class<?> testClass) {
        LauncherDiscoveryRequestBuilder builder = LauncherDiscoveryRequestBuilder.request()
            .selectors(selectClass(testClass));
        if (!tags.isEmpty()) {
            builder.filters(TagFilter.includeTags(tags));
        }
        startWithLauncher(builder.build());
    }

    public static void run(String[] args) {
        run(args, 1);
    }

    /**
     * Run the autograder with the specified command line arguments.
     * <p>
     * This uses some magic to figure out the class of the main method that called this method,
     * so the usage is exactly the same for every AGTest*.java:
     * <p>
     * <pre>
     * public static void main(String[] args) {
     *     AutograderRunner.run(args);
     *     // Equivalently, AutograderRunner.run(args, 0);
     * }
     * </pre>
     * <p>
     * If you wrap this in a helper method to perform setup (such as inserting a {@link SecurityManager} or
     * {@link java.security.Policy}, make sure to increment <code>calls</code> by the number of calls
     * between the test class and the `run` call.
     *
     * @author Eli Lipsitz
     */
    public static void run(String[] args, int calls) {
        AutograderRunner runner = new AutograderRunner(args);
        // Find the class the appropriate distance up the callstack.
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        // 1 for `getStackTrace`, 1 for `run(String[], int)`
        String className = stackTraceElements[2 + calls].getClassName();
        try {
            Class<?> testClass = Class.forName(className);
            runner.start(testClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
