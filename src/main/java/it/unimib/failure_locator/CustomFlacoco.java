package it.unimib.failure_locator;

import eu.stamp_project.testrunner.runner.Failure;
import fr.spoonlabs.flacoco.api.Flacoco;
import fr.spoonlabs.flacoco.api.result.FlacocoResult;
import fr.spoonlabs.flacoco.core.config.FlacocoConfig;
import fr.spoonlabs.flacoco.localization.FaultLocalizationRunner;
import org.apache.log4j.Logger;

import java.util.List;

public class CustomFlacoco extends Flacoco {

    private final Logger logger = Logger.getLogger(CustomFlacoco.class);
    private final FlacocoConfig config;
    private CustomSpectrumRunner customSpectrumRunner;

    public CustomFlacoco(FlacocoConfig config) {
        super(config);
        this.config = config;
    }

    @Override
    public FlacocoResult run() {
        this.logger.info("Running Fault Localization...");
        return this.getRunner().run();
    }

    public List<Failure> getFailedTests() {
        return this.customSpectrumRunner.getFailedTests();
    }

    private FaultLocalizationRunner getRunner() {
        if (this.config.getFamily() == FlacocoConfig.FaultLocalizationFamily.SPECTRUM_BASED) {
            this.customSpectrumRunner = new CustomSpectrumRunner(this.config);
            return this.customSpectrumRunner;
        }
        return null;
    }
}
