package pt.drsoares.plugins.targetprocess.domain;

import com.google.gson.annotations.SerializedName;

public class TestCase extends Item {
    @SerializedName("TestPlans")
    public TestPlans testPlans;
}
