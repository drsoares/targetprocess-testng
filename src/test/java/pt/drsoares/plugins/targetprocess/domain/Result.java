package pt.drsoares.plugins.targetprocess.domain;

public enum Result {
    SUCCESS(new TestCaseResult("Passed", "Test Passed")),
    SKIPPED(new TestCaseResult("NotRun", "Test Skipped")),
    FAILURE(new TestCaseResult("Failed", "Test Failed")),
    FAILED_BUT_WITHIN_SUCCESS_PERCENTAGE(new TestCaseResult("Failed", "Test Failed, but it was within success percentage"));

    private TestCaseResult testCaseResult;

    Result(TestCaseResult testCaseResult) {
        this.testCaseResult = testCaseResult;
    }

    public TestCaseResult getTestCaseResult() {
        return testCaseResult;
    }
}
