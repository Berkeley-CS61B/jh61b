/**
 * Handy container class for storing test results.
 */
package jh61b.grader;

import com.google.gson.JsonObject;

public class TestResult {
    protected final String name;
    protected final String number;
    protected final double maxScore;
    protected double score;
    protected final boolean suppressOutput;

    // outputSB is any text that we want to relay to the user when the test is done running.
    private StringBuilder outputSB;

    /* private List<String> tags; // Not yet implemented */
    /* private String visibility; Can be always, published, or never. Not yet implemented. */

    public TestResult(String name, String number, double maxScore) {
        this.name = name;
        this.number = number;
        this.maxScore = maxScore;
        this.outputSB = new StringBuilder();
        this.suppressOutput = false;
    }

    public TestResult(String name, String number, double maxScore, boolean suppressOutput) {
        this.name = name;
        this.number = number;
        this.maxScore = maxScore;
        this.outputSB = new StringBuilder();
        this.suppressOutput = suppressOutput;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void addOutput(String x) {
        if (!suppressOutput) {
            outputSB.append(x);
        }
    }

    public JsonObject toJSON() {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", name);
        obj.addProperty("number", number);
        obj.addProperty("score", score);
        obj.addProperty("max_score", maxScore);
        obj.addProperty("output", outputSB.toString());
        return obj;
    }
}
