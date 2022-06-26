package it.unimib.failure_locator;

import fr.spoonlabs.flacoco.api.result.FlacocoResult;
import fr.spoonlabs.flacoco.core.config.FlacocoConfig;
import fr.spoonlabs.flacoco.localization.spectrum.SpectrumFormula;
import it.unimib.model.FailureInfo;
import it.unimib.util.FailureAnalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestCasesExecutor {

    private final String programSourceCodePath;
    private final String programClassesPath;
    private final String testsSourceCodePath;
    private final String testsClassesPath;
    private String dependencies;

    private FlacocoConfig flacocoConfig;
    private FlacocoResult flacocoResult;

    public TestCasesExecutor(String programSourceCodePath, String programClassesPath, String testsSourceCodePath,
                             String testsClassesPath, String dependencies) {

        this.programSourceCodePath = programSourceCodePath;
        this.programClassesPath = programClassesPath;
        this.testsSourceCodePath = testsSourceCodePath;
        this.testsClassesPath = testsClassesPath;

        setDependencies(dependencies);
        setFlacocoConfiguration();
    }

    private void setDependencies(String dependencies) {
        if (dependencies != null) {
            StringBuilder formattedDependencies = new StringBuilder();
            try {
                List<Path> files = Files.walk(Paths.get(dependencies)).filter(Files::isRegularFile).collect(Collectors.toList());
                for (int i = 0; i < files.size(); i++) {
                    formattedDependencies.append(files.get(i)).append(File.pathSeparator);
                }
                if (formattedDependencies.substring(formattedDependencies.length()-1).equals(File.pathSeparator)) {
                    this.dependencies = formattedDependencies.substring(0, formattedDependencies.length() - 1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setFlacocoConfiguration() {
        flacocoConfig = new FlacocoConfig();

        List<String> srcJavaDir = new ArrayList<>();
        List<String> srcTestDir = new ArrayList<>();
        List<String> binJavaDir = new ArrayList<>();
        List<String> binTestDir = new ArrayList<>();

        srcJavaDir.add(programSourceCodePath);
        srcTestDir.add(testsSourceCodePath);
        binJavaDir.add(programClassesPath);
        binTestDir.add(testsClassesPath);

        flacocoConfig.setSrcJavaDir(srcJavaDir);
        flacocoConfig.setSrcTestDir(srcTestDir);
        flacocoConfig.setBinJavaDir(binJavaDir);
        flacocoConfig.setBinTestDir(binTestDir);

        if (dependencies != null) {
            flacocoConfig.setClasspath(dependencies);
        }

        flacocoConfig.setFamily(FlacocoConfig.FaultLocalizationFamily.SPECTRUM_BASED);
        flacocoConfig.setSpectrumFormula(SpectrumFormula.OCHIAI);
    }

    public FlacocoConfig getFlacocoConfig() {
        return flacocoConfig;
    }

    public List<FailureInfo> getFailuresInformation() {

        CustomFlacoco flacoco = new CustomFlacoco(flacocoConfig);

        flacocoResult = flacoco.run();

        List<FailureInfo> failureInfoList = new ArrayList<>();

        if (flacoco.getFailedTests() == null) {
            System.out.println("Process ended: no test cases threw an exception");
            return null;
        }

        flacoco.getFailedTests().forEach(item -> {
            failureInfoList.add(FailureAnalyzer.analyzeFailure(item,
                    item.testCaseName.substring(0, item.testCaseName.indexOf("#")),
                    item.testCaseName.substring(item.testCaseName.indexOf("#") + 1)));
        });

        return failureInfoList;
    }

    public FlacocoResult getFlacocoResult() {
        return flacocoResult;
    }
}
