package pt.drsoares.pluggins.targetprocess;

import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import pt.drsoares.pluggins.targetprocess.annotations.TestCase;
import pt.drsoares.pluggins.targetprocess.client.TargetProcess;
import pt.drsoares.pluggins.targetprocess.domain.*;
import pt.drsoares.pluggins.targetprocess.utils.Predicate;

public class TestCaseListener extends TestListenerAdapter {

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

    private static TargetProcess targetProcess = connect();

    private static final Predicate<ITestResult> IS_TARGET_PROCESS_TC = new Predicate<ITestResult>() {
        public boolean test(ITestResult result) {
            return result.getMethod()
                    .getConstructorOrMethod()
                    .getMethod()
                    .isAnnotationPresent(TestCase.class);
        }
    };

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        updateTestCaseInTargetProcess(tr, Result.SUCCESS);
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        super.onTestFailure(tr);
        updateTestCaseInTargetProcess(tr, Result.FAILURE);
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
        updateTestCaseInTargetProcess(tr, Result.SKIPPED);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult tr) {
        super.onTestFailedButWithinSuccessPercentage(tr);
        updateTestCaseInTargetProcess(tr, Result.FAILED_BUT_WITHIN_SUCCESS_PERCENTAGE);
    }

    private synchronized void updateTestCaseInTargetProcess(ITestResult tr, Result result) {
        if (IS_TARGET_PROCESS_TC.test(tr)) {
            handle(tr, result);
        }
    }

    private void handle(ITestResult tr, Result result) {

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
