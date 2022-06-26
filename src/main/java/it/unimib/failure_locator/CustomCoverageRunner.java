package it.unimib.failure_locator;

import eu.stamp_project.testrunner.listener.CoveredTestResultPerTestMethod;
import eu.stamp_project.testrunner.runner.Failure;
import fr.spoonlabs.flacoco.core.config.FlacocoConfig;
import fr.spoonlabs.flacoco.core.coverage.CoverageFromSingleTestUnit;
import fr.spoonlabs.flacoco.core.coverage.CoverageMatrix;
import fr.spoonlabs.flacoco.core.coverage.CoverageRunner;
import fr.spoonlabs.flacoco.core.test.TestContext;
import fr.spoonlabs.flacoco.core.test.method.TestMethod;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class CustomCoverageRunner extends CoverageRunner {

    private final FlacocoConfig config;
    private final Logger logger = Logger.getLogger(CustomCoverageRunner.class);
    private final List<Failure> failedTests;

    public CustomCoverageRunner(FlacocoConfig config) {
        super(config);
        this.config = config;
        this.failedTests = new ArrayList<>();
    }

    public List<Failure> getFailedTests() {
        return this.failedTests;
    }

    @Override
    public CoverageMatrix getCoverageMatrix(List<TestContext> testContexts) {
        // This matrix stores the results: the execution of tests and the coverage of
        // that execution on each line
        CoverageMatrix matrixExecutionResult = new CoverageMatrix(config);

        Set<String> testClasses = testContexts.stream().map(TestContext::getTestMethods).flatMap(List::stream)
                .map(TestMethod::getFullyQualifiedClassName).collect(Collectors.toSet());

        // For each test context
        int executedTests = 0;
        int testsFound = 0;
        for (TestContext testContext : testContexts) {
            this.logger.debug("Running " + testContext);

            try {
                // We run the test cases according to the specific test framework strategy
                CoveredTestResultPerTestMethod result = testContext.getTestFrameworkStrategy().execute(testContext);

                failedTests.addAll(result.getFailingTests());

                // Process each method individually
                for (TestMethod testMethod : testContext.getTestMethods()) {
                    testsFound++;

                    if (result.getCoverageResultsMap().containsKey(testMethod.getFullyQualifiedMethodName())) {
                        matrixExecutionResult.processSingleTest(new CoverageFromSingleTestUnit(testMethod, result),
                                testClasses);
                        executedTests++;
                    } else {
                        this.logger.warn("Test " + testMethod + " result was not reported by test-runner.");
                    }
                }
            } catch (TimeoutException e) {
                this.logger.error(e);
            }
        }

        this.logger.info("Tests found: " + testsFound);
        this.logger.info("Tests executed: " + executedTests);
        return matrixExecutionResult;
    }
}
