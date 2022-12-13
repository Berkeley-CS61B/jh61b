package jh61b.grader;
import java.lang.reflect.Method;


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

        for (StackTraceElement frame : exception.getStackTrace ()) {
            if (frame.getClassName().startsWith ("org.junit.")) {
                continue;
            }
            sb.append(printPosition(frame));
            if (isStoppingFrame(frame)) {
                break;
            }
        }
        sb.append(System.lineSeparator());
        return sb.toString().replaceFirst("\\s+$", "");
    }

    /** Returns a string representation of the source position indicated by FRAME. */
    private static String printPosition(StackTraceElement frame) {

        if (frame.isNativeMethod())
            return String.format("    at %s.%s (native method)%n",
                    frame.getClassName (),
                    frame.getMethodName ());
        else
            return String.format("    at %s.%s:%d (%s)%n",
                    frame.getClassName (),
                    frame.getMethodName (),
                    frame.getLineNumber (),
                    frame.getFileName ());
    }

    /** True iff FRAME is positioned on a method with a junit @Test
     *  annotation. */
    private static boolean isStoppingFrame(StackTraceElement frame) {
        if (frame.isNativeMethod ())
            return false;
        try {
            Class<?> cls = Class.forName(frame.getClassName());
            Method mthd = cls.getMethod(frame.getMethodName());
            return mthd.getAnnotation(org.junit.Test.class) != null || mthd.getAnnotation(org.junit.jupiter.api.Test.class) != null;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            return false;
        }
    }
}