package it.unimib.util;

import com.beust.jcommander.Parameter;

public class ExceptParametersCommander {
	
	@Parameter(names={"--sourceCodePath", "-s"}, required = true,
			description = "It specifies the path associated with the program source code path.")
	
	private String sourceCodePath;
	
	@Parameter(names={"--buildClassesPath", "-bc"}, required = true,
	description = "It specifies path associated with the compiled classes of the program.")
	
	private String buildClassesPath;

	@Parameter(names={"--sourceCodeTestsPath", "-st"}, required = true,
			description = "It specifies the path associated with the program source tests code path.")

	private String sourceCodeTestsPath;
	
	@Parameter(names={"--buildTestClassesPath", "-bt"}, required = true,
	description = "It specifies path associated with the compiled test classes of the program.")
	
	private String buildTestClassesPath;
	
	@Parameter(names={"--failingTestClass", "-ftc"},
	description = "It specifies the class that contains the failing test case to be executed.")
	
	private String failingTestClass;
	
	@Parameter(names={"--failingTestName", "-ftn"},
	description = "It specifies the name of the failing test case to be executed.")
	
	private String failingTestName;
	
	@Parameter(names={"--dependencies", "-d"},
	description = "It specifies the dependencies required by the program.")

	private String dependencies;
	
	@Parameter(names={"--outputDirectory", "-o"}, required = true, 
			description = "The directory where to save the results.")
	
	private String outputDirectory;
	
	@Parameter(names={"--sbflRanking", "-r"},
			description = "The file that contains the SBFL ranking values.")
	
	private String sbflRanking;
	
	@Parameter (names= {"--faultLocalizationType", "-flt"}, arity = 1,
			validateWith = FaultLocalizationTypeValidator.class,
			description = "The type of structure of SBFL file. Valid values: gzoltar, flacoco.")
	
	private String faultLocalizationType;
	
	@Parameter(names={"--help"}, help = true)
    private boolean help;

	public String getSourceCodePath() {
		return sourceCodePath;
	}

	public String getBuildClassesPath() {
		return buildClassesPath;
	}

	public String getSourceCodeTestsPath() {
		return sourceCodeTestsPath;
	}

	public String getBuildTestClassesPath() {
		return buildTestClassesPath;
	}

	public String getFailingTestClass() {
		return failingTestClass;
	}

	public String getFailingTestName() {
		return failingTestName;
	}

	public String getDependencies() {
		return dependencies;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}
	
	public String getSbflRanking() {
		return sbflRanking;
	}
	
	public String getFaultLocalizationType() {
		return faultLocalizationType;
	}

	public boolean isHelp() {
		return help;
	}
}
