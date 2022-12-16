package jh61b.grader;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;


public class JUnitUtilities {

    public static String throwableToString(Throwable exception) {
        if (exception == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        if (exception instanceof AssertionError) {
            if (exception.getMessage() == null)
                sb.append("Assertion failed with no message");
            else {
                sb.append(String.format("%s%n", exception.getMessage()));
                if (exception.getMessage().startsWith("Expected exception:")) {
                    return sb.toString();
                }
            }
        } else {
            if (exception.getCause() != null) {
                exception = exception.getCause();
            }
            sb.append(String.format("%s%n", exception));
        }

        frameWalk:
        for (StackTraceElement frame : exception.getStackTrace()) {
            for (String exclude : STACK_TRACE_EXCLUDE) {
                String cleaned = frame.toString().replaceFirst("^" + Pattern.quote(frame.getModuleName() + "/"), "");
                if (cleaned.startsWith(exclude)) {
                    continue frameWalk;
                }
            }
            sb.append(printPosition(frame));
            if (isStoppingFrame(frame)) {
                break;
            }
        }
        return sb.toString().replaceFirst("\\s+$", "");
    }

    /**
     * Returns a string representation of the source position indicated by FRAME.
     */
    private static String printPosition(StackTraceElement frame) {

        if (frame.isNativeMethod()) {
            return String.format("    at %s.%s (native method)%n",
                    frame.getClassName(),
                    frame.getMethodName());
        } else {
            return String.format("    at %s.%s:%d (%s)%n",
                    frame.getClassName(),
                    frame.getMethodName(),
                    frame.getLineNumber(),
                    frame.getFileName());
        }
    }

    /**
     * True iff FRAME is positioned on a method with a junit @Test
     * annotation.
     */
    private static boolean isStoppingFrame(StackTraceElement frame) {
        if (frame.isNativeMethod())
            return false;
        try {
            Class<?> cls = Class.forName(frame.getClassName());
            Method mthd = cls.getMethod(frame.getMethodName());
            return mthd.getAnnotation(org.junit.Test.class) != null || mthd.getAnnotation(org.junit.jupiter.api.Test.class) != null;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            return false;
        }
    }

    public static final List<String> STACK_TRACE_EXCLUDE = List.of( // region exclusions
            "jh61b.grader.AutograderRunner.",
            "[[Reflective call:",
            "[[Testing framework:",
            "java.util.stream.AbstractPipeline.copyInto(",
            "java.util.stream.AbstractPipeline.evaluate(",
            "java.util.stream.AbstractPipeline.wrapAndCopyInto(",
            "java.util.stream.DistinctOps",
            "java.util.stream.FindOps",
            "java.util.stream.ForEachOps",
            "java.util.stream.MatchOps",
            "java.util.stream.ReduceOps",
            "java.util.stream.ReferencePipeline",
            "java.util.stream.SliceOps",
            "java.util.stream.WhileOps",
            "java.util.ArrayList$ArrayListSpliterator",
            "java.util.concurrent.",
            "java.util.concurrent.CompletableFuture$AsyncSupply.run(",
            "java.util.concurrent.Executors$",
            "java.util.concurrent.FutureTask.",
            "java.util.concurrent.ThreadPoolExecutor",
            "java.util.Spliterators$",
            "junit.framework.Assert.assert",
            "junit.framework.Assert.fail",
            "junit.framework.TestCase.assert",
            "junit.framework.TestCase.run",
            "junit.framework.TestResult",
            "junit.framework.TestSuite.run",
            "org.junit.Assert.assert",
            "org.junit.Assert.fail(",
            "org.junit.Assert.failNotSame(",
            "org.junit.Assert.failSame(",
            "org.junit.Assert.internalArrayEquals(",
            "org.junit.internal.ComparisonCriteria.arrayEquals(",
            "org.junit.internal.runners.",
            "org.junit.jupiter.",
            "org.junit.platform.",
            "org.junit.rules.",
            "org.junit.runner.JUnitCore.",
            "org.junit.runners.",
            "org.junit.vintage.",
            "org.mockito.internal.",
            "org.testng.internal.",
            "org.testng.SuiteRunner.",
            "org.testng.TestNG.run",
            "org.testng.TestRunner.",
            "java.awt.Component.dispatchEvent(",
            "java.awt.Component.dispatchEventImpl(",
            "java.awt.Component.processEvent(",
            "java.awt.Component.processMouseEvent(",
            "java.awt.Container.dispatchEventImpl(",
            "java.awt.Container.dispatchEventImpl(",
            "java.awt.Container.processEvent(",
            "java.awt.event.InvocationEvent.dispatch(",
            "java.awt.EventDispatchThread",
            "java.awt.EventQueue",
            "java.awt.EventQueue.dispatchEventImpl(",
            "java.awt.LightweightDispatcher.",
            "java.awt.Window.dispatchEventImpl(",
            "jdk.internal",
            "java.rmi.",
            "java.security.AccessControlContext$1.doIntersectionPrivilege(",
            "java.security.AccessController.doPrivileged(",
            "java.security.AccessController.doPrivileged(Native Method)",
            "java.security.ProtectionDomain$1.doIntersectionPrivilege(",
            "java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(",
            "javax.swing.AbstractButton$Handler.actionPerformed(",
            "javax.swing.AbstractButton.doClick(",
            "javax.swing.AbstractButton.fireActionPerformed(",
            "javax.swing.DefaultButtonModel.fireActionPerformed(",
            "javax.swing.DefaultButtonModel.setPressed(",
            "javax.swing.JComponent.processMouseEvent(",
            "javax.swing.plaf.basic.BasicComboPopup$Handler.mouseReleased(",
            "javax.swing.plaf.basic.BasicMenuItemUI",
            "java.lang.reflect.Constructor.newInstance(",
            "java.lang.reflect.Method.invoke(",
            "java.lang.Thread.run("
    );  // endregion
}