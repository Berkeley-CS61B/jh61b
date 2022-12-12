package jh61b.junit;
import java.util.HashMap;

// The ProgramPerformanceLimits class was designed to simplify performance testing code by allowing
// performance limits to be specified using literal matrices.

// This class is suitable for creating both memory and timing tests. The constructor
// takes as input an array of fieldnames (given as Strings), and a table of limits (given as
// a 2D array of doubles). The test client can then query the ProgramPerformanceLimit class
// to see whether a particular test exists, can query whether a given piece of data obeys the PPL,
// and can request a reportString which provides a simple numerical description of how closely
// the student is to obeying the appropriate PPL.

// The architecture of the code is a little strange (particularly the constructor), but it's all in service of
// making it simple to specifiy operation and memory count limits as literals. See the method descriptions
// below for more details.


// While the ideal solution would be literal
// specifications of some sort of Hashmap of Hashmaps, literal specification of HashMaps is not possible
// with the current version of Java.


// Columns 2 through M represent the permissable lower and upper bounds for field #0 through #F-1.
// Column 2 represents the lower bound for field #0, and column 3 represents the upper bound for field #0.
// Column 4 represents the lower bound for field #1, and so forth.

// Once a ProgramPeformanceLimits object has been instantiated, the key methods that the client
// (usually TestMemoryOfXXX.java or TimeXXX.java) will utilize are:

// boolean testExists(double testID, String fieldName)
// boolean obeysLimits(double testID, String fieldName, double data)
// String reportString(double testID, String fieldName, double data)

// For more details on these methods, see the comments below. As an alternative to calling
// these methods directly, one can also use the PPLTestResult nested class, which performs all
// tests at once, and makes results available via separate (final) instance variables.
public class ProgramPerformanceLimits {
    private double[][] performanceLimits;
    private HashMap<String, Integer> fieldNameToColumnNumber;

    // The constructor takes an array of fieldnames of arbitrary length F, and a 2D array
    // of doubles of size N x M, where N may be any number, and M = 2*F + 1. The first column of the table
    // should be a unique identifier, which will typically the object under test. For example, for percolation,
    // the first column specifies the grid size.

    // An example PPL instantiation is given below:

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // String [] fields = {"time", "union", "findPlus2xConnected", "constructor"};

    // double [][] limits = {
    ////   N,         time,                  union,                  find                constructor
    //    {8,     0,      100,        10,         1500,       15,         1500,       1,         3},
    //    {32,    0,      100,        150,        15000,      150,        20000,      1,         3},
    //    {128,   0,      100,        2500,       220000,     3000,       300000,     1,         3},
    //    {512,   0,      100,        35000,      3600000,    50000,      5000000,    1,         3},
    //    {1024,  0,      100,        150000,     15000000,   200000,     20000000,   1,         3}
    //};

    //  ProgramPerformanceLimits ppl = new ProgramPerformanceLimits(fields, limits);
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    // Under the current (Fall 2012) assignment set, the only assignment
    // for which the first column is NOT a simple object size is 8Puzzle. For this assignment, the first
    // column is equal to the number in the relevant puzzle name (e.g. puzzle32.txt).

    public ProgramPerformanceLimits(String[] fieldnames, double[][] limits) {
        int numRows = limits.length;
        assert numRows > 0;

        int numFields = fieldnames.length;
        int numColumns = limits[0].length;

        int expectedNumberOfColumns = numFields * 2 + 1; //one for ID, two for each field.
        assert expectedNumberOfColumns == numColumns;

        // Create copies of current field name
        // Create map from field name to column number in performance matrix

        fieldNameToColumnNumber = new HashMap<String, Integer>();
        int columnOfCurrentField = 1;

        for (int i = 0; i < fieldnames.length; i++) {
            fieldNameToColumnNumber.put(fieldnames[i], columnOfCurrentField);
            columnOfCurrentField += 2;
        }

        // copy the performance matrix
        performanceLimits = new double[numRows][numColumns];
        for (int r = 0; r < numRows; r++)
            for (int c = 0; c < numColumns; c++)
                performanceLimits[r][c] = limits[r][c];
    }

    // Determines whether or not a given test exists.
    public boolean testExists(double testID, String fieldName) {
        boolean rowExists = false;
        int rowNum = testIDToRow(testID);
        if (rowNum >= 0)
            rowExists = true;
        boolean columnExists = fieldNameToColumnNumber.containsKey(fieldName);
        return columnExists && rowExists;
    }

    // Determines whether data lies within the bounds given in the PPL. If the
    // error factor given by reportString rounds to 1.0, then the student is
    // considered to have passed.
    public boolean obeysLimits(double testID, String fieldName, double data) {
        double[] limits = getLimits(testID, fieldName);

        double minValue = limits[0];
        double maxValue = limits[1];

        if ((data <= maxValue) && (data >= minValue))
            return true;

        String rs = reportString(testID, fieldName, data);
        return rs.equals("        ");
    }

    // Returns a string which represents the factor by which the student
    // overshot (or undershot) the bounds given in this PPL. If
    // the student is within the appropriate bounds, a blank string
    // consisting of only spaces is returned.

    public String reportString(double testID, String fieldName, double data) {
        double[] limits = getLimits(testID, fieldName);

        double minValue = limits[0];
        double maxValue = limits[1];

        String rs;
        if      (data > maxValue) rs = floatingShorter("(%-3.1#x)", data / maxValue);
        else if (data < minValue) rs = floatingShorter("(%-3.1#x)", data / minValue);
        else                      rs = "        ";

        // This is a bit of a hack. If the error factor rounds to 1.0, then
        // the student is considered to have passed the test.
        if (rs.equals("(1.0x)"))
            rs = "        ";

        return rs;
    }

    private int testIDToRow(double testID) {
        int numRows = performanceLimits.length;
        int returnValue = -1;

        for (int r = 0; r < numRows; r++)
            if (performanceLimits[r][0] == testID)
                returnValue = r;

        return returnValue;
    }


    private int[] getRowAndColumn(double testID, String fieldName) {
        int rowNum = testIDToRow(testID);
        if (!fieldNameToColumnNumber.containsKey(fieldName))
            throw new RuntimeException("fieldName " + fieldName + " does not exist in program performance limit table!");
        int colNum = fieldNameToColumnNumber.get(fieldName);
        return new int[] {rowNum, colNum};
    }

    private double[] getLimits(double testID, String fieldName) {
        int[] rowAndCol = getRowAndColumn(testID, fieldName);
        int rowNum = rowAndCol[0];
        int colNum = rowAndCol[1];

        double minValue = performanceLimits[rowNum][colNum];
        double maxValue = performanceLimits[rowNum][colNum+1];

        return new double[]{minValue, maxValue};
    }

    public double[] getTestIDs() {
        double [] testIDs = new double[performanceLimits.length];
        for (int i = 0; i < performanceLimits.length; i++) {
            testIDs[i] = performanceLimits[i][0];
        }
        return testIDs;
    }

    // This function outputs a double in the shorter of two formats, where
    // the two possible formats are %f and %g. The formatString is a
    // specially formatted string, where the # character represents the
    // position where the f or g should appear.

    // For example, floatingShorter("%.2#", someDouble) would return the shorter of
    // String.format("%.2f", someDouble)   and   String.format("%.2g", someDouble)

    public static String floatingShorter(String formatString, double value) {
        String firstFormatString = formatString.replace('#', 'f');
        String secondFormatString = formatString.replace('#', 'g');
        String firstString = String.format(firstFormatString, value);
        if (value == 0.0) //%g crashes with zero
            return firstString;

        String secondString = String.format(secondFormatString, value);

        if (firstString.length() <= secondString.length())
            return firstString;
        else
            return secondString;
    }


    // ProgramPerformanceLimits.PPLTestResult provides a convenient class for storing pertinentd
    // ProgramPerformanceLimits test results.
    // Effectively, this class avoids the need for the user to explicitly call testExists(),
    // obeysLimits(), and reportString(). It is particularly useful for when only a single
    // field is used for generating the entire output of a test, e.g. most memory tests.

    public static class PPLTestResult {
        public final String passString;
        public final String errorFactor;
        public final boolean testExists;
        public final boolean passed;

        public PPLTestResult(ProgramPerformanceLimits ppl, String fieldName, double testID, double data) {
            if (ppl.testExists(testID, fieldName)) {
                testExists = true;

                if (ppl.obeysLimits(testID, fieldName, data)) {
                    passString = "=> passed";
                    passed = true;
                }
                else {
                    passString = "=> FAILED";
                    passed = false;
                }

                errorFactor = ppl.reportString(testID, fieldName, data);
            }
            else {
                passString = "=> Error in grading script. Grading script requested nonexistent test where" + fieldName + "=" + testID;
                errorFactor = "[ERROR]";
                passed = false;
                testExists = false;
            }
        }
    }

}

