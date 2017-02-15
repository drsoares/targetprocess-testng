package pt.drsoares.plugins.targetprocess;

import feign.auth.BasicAuthRequestInterceptor;
import org.testng.IInvokedMethod;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestResult;
import pt.drsoares.plugins.targetprocess.annotations.TestCase;
import pt.drsoares.plugins.targetprocess.client.TargetProcess;
import pt.drsoares.plugins.targetprocess.client.TargetProcessBuilder;
import pt.drsoares.plugins.targetprocess.client.authentication.token.TokenAuthRequestInterceptor;
import pt.drsoares.plugins.targetprocess.domain.*;
import pt.drsoares.plugins.targetprocess.utils.Builder;
import pt.drsoares.plugins.targetprocess.utils.Predicate;

import java.util.HashMap;
import java.util.Map;

public class TestSuiteListener implements ISuiteListener {

    private static final Map<String, String> TPR_PER_TC = new HashMap<>();

    private static TargetProcess targetProcess;
    private static boolean skipMode = false;

    static {

        String url = System.getProperty("targetProcessUrl");
        String username = System.getProperty("targetProcessUser");
        String password = System.getProperty("targetProcessPassword");
        String token = System.getProperty("targetProcessAuthToken");

        Builder<TargetProcess> builder = null;

        if (url != null) {
            if (username != null && password != null) {
                builder = new TargetProcessBuilder(url, new BasicAuthRequestInterceptor(username, password));
            } else if (token != null) {
                builder = new TargetProcessBuilder(url, new TokenAuthRequestInterceptor(token));
            }
        }

        if (builder != null) {
            targetProcess = builder.build();
        } else {
            skipMode = true;
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

    public void onStart(ISuite suite) {

    }

    public void onFinish(ISuite suite) {
        if (!skipMode) {
            for (IInvokedMethod testNGMethod : suite.getAllInvokedMethods()) {
                updateTestCaseInTargetProcess(testNGMethod.getTestResult());
            }
        }
    }

    private void updateTestCaseInTargetProcess(ITestResult tr) {
        if (IS_TARGET_PROCESS_TC.test(tr)) {
            handle(tr);
        }
    }

    private void handle(ITestResult testResult) {

        TestCase targetProcessTestCase = testResult.getMethod()
                .getConstructorOrMethod()
                .getMethod()
                .getAnnotation(TestCase.class);

        TestCaseResult result = Result.getTestCaseResult(testResult.getStatus());

        String testCaseId = targetProcessTestCase.id();

        String testPlanId = targetProcessTestCase.testPlan();

        if (TPR_PER_TC.isEmpty()) {
            if (testPlanId.isEmpty()) {
                pt.drsoares.plugins.targetprocess.domain.TestCase testCase = targetProcess.getTestCases(testCaseId);

                for (Item testPlanItem : testCase.testPlans.items) {
                    TestPlan testPlan = new TestPlan();
                    testPlan.id = testPlanItem.id;
                    createTestPlanRun(testPlan);
                }
            } else {
                TestPlan testPlan = new TestPlan();
                testPlan.id = testPlanId;
                createTestPlanRun(testPlan);
            }
        }

        String testCaseRunId = TPR_PER_TC.get(testCaseId);

        if (testCaseRunId != null) {
            targetProcess.testCaseRun(testCaseRunId, result);
        }
    }

    private static void createTestPlanRun(TestPlan testPlan) {
        TestPlanRunRequest testPlanRunRequest = new TestPlanRunRequest();
        testPlanRunRequest.testPlan = testPlan;
        TestPlanRun testPlanRun = targetProcess.createTestPlanRun(testPlanRunRequest);

        for (TestCaseItem item : testPlanRun.testCaseRuns.items) {
            TPR_PER_TC.put(item.testCase.id, item.id);
        }
    }
}
