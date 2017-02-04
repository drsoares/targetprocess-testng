package pt.drsoares.pluggins.targetprocess.domain;

import com.google.gson.annotations.SerializedName;

public class TestPlanRun extends Item {
    @SerializedName("TestCaseRuns")
    public TestCaseRuns testCaseRuns;
}
