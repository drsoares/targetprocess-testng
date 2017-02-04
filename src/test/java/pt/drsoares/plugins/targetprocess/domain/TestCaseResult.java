package pt.drsoares.plugins.targetprocess.domain;

public class TestCaseResult {

    public String status;
    public String comment;

    public TestCaseResult(String status, String comment) {
        this.status = status;
        this.comment = comment;
    }
}
