package pt.drsoares.plugins.targetprocess.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import pt.drsoares.plugins.targetprocess.domain.TestCase;
import pt.drsoares.plugins.targetprocess.domain.TestCaseResult;
import pt.drsoares.plugins.targetprocess.domain.TestPlanRun;
import pt.drsoares.plugins.targetprocess.domain.TestPlanRunRequest;

@Headers("Accept: application/json")
public interface TargetProcess {

    @RequestLine("GET /api/v1/TestCases/{testCaseId}?include=[TestPlans[Id]]")
    TestCase getTestCases(@Param("testCaseId") String testCaseId);

    @Headers("Content-Type: application/json")
    @RequestLine("POST /api/v1/TestPlanRuns?resultInclude=[Id,TestCaseRuns[Id,TestCase[Id]]]")
    TestPlanRun createTestPlanRun(TestPlanRunRequest testPlanReq);

    @Headers("Content-Type: application/json")
    @RequestLine("POST /api/v1/TestCaseRuns/{testCaseRunId}")
    void testCaseRun(@Param("testCaseRunId") String testCaseRunId, TestCaseResult testCaseRun);

}
