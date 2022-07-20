package it.unimib.core;

import com.beust.jcommander.JCommander;
import it.unimib.failure_locator.TestCasesExecutor;
import it.unimib.generator.RepairTargetGenerator;
import it.unimib.model.FailureInfo;
import it.unimib.model.RepairTarget;
import it.unimib.util.ExceptParametersCommander;
import it.unimib.util.SBFLRankingMergingUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;

import static it.unimib.util.Constants.APP_NAME;
import static it.unimib.util.Constants.EXCEPT_WORKING_DIR;

/**
 * Entry point of the app
 *
 */
public class ExceptMain {

    private static final Logger logger = Logger.getLogger(ExceptMain.class);

    public static void main(String[] args) {

        ExceptParametersCommander exceptParametersCommander = new ExceptParametersCommander();

        JCommander jCommander = JCommander.newBuilder().addObject(exceptParametersCommander).build();
        jCommander.setProgramName(APP_NAME);

        jCommander.parse(args);
        if (exceptParametersCommander.isHelp()) {
            jCommander.usage();
        } else {
            String programSourceCodePath = exceptParametersCommander.getSourceCodePath();
            String programClassesPath = exceptParametersCommander.getBuildClassesPath();
            String testsSourceCodePath = exceptParametersCommander.getSourceCodeTestsPath();
            String testsClassesPath = exceptParametersCommander.getBuildTestClassesPath();
            String dependencies = exceptParametersCommander.getDependencies();
            String output = exceptParametersCommander.getOutputDirectory();

            startAnalysisWithOnlyLocalization(programSourceCodePath, programClassesPath, testsSourceCodePath,
                    testsClassesPath, dependencies, output);
        }
    }

    public static void startAnalysisWithOnlyLocalization(String programSourceCodePath, String programClassesPath,
                                               String testsSourceCodePath, String testsClassesPath,
                                                         String dependencies, String outputFolder) {

        logger.info("Analysis started...");

        TestCasesExecutor testCasesExecutor = new TestCasesExecutor(programSourceCodePath, programClassesPath,
                testsSourceCodePath, testsClassesPath, dependencies);
        List<FailureInfo> failuresInformationList = testCasesExecutor.getFailuresInformation();

        Map<FailureInfo, List<RepairTarget>> repairTargetForFailureMap =
                RepairTargetGenerator.getRepairTargets(failuresInformationList, programSourceCodePath);

        for (Map.Entry<FailureInfo, List<RepairTarget>> set : repairTargetForFailureMap.entrySet()) {
            SBFLRankingMergingUtil.mergeRepairTargetsWithFlacocoResults(outputFolder, set.getKey(), set.getValue(),
                    testCasesExecutor.getFlacocoResult());
        }

        if (outputFolder == null) {
            outputFolder = EXCEPT_WORKING_DIR;
        }

        logger.info("Process ended: the output is available in the folder " +
                outputFolder);
    }

    public static List<RepairTarget> startAnalysisWithLocalizationAPI(String programSourceCodePath, String programClassesPath,
                                                         String testsSourceCodePath, String testsClassesPath, String dependencies) {
        logger.info("Analysis started...");

        TestCasesExecutor testCasesExecutor = new TestCasesExecutor(programSourceCodePath, programClassesPath, testsSourceCodePath,
                testsClassesPath, dependencies);
        List<FailureInfo> failuresInformationList = testCasesExecutor.getFailuresInformation();

        Map<FailureInfo, List<RepairTarget>> repairTargetForFailureMap =
                RepairTargetGenerator.getRepairTargets(failuresInformationList, programSourceCodePath);

        List<RepairTarget> repairTargetList = SBFLRankingMergingUtil.
                getRepairTargetsWithFlacocoResults(repairTargetForFailureMap, testCasesExecutor.getFlacocoResult());

        logger.info("Process ended");

        return repairTargetList;
    }

    public static Map<FailureInfo, List<RepairTarget>> getRepairTargetsMap(String programSourceCodePath, String programClassesPath,
                                                                           String testsSourceCodePath, String testsClassesPath,
                                                                           String dependencies) {
        logger.info("Analysis started...");

        TestCasesExecutor testCasesExecutor = new TestCasesExecutor(programSourceCodePath, programClassesPath, testsSourceCodePath,
                testsClassesPath, dependencies);
        List<FailureInfo> failuresInformationList = testCasesExecutor.getFailuresInformation();

        logger.info("Process ended");

        return RepairTargetGenerator.getRepairTargets(failuresInformationList, programSourceCodePath);
    }
}
