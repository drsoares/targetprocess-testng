package pt.drsoares.pluggins.targetprocess;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import pt.drsoares.pluggins.targetprocess.annotations.TestCase;
import pt.drsoares.pluggins.targetprocess.domain.Result;
import pt.drsoares.pluggins.targetprocess.utils.Predicate;

public class TestCaseListener extends TestListenerAdapter {

    private static TestResultDelegate testResultDelegate = new TestResultDelegate();

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
            testResultDelegate.handle(tr, result);
        }
    }
}
