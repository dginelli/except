package it.unimib;

import it.unimib.core.ExceptMain;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for ArrayIndexOutOfBoundsException
 */
public class ArrayIndexOutOfBoundsExceptionTest {

    @Test
    public void math98LocalizationTest() {
        String programName = "math_098_buggy";
        String programSourceCodePath = "./examples/math_098_buggy/src/java";
        String programClassesPath = "./examples/math_098_buggy/target/classes";
        String testsSourceCodePath = "./examples/math_098_buggy/src/test";
        String testsClassesPath = "./examples/math_098_buggy/target/test-classes";

        ExceptMain.startAnalysisWithOnlyLocalization(programName, programSourceCodePath, programClassesPath,
                testsSourceCodePath, testsClassesPath, null);
    }

    @Test
    public void lang12LocalizationTest() {
        String programName = "lang_012_buggy";
        String programSourceCodePath = "./examples/lang_012_buggy/src/main/java";
        String programClassesPath = "./examples/lang_012_buggy/target/classes";
        String testsSourceCodePath = "./examples/lang_012_buggy/src/test";
        String testsClassesPath = "./examples/lang_012_buggy/target/tests";
        String lib = "./examples/lang_012_buggy/lib";

        ExceptMain.startAnalysisWithOnlyLocalization(programName, programSourceCodePath, programClassesPath,
                testsSourceCodePath, testsClassesPath, lib);
    }

    @Test
    public void math98LocalizationAPITest() {
        String programSourceCodePath = "./examples/math_098_buggy/src/java";
        String programClassesPath = "./examples/math_098_buggy/target/classes";
        String testsSourceCodePath = "./examples/math_098_buggy/src/test";
        String testsClassesPath = "./examples/math_098_buggy/target/test-classes";

        assertNotNull(ExceptMain.startAnalysisWithLocalizationAPI(programSourceCodePath, programClassesPath,
                testsSourceCodePath, testsClassesPath, null));
    }
}
