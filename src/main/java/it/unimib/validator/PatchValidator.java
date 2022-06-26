package it.unimib.validator;

import eu.stamp_project.testrunner.listener.CoveredTestResultPerTestMethod;
import eu.stamp_project.testrunner.runner.Failure;
import fr.spoonlabs.flacoco.core.config.FlacocoConfig;
import fr.spoonlabs.flacoco.core.test.TestContext;
import fr.spoonlabs.flacoco.core.test.strategies.testrunner.TestRunnerStrategy;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public class PatchValidator {

    private final FlacocoConfig flacocoConfig;

    public PatchValidator(FlacocoConfig flacocoConfig) {
        this.flacocoConfig = flacocoConfig;
    }

    public boolean runTestCases(String failingTestClass, String failingTestMethod, int failingTestNumber) {
        List<TestContext> testContexts = new TestRunnerStrategy(flacocoConfig).findTests();

        for (TestContext testContext : testContexts) {
            try {
                CoveredTestResultPerTestMethod result = testContext.getTestFrameworkStrategy().execute(testContext);

                Set<Failure> failures = result.getFailingTests();

                for (Failure failure : failures) {

                    if ((failure.testClassName.equals(failingTestClass) &&
                            failure.testCaseName.equals(failingTestMethod)) ||
                            failures.size() >= failingTestNumber) {
                        return false;
                    }
                }
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
