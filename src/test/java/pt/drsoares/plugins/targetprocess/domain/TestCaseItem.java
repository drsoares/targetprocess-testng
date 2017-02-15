package pt.drsoares.plugins.targetprocess.domain;

import com.google.gson.annotations.SerializedName;

public class TestCaseItem extends Item {
    @SerializedName("TestCase")
    public TestCase testCase;
}
