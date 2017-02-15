package pt.drsoares.plugins.targetprocess.domain;

import java.util.HashMap;
import java.util.Map;

public enum Result {

    SUCCESS(1, new TestCaseResult("Passed", "Test Passed")),

    SKIPPED(2, new TestCaseResult("NotRun", "Test Skipped")),

    FAILURE(3, new TestCaseResult("Failed", "Test Failed")),

    FAILED_BUT_WITHIN_SUCCESS_PERCENTAGE(4, new TestCaseResult("Failed", "Test Failed, but it was within success percentage"));

    private int status;
    private TestCaseResult testCaseResult;

    Result(int status, TestCaseResult testCaseResult) {
        this.status = status;
        this.testCaseResult = testCaseResult;
    }

    private static final Map<Integer, TestCaseResult> statuses = new HashMap<>();

    static {
        for (Result result : values()) {
            statuses.put(result.status, result.testCaseResult);
        }
    }

    public static TestCaseResult getTestCaseResult(int status) {
        return statuses.get(status);
    }
}
