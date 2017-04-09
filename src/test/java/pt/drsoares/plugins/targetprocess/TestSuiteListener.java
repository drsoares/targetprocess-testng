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

/**
 * TestSuiteListener is a TestNG Suite Listener, once the Integration Tests finish,
 * it will look for the annotated tests ( with pt.dr.soares.plugins.targetprocess.TestCase annotation) and
 * create a Target Process Test Case Run for them on a Target Process instance.
 *
 * @author Diogo Soares
 */
public class TestSuiteListener implements ISuiteListener {

    private static final Map<String, String> TEST_CASE_RUN_PER_TEST_CASE = new HashMap<>();

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

        if (TEST_CASE_RUN_PER_TEST_CASE.isEmpty()) {
            populateTestCaseRunPerTestCase(testCaseId, testPlanId);
        }

        String testCaseRunId = TEST_CASE_RUN_PER_TEST_CASE.get(testCaseId);

        if (testCaseRunId != null) {
            targetProcess.testCaseRun(testCaseRunId, result);
        }
    }

    private void populateTestCaseRunPerTestCase(String testCaseId, String testPlanId) {
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

    private void createTestPlanRun(TestPlan testPlan) {
        TestPlanRunRequest testPlanRunRequest = new TestPlanRunRequest();
        testPlanRunRequest.testPlan = testPlan;
        TestPlanRun testPlanRun = targetProcess.createTestPlanRun(testPlanRunRequest);

        for (TestCaseItem item : testPlanRun.testCaseRuns.items) {
            TEST_CASE_RUN_PER_TEST_CASE.put(item.testCase.id, item.id);
        }
    }
}
