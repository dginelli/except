package it.unimib.failure_locator;

import eu.stamp_project.testrunner.runner.Failure;
import fr.spoonlabs.flacoco.api.result.FlacocoResult;
import fr.spoonlabs.flacoco.api.result.Location;
import fr.spoonlabs.flacoco.api.result.Suspiciousness;
import fr.spoonlabs.flacoco.core.config.FlacocoConfig;
import fr.spoonlabs.flacoco.core.coverage.CoverageMatrix;
import fr.spoonlabs.flacoco.core.test.TestContext;
import fr.spoonlabs.flacoco.core.test.TestDetector;
import fr.spoonlabs.flacoco.localization.spectrum.SpectrumRunner;
import fr.spoonlabs.flacoco.localization.spectrum.SpectrumSuspiciousComputation;
import fr.spoonlabs.flacoco.utils.spoon.SpoonConverter;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class CustomSpectrumRunner extends SpectrumRunner {

    private final Logger logger = Logger.getLogger(CustomSpectrumRunner.class);
    private final FlacocoConfig config;
    private List<Failure> failedTests;

    public CustomSpectrumRunner(FlacocoConfig config) {
        super(config);
        this.config = config;
    }

    public List<Failure> getFailedTests() {
        return this.failedTests;
    }

    @Override
    public FlacocoResult run() {
        FlacocoResult result = new FlacocoResult();
        CoverageMatrix coverageMatrix = this.computeCoverageMatrix();
        result.setFailingTests(coverageMatrix.getFailingTestCases());
        SpectrumSuspiciousComputation ssc = new SpectrumSuspiciousComputation(this.config);
        Map<Location, Suspiciousness> defaultMapping = ssc.calculateSuspicious(coverageMatrix, this.config.getSpectrumFormula().getFormula());
        result.setDefaultSuspiciousnessMap(defaultMapping);
        if (this.config.isComputeSpoonResults()) {
            result = (new SpoonConverter(this.config)).convertResult(result);
        }

        return result;
    }

    private CoverageMatrix computeCoverageMatrix() {
        this.logger.debug("Running custom spectrum-based fault localization...");
        this.logger.debug(this.config);
        TestDetector testDetector = new TestDetector(this.config);
        List<TestContext> tests = testDetector.getTests();
        CustomCoverageRunner detector = new CustomCoverageRunner(this.config);
        CoverageMatrix coverageMatrix = detector.getCoverageMatrix(tests);
        this.failedTests = detector.getFailedTests();
        return coverageMatrix;
    }
}
