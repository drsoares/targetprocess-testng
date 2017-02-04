package pt.drsoares.pluggins.targetprocess;

import feign.*;
import feign.auth.BasicAuthRequestInterceptor;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import org.testng.ITestResult;
import pt.drsoares.pluggins.targetprocess.annotations.TestCase;
import pt.drsoares.pluggins.targetprocess.client.TargetProcess;
import pt.drsoares.pluggins.targetprocess.domain.*;

public class TestResultDelegate {

    private static TargetProcess targetProcess = connect();

    private static TargetProcess connect() {
        String url = System.getProperty("targetProcessUrl");
        String username = System.getProperty("targetProcessUser");
        String password = System.getProperty("targetProcessPassword");

        return Feign.builder()
                .decoder(new GsonDecoder())
                .encoder(new GsonEncoder())
                .requestInterceptor(new BasicAuthRequestInterceptor(username, password))
                .target(TargetProcess.class, url);
    }

    public void handle(ITestResult tr, Result result) {

        TestCase targetProcessTestCase = tr.getMethod()
                .getConstructorOrMethod()
                .getMethod()
                .getAnnotation(TestCase.class);

        String testCaseId = targetProcessTestCase.id();

        String testPlanId = targetProcessTestCase.testPlan();

        if (testPlanId.isEmpty()) {
            pt.drsoares.pluggins.targetprocess.domain.TestCase testCase = targetProcess.getTestCases(testCaseId);

            for (Item testPlanItem : testCase.testPlans.items) {
                TestPlan testPlan = new TestPlan();
                testPlan.id = testPlanItem.id;
                runTestPlan(result, testPlan);
            }
        } else {
            TestPlan testPlan = new TestPlan();
            testPlan.id = testPlanId;
            runTestPlan(result, testPlan);
        }

    }

    private void runTestPlan(Result result, TestPlan testPlan) {
        TestPlanRunRequest testPlanRunRequest = new TestPlanRunRequest();
        testPlanRunRequest.testPlan = testPlan;
        TestPlanRun testPlanRun = targetProcess.createTestPlanRun(testPlanRunRequest);
        for (Item testCaseRunItem : testPlanRun.testCaseRuns.items) {
            targetProcess.testCaseRun(testCaseRunItem.id, result.getTestCaseResult());
        }
    }
}
