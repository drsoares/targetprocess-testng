package pt.drsoares.plugins.targetprocess.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TestCaseRuns {
    @SerializedName("Items")
    public List<TestCaseItem> items;
}
