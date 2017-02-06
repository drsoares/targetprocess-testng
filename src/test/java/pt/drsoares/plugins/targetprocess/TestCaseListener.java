package pt.drsoares.plugins.targetprocess;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import pt.drsoares.plugins.targetprocess.annotations.TestCase;
import pt.drsoares.plugins.targetprocess.client.TargetProcess;
import pt.drsoares.plugins.targetprocess.client.authentication.basic.BasicAuthenticationBuilder;
import pt.drsoares.plugins.targetprocess.client.authentication.token.TokenAuthenticationBuilder;
import pt.drsoares.plugins.targetprocess.domain.*;
import pt.drsoares.plugins.targetprocess.utils.Predicate;

import java.util.logging.Logger;

public class TestCaseListener extends TestListenerAdapter {

    private final static Logger LOG = Logger.getLogger(TestCaseListener.class.getName());

    private static TargetProcess targetProcess;

    static {

        String url = System.getProperty("targetProcessUrl");
        String username = System.getProperty("targetProcessUser");
        String password = System.getProperty("targetProcessPassword");
        String token = System.getProperty("targetProcessAuthToken");

        if (url != null) {
            if (username != null && password != null) {
                targetProcess = BasicAuthenticationBuilder.build(url, username, password);
            } else if (token != null) {
                targetProcess = TokenAuthenticationBuilder.build(url, token);
            }
        }
    }

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

        if (targetProcess != null) {

            String testPlanId = targetProcessTestCase.testPlan();

            if (testPlanId.isEmpty()) {
                pt.drsoares.plugins.targetprocess.domain.TestCase testCase = targetProcess.getTestCases(testCaseId);

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
        } else {
            LOG.fine("Not updating TC:" + testCaseId);
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
