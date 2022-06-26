package it.unimib;

import it.unimib.core.ExceptMain;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NullPointerExceptionTest {
    @Test
    public void lang33Test() {
        String programSourceCodePath = "./examples/lang_033_buggy/src/main/java";
        String programClassesPath = "./examples/lang_033_buggy/target/classes";
        String testsSourceCodePath = "./examples/lang_033_buggy/src/test/java";
        String testsClassesPath = "./examples/lang_033_buggy/target/test-classes";
        String lib = "./examples/lang_033_buggy/lib";

        assertNotNull(ExceptMain.startAnalysisWithLocalizationAPI(programSourceCodePath, programClassesPath,
                testsSourceCodePath, testsClassesPath, lib));
    }
}
