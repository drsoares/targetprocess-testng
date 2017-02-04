package pt.drsoares.plugins.targetprocess.domain;

import com.google.gson.annotations.SerializedName;

public class TestPlanRun extends Item {
    @SerializedName("TestCaseRuns")
    public TestCaseRuns testCaseRuns;
}
