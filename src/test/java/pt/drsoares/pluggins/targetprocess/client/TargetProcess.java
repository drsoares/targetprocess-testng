package pt.drsoares.pluggins.targetprocess.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import pt.drsoares.pluggins.targetprocess.domain.TestCase;
import pt.drsoares.pluggins.targetprocess.domain.TestCaseResult;
import pt.drsoares.pluggins.targetprocess.domain.TestPlanRun;
import pt.drsoares.pluggins.targetprocess.domain.TestPlanRunRequest;

@Headers("Accept: application/json")
public interface TargetProcess {

    @RequestLine("GET /api/v1/TestCases/{testcaseid}?include=[TestPlans[ID]]")
    TestCase getTestCases(@Param("testcaseid") String testCaseid);

    @Headers("Content-Type: application/json")
    @RequestLine("POST /api/v1/TestPlanRuns?resultFormat=json&resultInclude=[Id,TestCaseRuns[Id]]")
    TestPlanRun createTestPlanRun(TestPlanRunRequest testPlanReq);

    @Headers("Content-Type: application/json")
    @RequestLine("POST /api/v1/TestCaseRuns/{testcaseid}")
    void testCaseRun(@Param("testcaseid") String testCaseId, TestCaseResult testCaseRun);

}
